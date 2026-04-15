package com.aluguelcarros.service;

import com.aluguelcarros.model.entity.Usuario;
import com.aluguelcarros.model.exception.UsuarioException;
import com.aluguelcarros.repository.AgenteRepository;
import com.aluguelcarros.repository.ClienteRepository;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * Serviço responsável pela autenticação de usuários no sistema.
 */
@Singleton
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final AgenteRepository agenteRepository;

    public AuthService(ClienteRepository clienteRepository,
                       AgenteRepository agenteRepository) {
        this.clienteRepository = clienteRepository;
        this.agenteRepository  = agenteRepository;
    }

    @Transactional(readOnly = true)
    public Usuario autenticar(String login, String senhaFornecida) {
        Optional<? extends Usuario> optUsuario = clienteRepository.findByLogin(login);

        if (optUsuario.isEmpty()) {
            optUsuario = agenteRepository.findByLogin(login);
        }

        Usuario usuario = optUsuario.orElseThrow(() ->
            new UsuarioException("Usuário não encontrado para o login: " + login));

        if (!usuario.autenticar(senhaFornecida)) {
            throw new UsuarioException("Senha incorreta.");
        }

        return usuario;
    }

    @Transactional(readOnly = true)
    public boolean loginDisponivel(String login) {
        return clienteRepository.findByLogin(login).isEmpty()
            && agenteRepository.findByLogin(login).isEmpty();
    }
}

