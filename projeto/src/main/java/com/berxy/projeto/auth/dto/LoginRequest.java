package com.berxy.projeto.auth.dto;

import com.berxy.projeto.user.dto.MaxBytes;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @MaxBytes(72) String password
) { }
