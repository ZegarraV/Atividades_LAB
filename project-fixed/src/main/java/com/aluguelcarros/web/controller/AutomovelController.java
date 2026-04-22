package com.aluguelcarros.web.controller;

import com.aluguelcarros.model.entity.Automovel;
import com.aluguelcarros.model.entity.Proprietario;
import com.aluguelcarros.model.exception.NegocioException;
import com.aluguelcarros.service.AutomovelService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.session.Session;
import io.micronaut.views.View;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller("/automoveis")
public class AutomovelController {

    private final AutomovelService automovelService;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";
    private static final String UPLOAD_URL_PATH = "/static/uploads/";

    public AutomovelController(AutomovelService automovelService) {
        this.automovelService = automovelService;
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException ignored) {}
    }

    @Get
    @View("automoveis/lista")
    public HttpResponse<?> lista(Session session,
                                 @QueryValue(defaultValue = "") String msg,
                                 @QueryValue(defaultValue = "") String erro) {
        if (session.get("usuarioId").isEmpty()) return HttpResponse.seeOther(URI.create("/login"));

        Map<String, Object> model = new HashMap<>();
        model.put("automoveis",  automovelService.listarTodos());
        model.put("usuarioNome", session.get("usuarioNome").orElse(""));
        model.put("usuarioTipo", session.get("usuarioTipo").orElse(""));
        if (!msg.isEmpty())  model.put("msg",  msg);
        if (!erro.isEmpty()) model.put("erro", erro);
        return HttpResponse.ok(model);
    }

    @Get("/novo")
    @View("automoveis/novo")
    public HttpResponse<?> novoForm(Session session,
                                    @QueryValue(defaultValue = "") String erro) {
        if (!"AGENTE".equals(session.get("usuarioTipo").orElse(""))) {
            return HttpResponse.seeOther(URI.create("/login"));
        }
        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNome", session.get("usuarioNome").orElse(""));
        if (!erro.isEmpty()) model.put("erro", erro);
        return HttpResponse.ok(model);
    }

    @Post
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<?> cadastrar(Session session,
                                     @Part("matricula") String matricula,
                                     @Part("placa") String placa,
                                     @Part("marca") String marca,
                                     @Part("modelo") String modelo,
                                     @Part("ano") String anoStr,
                                     @Part("proprietarioTipo") String proprietarioTipo,
                                     @Part("proprietarioReferenciaId") String proprietarioReferenciaIdStr,
                                     @Part("imagem") CompletedFileUpload imagem) {
        if (!"AGENTE".equals(session.get("usuarioTipo").orElse(""))) {
            return HttpResponse.seeOther(URI.create("/login"));
        }
        try {
            int ano = Integer.parseInt(anoStr.trim());
            long proprietarioReferenciaId = Long.parseLong(proprietarioReferenciaIdStr.trim());

            Proprietario proprietario = new Proprietario();
            proprietario.setTipo(proprietarioTipo);
            proprietario.setReferenciaId(proprietarioReferenciaId);

            Automovel automovel = new Automovel();
            automovel.setMatricula(matricula);
            automovel.setPlaca(placa);
            automovel.setMarca(marca);
            automovel.setModelo(modelo);
            automovel.setAno(ano);
            automovel.setProprietario(proprietario);

            if (imagem != null && imagem.getFilename() != null && !imagem.getFilename().isBlank()
                    && imagem.getSize() > 0) {
                String originalFilename = imagem.getFilename();
                String ext = originalFilename.contains(".")
                        ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                        : ".jpg";
                String savedName = UUID.randomUUID() + ext;
                Path dest = Paths.get(UPLOAD_DIR + savedName);
                Files.createDirectories(dest.getParent());
                Files.write(dest, imagem.getBytes());
                automovel.setImagemUrl(UPLOAD_URL_PATH + savedName);
            }

            automovelService.cadastrar(automovel);
            return HttpResponse.seeOther(URI.create("/automoveis?msg=Automóvel+cadastrado+com+sucesso!"));
        } catch (NegocioException e) {
            return HttpResponse.seeOther(URI.create("/automoveis/novo?erro=" + encode(e.getMessage())));
        } catch (Exception e) {
            return HttpResponse.seeOther(URI.create("/automoveis/novo?erro=" + encode("Erro ao cadastrar: " + e.getMessage())));
        }
    }

    @Post("/{id}/disponibilizar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> disponibilizar(Session session, Long id) {
        if (!"AGENTE".equals(session.get("usuarioTipo").orElse(""))) {
            return HttpResponse.seeOther(URI.create("/login"));
        }
        automovelService.disponibilizar(id);
        return HttpResponse.seeOther(URI.create("/automoveis?msg=Automóvel+disponibilizado."));
    }

    private String encode(String s) {
        return s.replace(" ", "+").replace("&", "e");
    }
}
