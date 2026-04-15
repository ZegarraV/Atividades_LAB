package com.aluguelcarros.repository;

import com.aluguelcarros.model.entity.Contrato;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA manual para {@link Contrato}.
 *
 * <p>Esse acesso não usa Micronaut Data porque o processador estava falhando na
 * geração dos métodos herdados de {@code CrudRepository} para esta entidade,
 * bloqueando a compilação da aplicação inteira.
 */
@Singleton
public class ContratoRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public Optional<Contrato> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Contrato.class, id));
    }

    @Transactional
    public Contrato save(Contrato contrato) {
        if (contrato.getId() == null) {
            entityManager.persist(contrato);
            return contrato;
        }
        return entityManager.merge(contrato);
    }

    @Transactional(readOnly = true)
    public Optional<Contrato> findByPedidoId(Long pedidoId) {
        List<Contrato> resultados = entityManager.createQuery(
                "SELECT c FROM Contrato c JOIN FETCH c.pedido p WHERE p.id = :pedidoId",
                Contrato.class)
            .setParameter("pedidoId", pedidoId)
            .getResultList();
        return resultados.stream().findFirst();
    }

    @Transactional(readOnly = true)
    public List<Contrato> findByClienteId(Long clienteId) {
        return entityManager.createQuery(
                "SELECT c FROM Contrato c JOIN FETCH c.pedido p WHERE p.cliente.id = :clienteId",
                Contrato.class)
            .setParameter("clienteId", clienteId)
            .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Contrato> findByTipoIgnoreCase(String tipo) {
        return entityManager.createQuery(
                "SELECT c FROM Contrato c WHERE LOWER(c.tipo) = LOWER(:tipo)",
                Contrato.class)
            .setParameter("tipo", tipo)
            .getResultList();
    }

    @Transactional(readOnly = true)
    public boolean existsByPedidoId(Long pedidoId) {
        Long total = entityManager.createQuery(
                "SELECT COUNT(c) FROM Contrato c WHERE c.pedido.id = :pedidoId",
                Long.class)
            .setParameter("pedidoId", pedidoId)
            .getSingleResult();
        return total != null && total > 0;
    }
}
