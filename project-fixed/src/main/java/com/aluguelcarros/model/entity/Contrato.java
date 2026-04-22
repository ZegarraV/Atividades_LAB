package com.aluguelcarros.model.entity;

import io.micronaut.core.annotation.Introspected;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Representa o contrato formalizado após a aprovação de um {@link PedidoImpl}.
 *
 * <p>Relacionamento: Um {@code Contrato} para Um {@link PedidoImpl}.
 * O contrato é gerado pelo {@link Agente#aprovarPedido(PedidoImpl)} e
 * persistido pelo {@code PedidoService}.
 */
@Data
@NoArgsConstructor
@Introspected
@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tipo do contrato, refletindo o tipo do agente que o gerou:
     * "BANCO" ou "EMPRESA".
     */
    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false)
    private LocalDate dataAssinatura;

    @Column(nullable = false)
    private double valorFinanciado;

    /**
     * Pedido que originou este contrato.
     * Excluído do toString para evitar referência circular.
     */
    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private PedidoImpl pedido;

    // ─────────────────────────────────────────────
    // Métodos de domínio (conforme diagrama de classes)
    // ─────────────────────────────────────────────

    /**
     * Preenche os dados padrão do contrato com base no pedido vinculado.
     * Deve ser chamado após {@link #vincularPedido(PedidoImpl)}.
     */
    public void gerarContrato() {
        if (this.pedido != null) {
            this.valorFinanciado = this.pedido.calcularValor();
            this.dataAssinatura = LocalDate.now();
        }
    }

    /**
     * Associa um pedido a este contrato e sincroniza o valor financiado.
     *
     * @param pedido pedido aprovado a ser vinculado
     */
    public void vincularPedido(PedidoImpl pedido) {
        this.pedido = pedido;
        this.valorFinanciado = pedido.calcularValor();
    }
}
