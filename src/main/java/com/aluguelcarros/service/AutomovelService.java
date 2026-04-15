package com.aluguelcarros.service;

import com.aluguelcarros.model.entity.Automovel;
import com.aluguelcarros.model.exception.NegocioException;
import com.aluguelcarros.repository.AutomovelRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Serviço de domínio para operações de cadastro e consulta de {@link Automovel}.
 *
 * <p>Regras de negócio aplicadas:
 * <ul>
 *   <li><strong>RN-AUT-01:</strong> Placa deve ser única no sistema.</li>
 *   <li><strong>RN-AUT-02:</strong> Um automóvel reservado não pode ser deletado
 *       diretamente; deve ser liberado primeiro.</li>
 * </ul>
 */
@Singleton
public class AutomovelService {

    private final AutomovelRepository automovelRepository;

    public AutomovelService(AutomovelRepository automovelRepository) {
        this.automovelRepository = automovelRepository;
    }

    /**
     * Cadastra um novo automóvel no sistema.
     *
     * @param automovel entidade preenchida (sem ID)
     * @return automóvel persistido
     * @throws NegocioException se a placa já estiver cadastrada (RN-AUT-01)
     */
    @Transactional
    public Automovel cadastrar(Automovel automovel) {
        if (automovelRepository.existsByPlaca(automovel.getPlaca())) {
            throw new NegocioException(
                "Já existe um automóvel cadastrado com a placa: " + automovel.getPlaca());
        }
        return automovelRepository.save(automovel);
    }

    /**
     * Busca um automóvel pelo ID.
     *
     * @param id ID do automóvel
     * @return automóvel encontrado
     * @throws NegocioException se não existir
     */
    @Transactional(readOnly = true)
    public Automovel buscarPorId(Long id) {
        return automovelRepository.findById(id)
            .orElseThrow(() -> new NegocioException("Automóvel não encontrado para o ID: " + id));
    }

    /**
     * Lista todos os automóveis de forma paginada.
     *
     * @param pageable parâmetros de paginação
     * @return página de automóveis
     */
    @Transactional(readOnly = true)
    public Page<Automovel> listar(Pageable pageable) {
        return automovelRepository.findAll(pageable);
    }

    /**
     * Lista apenas os automóveis disponíveis para seleção em novos pedidos.
     *
     * @return lista de automóveis com {@code disponivel = true}
     */
    @Transactional(readOnly = true)
    public List<Automovel> listarDisponiveis() {
        return automovelRepository.findByDisponivel(true);
    }

    /**
     * Lista todos os automóveis cadastrados no sistema.
     *
     * @return lista completa de automóveis
     */
    @Transactional(readOnly = true)
    public List<Automovel> listarTodos() {
        return StreamSupport.stream(automovelRepository.findAll().spliterator(), false)
            .toList();
    }

    /**
     * Atualiza os dados cadastrais de um automóvel.
     *
     * @param id     ID do automóvel a atualizar
     * @param dados  dados novos
     * @return automóvel atualizado
     * @throws NegocioException se o automóvel não existir
     */
    @Transactional
    public Automovel atualizar(Long id, Automovel dados) {
        Automovel existente = buscarPorId(id);
        existente.setMarca(dados.getMarca());
        existente.setModelo(dados.getModelo());
        existente.setAno(dados.getAno());
        existente.setMatricula(dados.getMatricula());
        existente.setProprietario(dados.getProprietario());
        return automovelRepository.update(existente);
    }

    /**
     * Remove um automóvel do sistema.
     *
     * @param id ID do automóvel a remover
     * @throws NegocioException se o automóvel não existir ou estiver reservado (RN-AUT-02)
     */
    @Transactional
    public void remover(Long id) {
        Automovel automovel = buscarPorId(id);
        if (!automovel.isDisponivel()) {
            throw new NegocioException(
                "O automóvel de placa '" + automovel.getPlaca()
                + "' está reservado e não pode ser removido (RN-AUT-02). "
                + "Libere-o antes de prosseguir.");
        }
        automovelRepository.delete(automovel);
    }

    /**
     * Marca um automóvel como disponível (após devolução ou cancelamento de pedido).
     *
     * @param id ID do automóvel
     * @return automóvel atualizado
     */
    @Transactional
    public Automovel disponibilizar(Long id) {
        Automovel automovel = buscarPorId(id);
        automovel.disponibilizar();
        return automovelRepository.update(automovel);
    }
}
