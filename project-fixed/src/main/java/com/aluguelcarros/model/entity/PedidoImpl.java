package com.aluguelcarros.model.entity;

import com.aluguelcarros.model.enums.PedidoStatus;
import io.micronaut.core.annotation.Introspected;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Implementação JPA da interface {@link Pedido}, representando um pedido de
 * aluguel de carro no sistema.
 *
 * <p>Relacionamentos:
 * <ul>
 *   <li>Muitos {@code PedidoImpl} para Um {@link Cliente} (lado dono).</li>
 *   <li>Um {@code PedidoImpl} para Um {@link Automovel} (lado dono).</li>
 * </ul>
 *
 * <p>Status inicial padrão: {@link PedidoStatus#RASCUNHO}.
 */
@Data
@NoArgsConstructor
@Introspected
@Entity
@Table(name = "pedidos")
public class PedidoImpl implements Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private LocalDate dataFim;

    /** Valor base por dia do aluguel. O valor total é calculado por {@link #calcularValor()}. */
    @Column(nullable = false)
    private double valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PedidoStatus status = PedidoStatus.RASCUNHO;

    /**
     * Cliente que originou o pedido.
     * Excluído do toString para evitar loop circular.
     */
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /**
     * Automóvel associado a este pedido.
     * Usa ManyToOne para permitir que um carro apareça em múltiplos pedidos
     * (histórico de aluguéis), sendo bloqueado pela regra de disponibilidade no serviço.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "automovel_id", nullable = false)
    private Automovel automovel;

    // ─────────────────────────────────────────────
    // Implementação da interface Pedido
    // ─────────────────────────────────────────────

    /**
     * {@inheritDoc}
     */
    @Override
    public void atualizarStatus(PedidoStatus novoStatus) {
        this.status = novoStatus;
    }

    /**
     * Calcula o valor total do pedido multiplicando o valor diário pelo número
     * de dias entre {@code dataInicio} e {@code dataFim}.
     *
     * @return valor total em reais; retorna {@code valor} se as datas forem nulas
     */
    @Override
    public double calcularValor() {
        if (dataInicio == null || dataFim == null) {
            return this.valor;
        }
        long dias = ChronoUnit.DAYS.between(dataInicio, dataFim);
        return dias > 0 ? dias * this.valor : this.valor;
    }
}
