package com.berxy.projeto.auth.dto;

import com.berxy.projeto.user.dto.MaxBytes;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8) @MaxBytes(72) String password
) { }
