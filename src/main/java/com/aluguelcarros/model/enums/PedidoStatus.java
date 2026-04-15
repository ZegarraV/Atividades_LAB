package com.aluguelcarros.model.enums;

/**
 * Representa os possíveis estados de um {@link com.aluguelcarros.model.entity.PedidoImpl}
 * ao longo do seu ciclo de vida no sistema de aluguel de carros.
 *
 * <p>Fluxo esperado:
 * <pre>
 *   RASCUNHO → ENVIADO → EM_ANALISE → APROVADO → (gera Contrato)
 *                                   ↘ REPROVADO
 *   (qualquer estado anterior a APROVADO) → CANCELADO
 * </pre>
 */
public enum PedidoStatus {

    /** Pedido criado mas ainda não submetido pelo cliente. */
    RASCUNHO,

    /** Pedido submetido pelo cliente, aguardando análise do agente. */
    ENVIADO,

    /** Pedido em processo de análise financeira pelo agente. */
    EM_ANALISE,

    /** Pedido aprovado pelo agente; elegível para geração de contrato. */
    APROVADO,

    /** Pedido reprovado pelo agente após análise financeira. */
    REPROVADO,

    /** Pedido cancelado pelo cliente antes da aprovação. */
    CANCELADO
}
