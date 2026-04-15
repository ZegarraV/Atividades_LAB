package com.aluguelcarros.web.dto;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;

@Data
@Introspected
public class AutomovelForm {
    private String matricula;
    private String placa;
    private String marca;
    private String modelo;
    private int ano;
    private String proprietarioTipo;
    private int proprietarioReferenciaId;
}
