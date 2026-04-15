package com.aluguelcarros.model.entity;

import io.micronaut.core.annotation.Introspected;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Representa um rendimento financeiro declarado pelo {@link Cliente}.
 *
 * <p>Relacionamento: Muitos {@code Rendimento} para Um {@code Cliente}.
 * Um cliente pode ter no máximo {@link Cliente#MAX_RENDIMENTOS} rendimentos
 * — regra aplicada em {@link Cliente#adicionarRendimento(Rendimento)}.
 */
@Data
@NoArgsConstructor
@Introspected
@Entity
@Table(name = "rendimentos")
public class Rendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String descricao;

    @Column(nullable = false)
    private double valor;

    /**
     * Cliente proprietário deste rendimento.
     * Excluído do {@code toString()} para evitar loop circular.
     */
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}
