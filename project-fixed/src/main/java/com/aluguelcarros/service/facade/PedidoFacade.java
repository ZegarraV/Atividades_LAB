package com.aluguelcarros.service.facade;

import com.aluguelcarros.model.entity.Automovel;
import com.aluguelcarros.model.entity.Contrato;
import com.aluguelcarros.model.entity.PedidoImpl;
import com.aluguelcarros.model.enums.PedidoStatus;
import com.aluguelcarros.service.AutomovelService;
import com.aluguelcarros.service.PedidoService;
import jakarta.inject.Singleton;

import java.util.List;

/**
 * Fachada (Facade) que simplifica as operações orientadas ao fluxo de análise
 * de pedidos pelo {@link com.aluguelcarros.model.entity.Agente}.
 *
 * <p>Responsabilidades desta fachada:
 * <ul>
 *   <li>Apresentar a fila de pedidos aguardando análise (status ENVIADO).</li>
 *   <li>Coordenar as transições de status do pedido (análise, aprovação, reprovação).</li>
 *   <li>Encapsular a lógica de geração de contrato após aprovação.</li>
 *   <li>Gerenciar a disponibilidade dos automóveis em conjunto com o ciclo do pedido.</li>
 * </ul>
 */
@Singleton
public class PedidoFacade {

    private final PedidoService pedidoService;
    private final AutomovelService automovelService;

    public PedidoFacade(PedidoService pedidoService,
                        AutomovelService automovelService) {
        this.pedidoService    = pedidoService;
        this.automovelService = automovelService;
    }

    // ─────────────────────────────────────────────
    // Fila de análise
    // ─────────────────────────────────────────────

    /**
     * Retorna a fila de pedidos aguardando análise (status ENVIADO).
     * Utilizado pelo agente para visualizar os pedidos pendentes.
     *
     * @return lista de pedidos com automóvel já carregado (sem N+1 queries)
     */
    public List<PedidoImpl> listarFilaDeAnalise() {
        return pedidoService.listarPorStatus(PedidoStatus.ENVIADO);
    }

    /**
     * Retorna os pedidos atualmente em análise (status EM_ANALISE).
     *
     * @return lista de pedidos em análise
     */
    public List<PedidoImpl> listarEmAnalise() {
        return pedidoService.listarPorStatus(PedidoStatus.EM_ANALISE);
    }

    /**
     * Busca os dados completos de um pedido pelo ID.
     *
     * @param pedidoId ID do pedido
     * @return pedido encontrado
     */
    public PedidoImpl buscarPedido(Long pedidoId) {
        return pedidoService.buscarPorId(pedidoId);
    }

    // ─────────────────────────────────────────────
    // Transições de status pelo Agente
    // ─────────────────────────────────────────────

    /**
     * Inicia a análise de um pedido, transitando seu status de ENVIADO para EM_ANALISE.
     *
     * @param pedidoId ID do pedido a analisar
     * @return pedido com status EM_ANALISE
     */
    public PedidoImpl iniciarAnalise(Long pedidoId) {
        return pedidoService.iniciarAnalise(pedidoId);
    }

    /**
     * Aprova um pedido em análise: persiste a aprovação, reserva o automóvel
     * e gera o contrato. Encapsula a coordenação entre {@link PedidoService}
     * e {@link AutomovelService}.
     *
     * @param pedidoId   ID do pedido a aprovar
     * @param tipoAgente tipo do agente aprovador ("BANCO" ou "EMPRESA")
     * @return contrato gerado e persistido
     */
    public Contrato aprovar(Long pedidoId, String tipoAgente) {
        return pedidoService.aprovar(pedidoId, tipoAgente);
    }

    /**
     * Reprova um pedido em análise (status → REPROVADO).
     *
     * @param pedidoId ID do pedido a reprovar
     * @return pedido com status REPROVADO
     */
    public PedidoImpl reprovar(Long pedidoId) {
        return pedidoService.reprovar(pedidoId);
    }

    // ─────────────────────────────────────────────
    // Automóveis
    // ─────────────────────────────────────────────

    /**
     * Lista os automóveis disponíveis para seleção em novos pedidos.
     * Exposto pela fachada para que o controller de pedidos não dependa
     * diretamente do {@link AutomovelService}.
     *
     * @return lista de automóveis disponíveis
     */
    public List<Automovel> listarAutomoveisDisponiveis() {
        return automovelService.listarDisponiveis();
    }

    /**
     * Registra a devolução de um automóvel, tornando-o disponível novamente.
     * Chamado após o fim do período de aluguel ou após cancelamento.
     *
     * @param automovelId ID do automóvel devolvido
     * @return automóvel com {@code disponivel = true}
     */
    public Automovel registrarDevolucao(Long automovelId) {
        return automovelService.disponibilizar(automovelId);
    }
}
