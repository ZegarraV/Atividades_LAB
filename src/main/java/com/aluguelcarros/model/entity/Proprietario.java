package com.aluguelcarros.model.entity;

import io.micronaut.core.annotation.Introspected;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objeto de valor embutido ({@code @Embeddable}) que identifica o proprietário
 * de um {@link Automovel}.
 *
 * <p>Por ser um tipo de referência polimórfica (Banco ou Empresa), armazena
 * o tipo e o ID externo em vez de uma FK direta, evitando múltiplas foreign keys.
 */
@Data
@NoArgsConstructor
@Introspected
@Embeddable
public class Proprietario {

    /**
     * Tipo do proprietário. Valores esperados: "BANCO" ou "EMPRESA".
     */
    @Column(name = "proprietario_tipo", length = 20)
    private String tipo;

    /**
     * Identificador externo do proprietário no sistema correspondente
     * (ID do banco ou da empresa).
     */
    @Column(name = "proprietario_referencia_id")
    private int referenciaId;
}
