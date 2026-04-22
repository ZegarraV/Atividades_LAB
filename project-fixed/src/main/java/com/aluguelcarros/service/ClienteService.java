package com.aluguelcarros.service;

import com.aluguelcarros.model.entity.Cliente;
import com.aluguelcarros.model.entity.Rendimento;
import com.aluguelcarros.model.exception.UsuarioException;
import com.aluguelcarros.repository.AgenteRepository;
import com.aluguelcarros.repository.ClienteRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/**
 * Serviço de domínio para operações relacionadas a {@link Cliente}.
 *
 * <p>Concentra as regras de negócio da entidade Cliente:
 * <ul>
 *   <li><strong>RN-CLI-01:</strong> Um cliente não pode ter mais de
 *       {@link Cliente#MAX_RENDIMENTOS} rendimentos cadastrados.</li>
 *   <li><strong>RN-CLI-02:</strong> CPF deve ser único no sistema.</li>
 *   <li><strong>RN-CLI-03:</strong> Login deve ser único no sistema.</li>
 * </ul>
 *
 * <p>A validação do limite de rendimentos é aplicada em dois níveis:
 * <ol>
 *   <li>Na entidade de domínio: {@link Cliente#adicionarRendimento(Rendimento)} — garante
 *       consistência mesmo quando a entidade é manipulada fora do serviço.</li>
 *   <li>Neste serviço: {@link #adicionarRendimento(Long, Rendimento)} — valida
 *       diretamente no banco via {@code countRendimentos}, evitando condições de
 *       corrida em leituras com lazy loading desativado.</li>
 * </ol>
 */
