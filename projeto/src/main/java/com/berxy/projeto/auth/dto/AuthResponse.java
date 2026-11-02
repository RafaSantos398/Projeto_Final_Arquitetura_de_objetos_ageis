package com.berxy.projeto.auth.dto;

import com.berxy.projeto.user.dto.UserResponse;

public record AuthResponse(String token, UserResponse usuario) { }
