package com.aluguelcarros.web.dto;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;

@Data
@Introspected
public class RendimentoForm {
    private String descricao;
    private double valor;
}
