package com.aluguelcarros.repository;

import com.aluguelcarros.model.entity.Automovel;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@Singleton
public class AutomovelRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Automovel save(Automovel automovel) {
        if (automovel.getId() == null) {
            entityManager.persist(automovel);
            return automovel;
        }
        return entityManager.merge(automovel);
    }

    @Transactional
    public Automovel update(Automovel automovel) {
        return entityManager.merge(automovel);
    }

    @Transactional
    public void delete(Automovel automovel) {
        entityManager.remove(entityManager.contains(automovel) ? automovel : entityManager.merge(automovel));
    }

    @Transactional(readOnly = true)
    public Optional<Automovel> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Automovel.class, id));
    }

    @Transactional(readOnly = true)
    public Optional<Automovel> findByPlaca(String placa) {
        List<Automovel> resultados = entityManager.createQuery(
                "SELECT a FROM Automovel a WHERE a.placa = :placa", Automovel.class)
            .setParameter("placa", placa)
            .getResultList();
        return resultados.stream().findFirst();
    }

    @Transactional(readOnly = true)
    public List<Automovel> findByDisponivel(boolean disponivel) {
        return entityManager.createQuery(
                "SELECT a FROM Automovel a WHERE a.disponivel = :disponivel ORDER BY a.marca, a.modelo",
                Automovel.class)
            .setParameter("disponivel", disponivel)
            .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Automovel> findByMarcaIgnoreCase(String marca) {
        return entityManager.createQuery(
                "SELECT a FROM Automovel a WHERE LOWER(a.marca) = LOWER(:marca) ORDER BY a.modelo",
                Automovel.class)
            .setParameter("marca", marca)
            .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Automovel> findByMarcaIgnoreCaseAndDisponivel(String marca, boolean disponivel) {
        return entityManager.createQuery(
                "SELECT a FROM Automovel a WHERE LOWER(a.marca) = LOWER(:marca) AND a.disponivel = :disponivel ORDER BY a.modelo",
                Automovel.class)
            .setParameter("marca", marca)
            .setParameter("disponivel", disponivel)
            .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Automovel> findByAno(int ano) {
        return entityManager.createQuery(
                "SELECT a FROM Automovel a WHERE a.ano = :ano ORDER BY a.marca, a.modelo",
                Automovel.class)
            .setParameter("ano", ano)
            .getResultList();
    }

    @Transactional(readOnly = true)
    public boolean existsByPlaca(String placa) {
        Long total = entityManager.createQuery(
                "SELECT COUNT(a) FROM Automovel a WHERE a.placa = :placa", Long.class)
            .setParameter("placa", placa)
            .getSingleResult();
        return total != null && total > 0;
    }

    @Transactional(readOnly = true)
    public Iterable<Automovel> findAll() {
        return entityManager.createQuery(
                "SELECT a FROM Automovel a ORDER BY a.marca, a.modelo",
                Automovel.class)
            .getResultList();
    }

    @Transactional(readOnly = true)
    public Page<Automovel> findAll(Pageable pageable) {
        List<Automovel> itens = entityManager.createQuery(
                "SELECT a FROM Automovel a ORDER BY a.marca, a.modelo",
                Automovel.class)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getSize())
            .getResultList();
        Long total = entityManager.createQuery("SELECT COUNT(a) FROM Automovel a", Long.class).getSingleResult();
        return Page.of(itens, pageable, total);
    }
}
