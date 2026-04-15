package com.aluguelcarros.model.exception;

/**
 * Exceção base para todas as violações de regras de negócio do sistema.
 * Deve ser estendida por exceções de domínio mais específicas.
 */
public class NegocioException extends RuntimeException {

    public NegocioException(String message) {
        super(message);
    }

    public NegocioException(String message, Throwable cause) {
        super(message, cause);
    }
}
