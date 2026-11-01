package com.berxy.projeto.user.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// O BCrypt limita a senha em bytes, e @Size conta caracteres: acentos e emojis ocupam mais de um byte
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxBytesValidator.class)
public @interface MaxBytes {

    int value();

    String message() default "excede o tamanho máximo permitido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
