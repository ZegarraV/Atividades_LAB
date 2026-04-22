package com.aluguelcarros.model.exception;

/**
 * Lançada quando uma operação sobre um Usuário (Cliente ou Agente) viola
 * as regras de negócio. Exemplos: exceder o limite de rendimentos,
 * tentativa de login inválida.
 */
public class UsuarioException extends NegocioException {

    public UsuarioException(String message) {
        super(message);
    }

    public UsuarioException(String message, Throwable cause) {
        super(message, cause);
    }
}
