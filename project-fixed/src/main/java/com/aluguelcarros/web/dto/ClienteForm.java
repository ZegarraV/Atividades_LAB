package com.aluguelcarros.web.dto;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;

@Data
@Introspected
public class ClienteForm {
    private String nome;
    private String cpf;
    private String rg;
    private String profissao;
    private String endereco;
    private String login;
    private String senha;
}
