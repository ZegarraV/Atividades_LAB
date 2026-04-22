package com.aluguelcarros.model.exception;

/**
 * Lançada quando uma operação sobre um Pedido viola as regras de negócio.
 * Exemplos: tentar aprovar um pedido que não está EM_ANALISE,
 * ou cancelar um pedido já APROVADO.
 */
public class PedidoException extends NegocioException {

    public PedidoException(String message) {
        super(message);
    }

    public PedidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
