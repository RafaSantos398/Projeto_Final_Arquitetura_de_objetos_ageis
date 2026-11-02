package com.berxy.projeto.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8)  String password
) {
}