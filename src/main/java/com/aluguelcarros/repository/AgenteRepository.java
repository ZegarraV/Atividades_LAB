package com.aluguelcarros.repository;

import com.aluguelcarros.model.entity.Agente;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@Singleton
public class AgenteRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Agente save(Agente agente) {
        if (agente.getId() == null) {
            entityManager.persist(agente);
            return agente;
        }
        return entityManager.merge(agente);
    }

    @Transactional(readOnly = true)
    public Optional<Agente> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Agente.class, id));
    }

    @Transactional(readOnly = true)
    public Optional<Agente> findByLogin(String login) {
        List<Agente> resultados = entityManager.createQuery(
                "SELECT a FROM Agente a WHERE a.login = :login", Agente.class)
            .setParameter("login", login)
            .getResultList();
        return resultados.stream().findFirst();
    }
}
