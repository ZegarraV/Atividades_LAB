package com.aluguelcarros.service;

import com.aluguelcarros.model.entity.Automovel;
import com.aluguelcarros.model.entity.Cliente;
import com.aluguelcarros.model.entity.Contrato;
import com.aluguelcarros.model.entity.PedidoImpl;
import com.aluguelcarros.model.enums.PedidoStatus;
import com.aluguelcarros.model.exception.PedidoException;
import com.aluguelcarros.model.exception.UsuarioException;
import com.aluguelcarros.repository.AutomovelRepository;
import com.aluguelcarros.repository.ClienteRepository;
import com.aluguelcarros.repository.ContratoRepository;
import com.aluguelcarros.repository.PedidoRepository;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/**
 * Serviço de domínio para operações relacionadas ao ciclo de vida de
 * {@link PedidoImpl}.
 *
 * <p>Regras de negócio aplicadas:
 * <ul>
 *   <li><strong>RN-PED-01:</strong> Criação de pedido requer automóvel disponível.</li>
 *   <li><strong>RN-PED-02:</strong> Somente pedidos em RASCUNHO ou ENVIADO podem
 *       ser cancelados pelo cliente.</li>
 *   <li><strong>RN-PED-03:</strong> O fluxo de status é unidirecional:
 *       RASCUNHO → ENVIADO → EM_ANALISE → APROVADO/REPROVADO.</li>
 *   <li><strong>RN-PED-04:</strong> A aprovação reserva o automóvel e gera contrato.</li>
 *   <li><strong>RN-PED-05:</strong> Não é possível gerar dois contratos para o mesmo
 *       pedido.</li>
 * </ul>
 */
