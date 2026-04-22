package com.aluguelcarros.repository;

import com.aluguelcarros.model.entity.PedidoImpl;
import com.aluguelcarros.model.enums.PedidoStatus;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@Singleton
public class PedidoRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public PedidoImpl save(PedidoImpl pedido) {
        if (pedido.getId() == null) {
            entityManager.persist(pedido);
            return pedido;
        }
        return entityManager.merge(pedido);
    }

    @Transactional
    public PedidoImpl update(PedidoImpl pedido) {
        return entityManager.merge(pedido);
    }

    @Transactional(readOnly = true)
    public Optional<PedidoImpl> findById(Long id) {
        return Optional.ofNullable(entityManager.find(PedidoImpl.class, id));
    }

    @Transactional(readOnly = true)
    public List<PedidoImpl> findByClienteId(Long clienteId) {
        return entityManager.createQuery(
                "SELECT p FROM PedidoImpl p JOIN FETCH p.automovel WHERE p.cliente.id = :clienteId ORDER BY p.id DESC",
                PedidoImpl.class)
            .setParameter("clienteId", clienteId)
            .getResultList();
    }

    @Transactional(readOnly = true)
    public List<PedidoImpl> findByClienteIdAndStatus(Long clienteId, PedidoStatus status) {
        return entityManager.createQuery(
                "SELECT p FROM PedidoImpl p JOIN FETCH p.automovel WHERE p.cliente.id = :clienteId AND p.status = :status ORDER BY p.id DESC",
                PedidoImpl.class)
            .setParameter("clienteId", clienteId)
            .setParameter("status", status)
            .getResultList();
    }

    @Transactional(readOnly = true)
    public List<PedidoImpl> findByStatus(PedidoStatus status) {
        return entityManager.createQuery(
                "SELECT p FROM PedidoImpl p WHERE p.status = :status ORDER BY p.id DESC",
                PedidoImpl.class)
            .setParameter("status", status)
            .getResultList();
    }

    @Transactional(readOnly = true)
    public boolean existsByAutomovelIdAndStatus(Long automovelId, PedidoStatus status) {
        Long total = entityManager.createQuery(
                "SELECT COUNT(p) FROM PedidoImpl p WHERE p.automovel.id = :automovelId AND p.status = :status",
                Long.class)
            .setParameter("automovelId", automovelId)
            .setParameter("status", status)
            .getSingleResult();
        return total != null && total > 0;
    }

    @Transactional(readOnly = true)
    public long countByClienteIdAndStatus(Long clienteId, PedidoStatus status) {
        Long total = entityManager.createQuery(
                "SELECT COUNT(p) FROM PedidoImpl p WHERE p.cliente.id = :clienteId AND p.status = :status",
                Long.class)
            .setParameter("clienteId", clienteId)
            .setParameter("status", status)
            .getSingleResult();
        return total == null ? 0 : total;
    }

    @Transactional(readOnly = true)
    public List<PedidoImpl> findByStatusWithAutomovel(PedidoStatus status) {
        return entityManager.createQuery(
                "SELECT DISTINCT p FROM PedidoImpl p " +
                    "JOIN FETCH p.automovel " +
                    "JOIN FETCH p.cliente c " +
                    "LEFT JOIN FETCH c.rendimentos " +
                    "WHERE p.status = :status ORDER BY p.id DESC",
                PedidoImpl.class)
            .setParameter("status", status)
            .getResultList();
    }
}
