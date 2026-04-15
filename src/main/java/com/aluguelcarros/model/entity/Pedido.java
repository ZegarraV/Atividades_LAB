package com.aluguelcarros.model.entity;

import com.aluguelcarros.model.enums.PedidoStatus;

/**
 * Contrato de domínio para um pedido de aluguel de carro.
 *
 * <p>A implementação concreta e persistível é {@link PedidoImpl}.
 * Esta interface define as operações de negócio que qualquer tipo de
 * pedido deve suportar, permitindo extensões futuras (ex: PedidoExpress).
 */
public interface Pedido {

    /**
     * Atualiza o status do pedido dentro do seu ciclo de vida.
     *
     * @param status novo status a ser aplicado
     */
    void atualizarStatus(PedidoStatus status);

    /**
     * Calcula o valor total do pedido com base nas datas e no automóvel.
     *
     * @return valor calculado em reais (BRL)
     */
    double calcularValor();
}