@Singleton
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AgenteRepository agenteRepository;

    public ClienteService(ClienteRepository clienteRepository,
                          AgenteRepository agenteRepository) {
        this.clienteRepository = clienteRepository;
        this.agenteRepository = agenteRepository;
    }

    // ─────────────────────────────────────────────
    // CRUD básico
    // ─────────────────────────────────────────────

    /**
     * Cadastra um novo cliente no sistema, aplicando validações de unicidade.
     *
     * @param cliente entidade preenchida (sem ID)
     * @return cliente persistido com ID gerado
     * @throws UsuarioException se CPF ou login já estiverem em uso
     */
    @Transactional
    public Cliente cadastrar(Cliente cliente) {
        String login = cliente.getLogin() == null ? "" : cliente.getLogin().trim();
        if (login.isEmpty()) {
            throw new UsuarioException("Login é obrigatório.");
        }
        cliente.setLogin(login);

        if (cliente.getPerfil() == null || cliente.getPerfil().isBlank()) {
            cliente.setPerfil("CLIENTE");
        }
        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new UsuarioException(
                "Já existe um cliente cadastrado com o CPF: " + cliente.getCpf());
        }
        if (clienteRepository.findByLogin(login).isPresent()
            || agenteRepository.findByLogin(login).isPresent()) {
            throw new UsuarioException(
                "O login '" + login + "' já está em uso.");
        }
        return clienteRepository.save(cliente);
    }

    /**
     * Retorna um cliente pelo ID.
     *
     * @param id ID do cliente
     * @return cliente encontrado
     * @throws UsuarioException se o cliente não existir
     */
    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new UsuarioException("Cliente não encontrado para o ID: " + id));
    }

    /**
     * Retorna um cliente pelo CPF.
     *
     * @param cpf CPF do cliente
     * @return cliente encontrado
     * @throws UsuarioException se o cliente não existir
     */
    @Transactional(readOnly = true)
    public Cliente buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf)
            .orElseThrow(() -> new UsuarioException("Cliente não encontrado para o CPF: " + cpf));
    }

    /**
     * Lista todos os clientes de forma paginada.
     *
     * @param pageable configuração de paginação e ordenação
     * @return página de clientes
     */
    @Transactional(readOnly = true)
    public Page<Cliente> listar(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    /**
     * Busca clientes cujo nome contenha o trecho informado (case-insensitive).
     *
     * @param nome trecho do nome para pesquisa
     * @return lista de clientes correspondentes
     */
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainsIgnoreCase(nome);
    }

    /**
     * Atualiza os dados cadastrais de um cliente existente.
     *
     * @param id      ID do cliente a atualizar
     * @param dados   objeto com os novos dados (o ID do parâmetro prevalece)
     * @return cliente atualizado
     * @throws UsuarioException se o cliente não existir
     */
    @Transactional
    public Cliente atualizar(Long id, Cliente dados) {
        Cliente existente = buscarPorId(id);
        existente.setNome(dados.getNome());
        existente.setEndereco(dados.getEndereco());
        existente.setProfissao(dados.getProfissao());
        existente.setRg(dados.getRg());
        return clienteRepository.update(existente);
    }

    /**
     * Remove um cliente pelo ID.
     *
     * @param id ID do cliente a remover
     * @throws UsuarioException se o cliente não existir
     */
    @Transactional
    public void remover(Long id) {
        Cliente cliente = buscarPorId(id);
        clienteRepository.delete(cliente);
    }

    // ─────────────────────────────────────────────
    // Regra de negócio: Rendimentos (RN-CLI-01)
    // ─────────────────────────────────────────────

    /**
     * Adiciona um rendimento a um cliente, garantindo que o limite máximo de
     * {@link Cliente#MAX_RENDIMENTOS} rendimentos não seja ultrapassado.
     *
     * <p><strong>RN-CLI-01:</strong> Um cliente não pode possuir mais de
     * {@value Cliente#MAX_RENDIMENTOS} rendimentos cadastrados.
     * A contagem é feita diretamente na lista gerenciada pelo Hibernate para
     * garantir consistência dentro da transação.
     *
     * <p>A validação ocorre em dois níveis:
     * <ol>
     *   <li>Aqui, antes de qualquer modificação, com mensagem contextualizada.</li>
     *   <li>Em {@link Cliente#adicionarRendimento(Rendimento)}, como guarda
     *       de domínio secundária.</li>
     * </ol>
     *
     * @param clienteId  ID do cliente que receberá o rendimento
     * @param rendimento rendimento a adicionar
     * @return cliente atualizado com o novo rendimento
     * @throws UsuarioException se o cliente já possuir {@value Cliente#MAX_RENDIMENTOS}
     *                          ou mais rendimentos (RN-CLI-01)
     * @throws UsuarioException se o cliente não for encontrado
     */
    @Transactional
    public Cliente adicionarRendimento(Long clienteId, Rendimento rendimento) {
        Cliente cliente = buscarPorId(clienteId);

        // ── RN-CLI-01: validação de limite de rendimentos (nível de serviço) ──
        int quantidadeAtual = cliente.getRendimentos().size();
        if (quantidadeAtual >= Cliente.MAX_RENDIMENTOS) {
            throw new UsuarioException(
                String.format(
                    "Limite de rendimentos atingido. O cliente '%s' já possui %d rendimento(s) "
                    + "cadastrado(s). O máximo permitido é %d (RN-CLI-01).",
                    cliente.getNome(),
                    quantidadeAtual,
                    Cliente.MAX_RENDIMENTOS
                )
            );
        }

        // Delega à entidade de domínio (segunda linha de defesa)
        cliente.adicionarRendimento(rendimento);

        return clienteRepository.update(cliente);
    }

    /**
     * Remove um rendimento de um cliente pelo índice na lista.
     *
     * @param clienteId    ID do cliente
     * @param rendimentoId ID do rendimento a remover
     * @return cliente atualizado sem o rendimento removido
     * @throws UsuarioException se o cliente não existir ou o rendimento não pertencer a ele
     */
    @Transactional
    public Cliente removerRendimento(Long clienteId, Long rendimentoId) {
        Cliente cliente = buscarPorId(clienteId);
        boolean removido = cliente.getRendimentos()
            .removeIf(r -> r.getId() != null && r.getId().equals(rendimentoId));

        if (!removido) {
            throw new UsuarioException(
                "Rendimento de ID " + rendimentoId
                + " não encontrado para o cliente de ID " + clienteId + ".");
        }
        return clienteRepository.update(cliente);
    }

    /**
     * Conta quantos rendimentos um cliente possui sem inicializar coleção lazy.
     *
     * @param clienteId ID do cliente
     * @return quantidade de rendimentos
     */
    @Transactional(readOnly = true)
    public long contarRendimentos(Long clienteId) {
        return clienteRepository.countRendimentosByClienteId(clienteId);
    }

    /**
     * Lista rendimentos do cliente sem depender de coleção lazy da entidade.
     *
     * @param clienteId ID do cliente
     * @return rendimentos do cliente
     */
    @Transactional(readOnly = true)
    public List<Rendimento> listarRendimentos(Long clienteId) {
        return clienteRepository.findRendimentosByClienteId(clienteId);
    }

    /**
     * Altera a senha do cliente após validação básica.
     *
     * @param clienteId  ID do cliente
     * @param novaSenha  nova senha (em produção: já deve vir como hash BCrypt)
     * @return cliente atualizado
     * @throws UsuarioException se o cliente não existir
     */
    @Transactional
    public Cliente atualizarSenha(Long clienteId, String novaSenha) {
        Cliente cliente = buscarPorId(clienteId);
        // Delega a validação básica ao método de domínio em Usuario
        cliente.atualizarSenha(novaSenha);
        return clienteRepository.update(cliente);
    }
}
