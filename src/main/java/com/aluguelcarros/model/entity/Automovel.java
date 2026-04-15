package com.aluguelcarros.model.entity;

import io.micronaut.core.annotation.Introspected;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa um automóvel disponível para aluguel no sistema.
 *
 * <p>O proprietário do veículo é modelado como um {@link Proprietario}
 * embutido ({@code @Embedded}), pois pode ser um Banco ou uma Empresa
 * (referência polimórfica por tipo + ID externo).
 */
@Data
@NoArgsConstructor
@Introspected
@Entity
@Table(name = "automoveis")
public class Automovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String matricula;

    @Column(nullable = false, unique = true, length = 8)
    private String placa;

    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 80)
    private String modelo;

    @Column(nullable = false)
    private int ano;

    /** Indica se o automóvel está disponível para reserva no momento. */
    @Column(nullable = false)
    private boolean disponivel = true;

    @Embedded
    private Proprietario proprietario;

    // ─────────────────────────────────────────────
    // Métodos de domínio (conforme diagrama de classes)
    // ─────────────────────────────────────────────

    /**
     * Marca o automóvel como disponível para novos pedidos.
     * Chamado pelo serviço após cancelamento ou devolução.
     */
    public void disponibilizar() {
        this.disponivel = true;
    }

    /**
     * Reserva o automóvel, tornando-o indisponível para outros pedidos.
     * Chamado pelo serviço ao aprovar um pedido.
     */
    public void reservar() {
        this.disponivel = false;
    }
}
