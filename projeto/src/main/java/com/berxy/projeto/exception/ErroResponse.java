package com.berxy.projeto.exception;

import java.util.Map;

// DTO de erro simples, como na Aula 08. Mantém os campos usados pelo frontend.
public record ErroResponse(int status, String detail, Map<String, String> errors) {
    public ErroResponse(int status, String detail) {
        this(status, detail, Map.of());
    }
}
