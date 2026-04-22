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
    private long proprietarioReferenciaId;
    /** URL da imagem do veículo (preenchida por URL ou upload processado no controller). */
    private String imagemUrl;
}
