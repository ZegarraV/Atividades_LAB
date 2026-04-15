package com.aluguelcarros.web.controller;

import com.aluguelcarros.model.entity.Automovel;
import com.aluguelcarros.model.exception.PedidoException;
import com.aluguelcarros.service.AutomovelService;
import com.aluguelcarros.service.facade.PedidoFacade;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.session.Session;
import io.micronaut.views.View;

import java.net.URI;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller("/agente")
public class AgenteController {

    private final AutomovelService automovelService;
    private final PedidoFacade     pedidoFacade;

    public AgenteController(AutomovelService automovelService,
                            PedidoFacade pedidoFacade) {
        this.automovelService = automovelService;
        this.pedidoFacade     = pedidoFacade;
    }

    @Get
    @View("agente/dashboard")
    public HttpResponse<?> dashboard(Session session,
                                     @QueryValue(defaultValue = "") String msg,
                                     @QueryValue(defaultValue = "") String erro) {
        if (!"AGENTE".equals(session.get("usuarioTipo").orElse(""))) {
            return HttpResponse.seeOther(URI.create("/login"));
        }
        List<Automovel> automoveis = automovelService.listarTodos();
        long automoveisDisponiveis = automoveis.stream()
            .filter(Automovel::isDisponivel)
            .count();

        Map<String, Object> model = new HashMap<>();
        model.put("pedidosEnviados",   pedidoFacade.listarFilaDeAnalise());
        model.put("pedidosEmAnalise",  pedidoFacade.listarEmAnalise());
        model.put("limiteRendimentosAnalise", 3);
        model.put("automoveis",        automoveis);
        model.put("automoveisDisponiveis", automoveisDisponiveis);
        model.put("usuarioNome",       session.get("usuarioNome").orElse(""));
        if (!msg.isEmpty())  model.put("msg",  msg);
        if (!erro.isEmpty()) model.put("erro", erro);
        return HttpResponse.ok(model);
    }

    @Post("/pedidos/{pedidoId}/analisar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> iniciarAnalise(Session session, Long pedidoId) {
        if (!"AGENTE".equals(session.get("usuarioTipo").orElse(""))) {
            return HttpResponse.seeOther(URI.create("/login"));
        }
        try {
            pedidoFacade.iniciarAnalise(pedidoId);
            return HttpResponse.seeOther(URI.create("/agente?msg=An%C3%A1lise+iniciada."));
        } catch (PedidoException e) {
            return HttpResponse.seeOther(URI.create("/agente?erro=" + encode(e.getMessage())));
        }
    }

    @Post("/pedidos/{pedidoId}/aprovar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> aprovar(Session session, Long pedidoId) {
        if (!"AGENTE".equals(session.get("usuarioTipo").orElse(""))) {
            return HttpResponse.seeOther(URI.create("/login"));
        }
        try {
            String tipoAgente = String.valueOf(session.get("agenteTipo").orElse("BANCO"));
            pedidoFacade.aprovar(pedidoId, tipoAgente);
            return HttpResponse.seeOther(URI.create("/agente?msg=Pedido+aprovado+e+contrato+gerado!"));
        } catch (PedidoException e) {
            return HttpResponse.seeOther(URI.create("/agente?erro=" + encode(e.getMessage())));
        }
    }

    @Post("/pedidos/{pedidoId}/reprovar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> reprovar(Session session, Long pedidoId) {
        if (!"AGENTE".equals(session.get("usuarioTipo").orElse(""))) {
            return HttpResponse.seeOther(URI.create("/login"));
        }
        try {
            pedidoFacade.reprovar(pedidoId);
            return HttpResponse.seeOther(URI.create("/agente?msg=Pedido+reprovado."));
        } catch (PedidoException e) {
            return HttpResponse.seeOther(URI.create("/agente?erro=" + encode(e.getMessage())));
        }
    }

    private String encode(String s) {
        return s.replace(" ", "+").replace("&", "e");
    }
}
