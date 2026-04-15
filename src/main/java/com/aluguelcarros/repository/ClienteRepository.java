package com.aluguelcarros.repository;

import com.aluguelcarros.model.entity.Cliente;
import com.aluguelcarros.model.entity.Rendimento;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@Singleton
public class ClienteRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Cliente save(Cliente cliente) {
        if (cliente.getId() == null) {
            entityManager.persist(cliente);
            return cliente;
        }
        return entityManager.merge(cliente);
    }

    @Transactional
    public Cliente update(Cliente cliente) {
        return entityManager.merge(cliente);
    }

    @Transactional
    public void delete(Cliente cliente) {
        entityManager.remove(entityManager.contains(cliente) ? cliente : entityManager.merge(cliente));
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Cliente.class, id));
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> findByCpf(String cpf) {
        List<Cliente> resultados = entityManager.createQuery(
                "SELECT c FROM Cliente c WHERE c.cpf = :cpf", Cliente.class)
            .setParameter("cpf", cpf)
            .getResultList();
        return resultados.stream().findFirst();
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> findByLogin(String login) {
        List<Cliente> resultados = entityManager.createQuery(
                "SELECT c FROM Cliente c WHERE c.login = :login", Cliente.class)
            .setParameter("login", login)
            .getResultList();
        return resultados.stream().findFirst();
    }

    @Transactional(readOnly = true)
    public boolean existsByCpf(String cpf) {
        Long total = entityManager.createQuery(
                "SELECT COUNT(c) FROM Cliente c WHERE c.cpf = :cpf", Long.class)
            .setParameter("cpf", cpf)
            .getSingleResult();
        return total != null && total > 0;
    }

    @Transactional(readOnly = true)
    public List<Cliente> findByNomeContainsIgnoreCase(String nome) {
        return entityManager.createQuery(
                "SELECT c FROM Cliente c WHERE LOWER(c.nome) LIKE LOWER(:nome) ORDER BY c.nome",
                Cliente.class)
            .setParameter("nome", "%" + nome + "%")
            .getResultList();
    }

    @Transactional(readOnly = true)
    public Page<Cliente> findAll(Pageable pageable) {
        List<Cliente> itens = entityManager.createQuery(
                "SELECT c FROM Cliente c ORDER BY c.nome",
                Cliente.class)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getSize())
            .getResultList();
        Long total = entityManager.createQuery("SELECT COUNT(c) FROM Cliente c", Long.class).getSingleResult();
        return Page.of(itens, pageable, total);
    }

    @Transactional(readOnly = true)
    public long countRendimentosByClienteId(Long clienteId) {
        Long total = entityManager.createQuery(
                "SELECT COUNT(r) FROM Rendimento r WHERE r.cliente.id = :clienteId",
                Long.class)
            .setParameter("clienteId", clienteId)
            .getSingleResult();
        return total == null ? 0 : total;
    }

    @Transactional(readOnly = true)
    public List<Rendimento> findRendimentosByClienteId(Long clienteId) {
        return entityManager.createQuery(
                "SELECT r FROM Rendimento r WHERE r.cliente.id = :clienteId ORDER BY r.id DESC",
                Rendimento.class)
            .setParameter("clienteId", clienteId)
            .getResultList();
    }
}
