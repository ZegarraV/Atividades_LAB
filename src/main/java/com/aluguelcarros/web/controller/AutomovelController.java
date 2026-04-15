package com.aluguelcarros.web.controller;

import com.aluguelcarros.model.entity.Automovel;
import com.aluguelcarros.model.entity.Proprietario;
import com.aluguelcarros.model.exception.NegocioException;
import com.aluguelcarros.service.AutomovelService;
import com.aluguelcarros.web.dto.AutomovelForm;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.session.Session;
import io.micronaut.views.View;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Controller("/automoveis")
public class AutomovelController {

    private final AutomovelService automovelService;

    public AutomovelController(AutomovelService automovelService) {
        this.automovelService = automovelService;
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
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> cadastrar(Session session, @Body AutomovelForm form) {
        if (!"AGENTE".equals(session.get("usuarioTipo").orElse(""))) {
            return HttpResponse.seeOther(URI.create("/login"));
        }
        try {
            Proprietario proprietario = new Proprietario();
            proprietario.setTipo(form.getProprietarioTipo());
            proprietario.setReferenciaId(form.getProprietarioReferenciaId());

            Automovel automovel = new Automovel();
            automovel.setMatricula(form.getMatricula());
            automovel.setPlaca(form.getPlaca());
            automovel.setMarca(form.getMarca());
            automovel.setModelo(form.getModelo());
            automovel.setAno(form.getAno());
            automovel.setProprietario(proprietario);

            automovelService.cadastrar(automovel);
            return HttpResponse.seeOther(URI.create("/automoveis?msg=Automóvel+cadastrado+com+sucesso!"));
        } catch (NegocioException e) {
            return HttpResponse.seeOther(URI.create("/automoveis/novo?erro=" + encode(e.getMessage())));
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
