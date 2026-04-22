package com.aluguelcarros.web.dto;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;

@Data
@Introspected
public class LoginForm {
    private String login;
    private String senha;
}
