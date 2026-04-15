package com.aluguelcarros.web.controller;

import com.aluguelcarros.model.entity.Cliente;
import com.aluguelcarros.model.entity.PedidoImpl;
import com.aluguelcarros.model.entity.Rendimento;
import com.aluguelcarros.model.enums.PedidoStatus;
import com.aluguelcarros.model.exception.UsuarioException;
import com.aluguelcarros.service.ClienteService;
import com.aluguelcarros.service.PedidoService;
import com.aluguelcarros.web.dto.RendimentoForm;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.session.Session;
import io.micronaut.views.View;

import java.net.URI;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller("/cliente")
public class ClienteController {

    private final ClienteService clienteService;
    private final PedidoService  pedidoService;

    public ClienteController(ClienteService clienteService, PedidoService pedidoService) {
        this.clienteService = clienteService;
        this.pedidoService  = pedidoService;
    }

    @Get
    @View("cliente/dashboard")
    public HttpResponse<?> dashboard(Session session) {
        Long id = getUsuarioId(session);
        if (id == null) return HttpResponse.seeOther(URI.create("/login"));

        Cliente cliente = clienteService.buscarPorId(id);
        List<PedidoImpl> pedidos = pedidoService.listarPorCliente(id);
        long emAndamento = pedidos.stream()
            .filter(p -> p.getStatus() == PedidoStatus.ENVIADO || p.getStatus() == PedidoStatus.EM_ANALISE)
            .count();
        long aprovados = pedidos.stream()
            .filter(p -> p.getStatus() == PedidoStatus.APROVADO)
            .count();

        Map<String, Object> model = new HashMap<>();
        model.put("cliente", cliente);
        model.put("pedidos", pedidos);
        model.put("totalPedidos", pedidos.size());
        model.put("pedidosEmAndamento", emAndamento);
        model.put("pedidosAprovados", aprovados);
        model.put("totalRendimentos", clienteService.contarRendimentos(id));
        model.put("usuarioNome", session.get("usuarioNome").orElse(""));
        return HttpResponse.ok(model);
    }

    @Get("/perfil")
    @View("cliente/perfil")
    public HttpResponse<?> perfil(Session session,
                                  @QueryValue(defaultValue = "") String msg,
                                  @QueryValue(defaultValue = "") String erro) {
        Long id = getUsuarioId(session);
        if (id == null) return HttpResponse.seeOther(URI.create("/login"));

        Cliente cliente = clienteService.buscarPorId(id);
        List<Rendimento> rendimentos = clienteService.listarRendimentos(id);
        Map<String, Object> model = new HashMap<>();
        model.put("cliente",     cliente);
        model.put("rendimentos", rendimentos);
        model.put("totalRendimentos", rendimentos.size());
        model.put("usuarioNome", session.get("usuarioNome").orElse(""));
        if (!msg.isEmpty())  model.put("msg",  msg);
        if (!erro.isEmpty()) model.put("erro", erro);
        return HttpResponse.ok(model);
    }

    @Post("/rendimentos")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> adicionarRendimento(Session session, @Body RendimentoForm form) {
        Long id = getUsuarioId(session);
        if (id == null) return HttpResponse.seeOther(URI.create("/login"));

        try {
            Rendimento r = new Rendimento();
            r.setDescricao(form.getDescricao());
            r.setValor(form.getValor());
            clienteService.adicionarRendimento(id, r);
            return HttpResponse.seeOther(URI.create("/cliente/perfil?msg=Rendimento+adicionado+com+sucesso!"));
        } catch (UsuarioException e) {
            return HttpResponse.seeOther(URI.create("/cliente/perfil?erro=" + encode(e.getMessage())));
        }
    }

    @Post("/rendimentos/{rendimentoId}/remover")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> removerRendimento(Session session, Long rendimentoId) {
        Long id = getUsuarioId(session);
        if (id == null) return HttpResponse.seeOther(URI.create("/login"));

        clienteService.removerRendimento(id, rendimentoId);
        return HttpResponse.seeOther(URI.create("/cliente/perfil?msg=Rendimento+removido."));
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
