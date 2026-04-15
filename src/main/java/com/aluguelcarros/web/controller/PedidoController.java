package com.aluguelcarros.web.controller;

import com.aluguelcarros.model.entity.PedidoImpl;
import com.aluguelcarros.model.exception.PedidoException;
import com.aluguelcarros.service.AutomovelService;
import com.aluguelcarros.service.PedidoService;
import com.aluguelcarros.web.dto.PedidoForm;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.session.Session;
import io.micronaut.views.View;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Controller("/pedidos")
public class PedidoController {

    private final PedidoService    pedidoService;
    private final AutomovelService automovelService;

    public PedidoController(PedidoService pedidoService, AutomovelService automovelService) {
        this.pedidoService    = pedidoService;
        this.automovelService = automovelService;
    }

    @Get("/novo")
    @View("pedidos/novo")
    public HttpResponse<?> novoForm(Session session,
                                    @QueryValue(defaultValue = "") String erro) {
        Long id = getUsuarioId(session);
        if (id == null) return HttpResponse.seeOther(URI.create("/login"));

        Map<String, Object> model = new HashMap<>();
        model.put("automoveis",  automovelService.listarDisponiveis());
        model.put("usuarioNome", session.get("usuarioNome").orElse(""));
        if (!erro.isEmpty()) model.put("erro", erro);
        return HttpResponse.ok(model);
    }

    @Get("/novo-pedido")
    @View("pedidos/novo")
    public HttpResponse<?> novoFormSemCacheAntigo(Session session,
                                                  @QueryValue(defaultValue = "") String erro) {
        return novoForm(session, erro);
    }

    @Post
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> criar(Session session, @Body PedidoForm form) {
        Long clienteId = getUsuarioId(session);
        if (clienteId == null) return HttpResponse.seeOther(URI.create("/login"));

        try {
            PedidoImpl pedido = new PedidoImpl();
            pedido.setDataInicio(form.getDataInicio());
            pedido.setDataFim(form.getDataFim());
            pedido.setValor(form.getValorDiario());

            pedidoService.criar(clienteId, form.getAutomovelId(), pedido);
            return HttpResponse.seeOther(URI.create("/cliente?msg=Pedido+criado+com+sucesso!"));
        } catch (PedidoException e) {
            return HttpResponse.seeOther(URI.create("/pedidos/novo-pedido?erro=" + encode(e.getMessage())));
        }
    }

    @Post("/{pedidoId}/enviar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> enviar(Session session, Long pedidoId) {
        if (session.get("usuarioId").isEmpty()) return HttpResponse.seeOther(URI.create("/login"));

        try {
            pedidoService.enviar(pedidoId);
            return HttpResponse.seeOther(URI.create("/cliente?msg=Pedido+enviado+para+análise!"));
        } catch (PedidoException e) {
            return HttpResponse.seeOther(URI.create("/cliente?erro=" + encode(e.getMessage())));
        }
    }

    @Post("/{pedidoId}/cancelar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> cancelar(Session session, Long pedidoId) {
        Long clienteId = getUsuarioId(session);
        if (clienteId == null) return HttpResponse.seeOther(URI.create("/login"));

        try {
            pedidoService.cancelar(pedidoId, clienteId);
            return HttpResponse.seeOther(URI.create("/cliente?msg=Pedido+cancelado."));
        } catch (PedidoException e) {
            return HttpResponse.seeOther(URI.create("/cliente?erro=" + encode(e.getMessage())));
        }
    }

    private String encode(String s) {
        return s.replace(" ", "+").replace("&", "e");
    }

    private Long getUsuarioId(Session session) {
        Object raw = session.get("usuarioId").orElse(null);
        if (raw == null) return null;
        if (raw instanceof Number n) return n.longValue();
        if (raw instanceof String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
