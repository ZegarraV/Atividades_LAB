package com.aluguelcarros.web.dto;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;

import java.time.LocalDate;

@Data
@Introspected
public class PedidoForm {
    private Long automovelId;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private double valorDiario;
}