@Singleton
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final AutomovelRepository automovelRepository;
    private final ContratoRepository contratoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         AutomovelRepository automovelRepository,
                         ContratoRepository contratoRepository) {
        this.pedidoRepository    = pedidoRepository;
        this.clienteRepository   = clienteRepository;
        this.automovelRepository = automovelRepository;
        this.contratoRepository  = contratoRepository;
    }

    // ─────────────────────────────────────────────
    // Operações do Cliente
    // ─────────────────────────────────────────────

    /**
     * Cria um pedido no status RASCUNHO e associa ao cliente e ao automóvel.
     *
     * @param clienteId   ID do cliente que está criando o pedido
     * @param automovelId ID do automóvel desejado
     * @param pedido      dados do pedido (datas, valor diário)
     * @return pedido salvo com status RASCUNHO
     * @throws UsuarioException se o cliente não for encontrado
     * @throws PedidoException  se o automóvel não estiver disponível (RN-PED-01)
     */
    @Transactional
    public PedidoImpl criar(Long clienteId, Long automovelId, PedidoImpl pedido) {
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new UsuarioException("Cliente não encontrado: " + clienteId));

        Automovel automovel = automovelRepository.findById(automovelId)
            .orElseThrow(() -> new PedidoException("Automóvel não encontrado: " + automovelId));

        if (!automovel.isDisponivel()) {
            throw new PedidoException(
                "O automóvel de placa '" + automovel.getPlaca() + "' não está disponível.");
        }

        // Delega à entidade de domínio: associa cliente e define status RASCUNHO
        cliente.cadastrarPedido(pedido);
        pedido.setAutomovel(automovel);

        return pedidoRepository.save(pedido);
    }

    /**
     * Submete um pedido em RASCUNHO para análise, transitando para ENVIADO.
     *
     * @param pedidoId ID do pedido
     * @return pedido atualizado
     * @throws PedidoException se o pedido não estiver em RASCUNHO (RN-PED-03)
     */
    @Transactional
    public PedidoImpl enviar(Long pedidoId) {
        PedidoImpl pedido = buscarPorId(pedidoId);
        if (pedido.getStatus() != PedidoStatus.RASCUNHO) {
            throw new PedidoException(
                "Somente pedidos em RASCUNHO podem ser enviados. Status atual: "
                + pedido.getStatus());
        }
        pedido.atualizarStatus(PedidoStatus.ENVIADO);
        return pedidoRepository.update(pedido);
    }

    /**
     * Cancela um pedido, devolvendo o automóvel ao pool de disponíveis se necessário.
     * Delega a validação de status à entidade {@link Cliente#cancelarPedido(PedidoImpl)}.
     *
     * @param clienteId ID do cliente solicitante (validação de propriedade)
     * @param pedidoId  ID do pedido a cancelar
     * @return pedido atualizado com status CANCELADO
     * @throws PedidoException  se o pedido não pertencer ao cliente ou status inválido
     * @throws UsuarioException se o cliente não existir
     */
    @Transactional
    public PedidoImpl cancelar(Long clienteId, Long pedidoId) {
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new UsuarioException("Cliente não encontrado: " + clienteId));

        PedidoImpl pedido = buscarPorId(pedidoId);

        if (!pedido.getCliente().getId().equals(clienteId)) {
            throw new PedidoException("O pedido " + pedidoId
                + " não pertence ao cliente " + clienteId + ".");
        }

        // Delega regra RN-PED-02 à entidade de domínio
        cliente.cancelarPedido(pedido);

        return pedidoRepository.update(pedido);
    }

    // ─────────────────────────────────────────────
    // Operações do Agente
    // ─────────────────────────────────────────────

    /**
     * Inicia a análise de um pedido, transitando de ENVIADO para EM_ANALISE.
     *
     * @param pedidoId ID do pedido a analisar
     * @return pedido com status EM_ANALISE
     * @throws PedidoException se o pedido não estiver em ENVIADO
     */
    @Transactional
    public PedidoImpl iniciarAnalise(Long pedidoId) {
        PedidoImpl pedido = buscarPorId(pedidoId);
        if (pedido.getStatus() != PedidoStatus.ENVIADO) {
            throw new PedidoException(
                "Somente pedidos ENVIADOS podem ser analisados. Status atual: "
                + pedido.getStatus());
        }
        pedido.atualizarStatus(PedidoStatus.EM_ANALISE);
        return pedidoRepository.update(pedido);
    }

    /**
     * Aprova um pedido em análise: reserva o automóvel, gera e persiste o contrato.
     *
     * @param pedidoId   ID do pedido a aprovar
     * @param tipoAgente tipo do agente aprovador ("BANCO" ou "EMPRESA")
     * @return contrato gerado
     * @throws PedidoException se o pedido não estiver em EM_ANALISE (RN-PED-03)
     *                         ou se já existir contrato para este pedido (RN-PED-05)
     */
    @Transactional
    public Contrato aprovar(Long pedidoId, String tipoAgente) {
        if (contratoRepository.existsByPedidoId(pedidoId)) {
            throw new PedidoException(
                "Já existe um contrato gerado para o pedido " + pedidoId + " (RN-PED-05).");
        }

        PedidoImpl pedido = buscarPorId(pedidoId);
        if (pedido.getStatus() != PedidoStatus.EM_ANALISE) {
            throw new PedidoException(
                "Somente pedidos EM_ANALISE podem ser aprovados. Status atual: "
                + pedido.getStatus());
        }

        pedido.atualizarStatus(PedidoStatus.APROVADO);

        // RN-PED-04: reserva o automóvel
        Automovel automovel = pedido.getAutomovel();
        automovel.reservar();
        automovelRepository.update(automovel);

        // Gera e persiste o contrato
        Contrato contrato = new Contrato();
        contrato.setTipo(tipoAgente);
        contrato.vincularPedido(pedido);
        contrato.gerarContrato();
        pedidoRepository.update(pedido);
        return contratoRepository.save(contrato);
    }

    /**
     * Reprova um pedido em análise.
     *
     * @param pedidoId ID do pedido a reprovar
     * @return pedido com status REPROVADO
     * @throws PedidoException se o pedido não estiver em EM_ANALISE
     */
    @Transactional
    public PedidoImpl reprovar(Long pedidoId) {
        PedidoImpl pedido = buscarPorId(pedidoId);
        if (pedido.getStatus() != PedidoStatus.EM_ANALISE) {
            throw new PedidoException(
                "Somente pedidos EM_ANALISE podem ser reprovados. Status atual: "
                + pedido.getStatus());
        }
        pedido.atualizarStatus(PedidoStatus.REPROVADO);
        return pedidoRepository.update(pedido);
    }

    // ─────────────────────────────────────────────
    // Consultas
    // ─────────────────────────────────────────────

    /**
     * Busca um pedido pelo ID.
     *
     * @param id ID do pedido
     * @return pedido encontrado
     * @throws PedidoException se o pedido não existir
     */
    @Transactional(readOnly = true)
    public PedidoImpl buscarPorId(Long id) {
        return pedidoRepository.findById(id)
            .orElseThrow(() -> new PedidoException("Pedido não encontrado para o ID: " + id));
    }

    /**
     * Lista todos os pedidos de um cliente.
     *
     * @param clienteId ID do cliente
     * @return lista de pedidos do cliente
     */
    @Transactional(readOnly = true)
    public List<PedidoImpl> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    /**
     * Lista pedidos filtrados por status. Usado pelo agente para ver a fila
     * de pedidos ENVIADOS aguardando análise.
     *
     * @param status status a filtrar
     * @return lista de pedidos com o status informado
     */
    @Transactional(readOnly = true)
    public List<PedidoImpl> listarPorStatus(PedidoStatus status) {
        return pedidoRepository.findByStatusWithAutomovel(status);
    }
}
