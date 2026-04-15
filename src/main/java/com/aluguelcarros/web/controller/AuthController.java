package com.aluguelcarros.web.controller;

import com.aluguelcarros.model.entity.Agente;
import com.aluguelcarros.model.entity.Cliente;
import com.aluguelcarros.model.entity.Usuario;
import com.aluguelcarros.model.exception.UsuarioException;
import com.aluguelcarros.service.AuthService;
import com.aluguelcarros.service.ClienteService;
import com.aluguelcarros.web.dto.ClienteForm;
import com.aluguelcarros.web.dto.LoginForm;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.session.Session;
import io.micronaut.views.View;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    private final AuthService authService;
    private final ClienteService clienteService;

    public AuthController(AuthService authService, ClienteService clienteService) {
        this.authService    = authService;
        this.clienteService = clienteService;
    }

    @Get("/login")
    @View("auth/login")
    public Map<String, Object> loginPage(@QueryValue(defaultValue = "") String erro,
                                         @QueryValue(defaultValue = "") String msg) {
        Map<String, Object> model = new HashMap<>();
        if (!erro.isEmpty()) model.put("erro", erro);
        if (!msg.isEmpty())  model.put("msg", msg);
        return model;
    }

    @Post("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> login(@Body LoginForm form, Session session) {
        try {
            Usuario usuario = authService.autenticar(form.getLogin(), form.getSenha());
            session.put("usuarioId",   usuario.getId());
            session.put("usuarioNome", getNome(usuario));
            session.put("usuarioTipo", usuario instanceof Cliente ? "CLIENTE" : "AGENTE");

            if (usuario instanceof Agente agente) {
                session.put("agenteTipo", agente.getTipo());
            }

            if (usuario instanceof Agente) {
                return HttpResponse.seeOther(URI.create("/agente"));
            }
            return HttpResponse.seeOther(URI.create("/cliente"));
        } catch (UsuarioException e) {
            return HttpResponse.seeOther(URI.create("/login?erro=" + encode(e.getMessage())));
        }
    }

    @Get("/cadastro")
    @View("auth/cadastro")
    public Map<String, Object> cadastroPage(@QueryValue(defaultValue = "") String erro) {
        Map<String, Object> model = new HashMap<>();
        if (!erro.isEmpty()) model.put("erro", erro);
        return model;
    }

    @Post("/cadastro")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> cadastrar(@Body ClienteForm form) {
        try {
            Cliente cliente = new Cliente();
            cliente.setNome(form.getNome());
            cliente.setCpf(form.getCpf());
            cliente.setRg(form.getRg());
            cliente.setProfissao(form.getProfissao());
            cliente.setEndereco(form.getEndereco());
            cliente.setLogin(form.getLogin());
            cliente.setSenha(form.getSenha());
            cliente.setPerfil("CLIENTE");

            clienteService.cadastrar(cliente);
            return HttpResponse.seeOther(URI.create("/login?msg=Cadastro+realizado+com+sucesso!+Fa%C3%A7a+seu+login."));
        } catch (UsuarioException e) {
            return HttpResponse.seeOther(URI.create("/cadastro?erro=" + encode(e.getMessage())));
        } catch (RuntimeException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage();
            if (msg.contains("usuarios(login") || msg.contains("UNIQUE") || msg.contains("23505")) {
                return HttpResponse.seeOther(URI.create("/cadastro?erro=O+login+informado+j%C3%A1+est%C3%A1+em+uso."));
            }
            return HttpResponse.seeOther(URI.create("/cadastro?erro=Erro+ao+realizar+cadastro.+Tente+novamente."));
        }
    }

    @Post("/logout")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> logout(Session session) {
        session.remove("usuarioId");
        session.remove("usuarioNome");
        session.remove("usuarioTipo");
        session.remove("agenteTipo");
        return HttpResponse.seeOther(URI.create("/login?msg=Voc%C3%AA+saiu+do+sistema."));
    }

    private String getNome(Usuario u) {
        if (u instanceof Cliente c) return c.getNome();
        if (u instanceof Agente  a) return a.getNome();
        return u.getLogin();
    }

    private String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
