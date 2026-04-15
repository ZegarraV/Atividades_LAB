package com.aluguelcarros.service.facade;

import com.aluguelcarros.model.entity.Cliente;
import com.aluguelcarros.model.entity.PedidoImpl;
import com.aluguelcarros.model.entity.Rendimento;
import com.aluguelcarros.model.exception.UsuarioException;
import com.aluguelcarros.service.AuthService;
import com.aluguelcarros.service.ClienteService;
import com.aluguelcarros.service.PedidoService;
import jakarta.inject.Singleton;

import java.util.List;

/**
 * Fachada (Facade) que simplifica as operações orientadas ao {@link Cliente}.
 *
 * <p>Esta classe aplica o padrão <em>Facade</em>: agrupa múltiplas chamadas a
 * serviços distintos em uma única interface coesa para uso pelos controllers,
 * evitando que a camada web conheça detalhes de coordenação entre serviços.
 *
 * <p>Responsabilidades desta fachada:
 * <ul>
 *   <li>Cadastro e autenticação de clientes.</li>
 *   <li>Gerenciamento de rendimentos (com RN-CLI-01 delegada ao {@link ClienteService}).</li>
 *   <li>Criação e cancelamento de pedidos pelo cliente.</li>
 *   <li>Consulta de pedidos do cliente.</li>
 * </ul>
 */
@Singleton
public class ClienteFacade {

    private final ClienteService clienteService;
    private final PedidoService pedidoService;
    private final AuthService authService;

    public ClienteFacade(ClienteService clienteService,
                         PedidoService pedidoService,
                         AuthService authService) {
        this.clienteService = clienteService;
        this.pedidoService  = pedidoService;
        this.authService    = authService;
    }

    // ─────────────────────────────────────────────
    // Cadastro e Autenticação
    // ─────────────────────────────────────────────

    /**
     * Realiza o cadastro de um novo cliente e retorna o registro persistido.
     *
     * @param cliente dados do cliente (sem ID)
     * @return cliente cadastrado
     * @throws UsuarioException se CPF ou login já existirem
     */
    public Cliente cadastrar(Cliente cliente) {
        return clienteService.cadastrar(cliente);
    }

    /**
     * Realiza o login do cliente, retornando o objeto do usuário autenticado.
     *
     * @param login  login do cliente
     * @param senha  senha em texto puro (protótipo)
     * @return cliente autenticado
     * @throws UsuarioException se as credenciais forem inválidas
     */
    public Cliente login(String login, String senha) {
        return (Cliente) authService.autenticar(login, senha);
    }

    /**
     * Busca os dados de um cliente pelo ID.
     *
     * @param clienteId ID do cliente
     * @return cliente encontrado
     */
    public Cliente buscarCliente(Long clienteId) {
        return clienteService.buscarPorId(clienteId);
    }

    // ─────────────────────────────────────────────
    // Rendimentos (RN-CLI-01)
    // ─────────────────────────────────────────────

    /**
     * Adiciona um rendimento ao cliente, respeitando o limite de
     * {@link Cliente#MAX_RENDIMENTOS} rendimentos (RN-CLI-01).
     *
     * <p>A regra é validada pelo {@link ClienteService#adicionarRendimento(Long, Rendimento)},
     * que lança {@link UsuarioException} com mensagem detalhada em caso de violação.
     *
     * @param clienteId  ID do cliente
     * @param rendimento rendimento a adicionar
     * @return cliente atualizado
     * @throws UsuarioException se o limite de rendimentos for atingido (RN-CLI-01)
     */
    public Cliente adicionarRendimento(Long clienteId, Rendimento rendimento) {
        return clienteService.adicionarRendimento(clienteId, rendimento);
    }

    /**
     * Remove um rendimento do cliente.
     *
     * @param clienteId    ID do cliente
     * @param rendimentoId ID do rendimento a remover
     * @return cliente atualizado
     */
    public Cliente removerRendimento(Long clienteId, Long rendimentoId) {
        return clienteService.removerRendimento(clienteId, rendimentoId);
    }

    // ─────────────────────────────────────────────
    // Pedidos
    // ─────────────────────────────────────────────

    /**
     * Cria um novo pedido em RASCUNHO para o cliente com o automóvel selecionado.
     *
     * @param clienteId   ID do cliente
     * @param automovelId ID do automóvel desejado
     * @param pedido      dados do pedido (datas, valor diário)
     * @return pedido criado com status RASCUNHO
     */
    public PedidoImpl criarPedido(Long clienteId, Long automovelId, PedidoImpl pedido) {
        return pedidoService.criar(clienteId, automovelId, pedido);
    }

    /**
     * Submete um pedido em RASCUNHO para avaliação do agente (status → ENVIADO).
     *
     * @param pedidoId ID do pedido a enviar
     * @return pedido com status ENVIADO
     */
    public PedidoImpl enviarPedido(Long pedidoId) {
        return pedidoService.enviar(pedidoId);
    }

    /**
     * Cancela um pedido do cliente (status → CANCELADO).
     * Somente pedidos em RASCUNHO ou ENVIADO podem ser cancelados (RN-PED-02).
     *
     * @param clienteId ID do cliente solicitante
     * @param pedidoId  ID do pedido a cancelar
     * @return pedido cancelado
     */
    public PedidoImpl cancelarPedido(Long clienteId, Long pedidoId) {
        return pedidoService.cancelar(clienteId, pedidoId);
    }

    /**
     * Consulta todos os pedidos do cliente logado.
     *
     * @param clienteId ID do cliente
     * @return lista de pedidos do cliente
     */
    public List<PedidoImpl> consultarMeusPedidos(Long clienteId) {
        return pedidoService.listarPorCliente(clienteId);
    }
}
