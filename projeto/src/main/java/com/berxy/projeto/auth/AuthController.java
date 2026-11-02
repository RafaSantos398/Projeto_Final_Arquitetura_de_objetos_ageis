package com.berxy.projeto.auth;

import com.berxy.projeto.auth.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import com.berxy.projeto.exception.ErroResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;
    public AuthController(AuthService service) { this.service = service; }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> cadastrar(@Valid @RequestBody RegisterRequest dados) {
        AuthResponse resposta = service.cadastrar(dados);
        return ResponseEntity.created(URI.create("/users/" + resposta.usuario().id())).body(resposta);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest dados) { return service.login(dados); }

    @GetMapping("/me")
    public AuthResponse usuarioAtual(Authentication authentication) { return service.consultar(authentication); }

    // JWT não é uma sessão: o frontend precisa descartar o token no logout.
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout() { }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErroResponse credenciaisInvalidas() {
        return new ErroResponse(401, "E-mail ou senha inválidos.");
    }
}
