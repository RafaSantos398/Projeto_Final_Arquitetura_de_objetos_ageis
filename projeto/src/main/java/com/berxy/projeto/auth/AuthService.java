package com.berxy.projeto.auth;

import com.berxy.projeto.auth.dto.RegisterRequest;
import com.berxy.projeto.auth.dto.AuthResponse;
import com.berxy.projeto.auth.dto.LoginRequest;
import com.berxy.projeto.security.JwtService;
import com.berxy.projeto.security.AcessoService;
import com.berxy.projeto.user.UserService;
import com.berxy.projeto.user.dto.UserResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService usuarios;
    private final AcessoService acesso;
    private final JwtService jwt;
    private final org.springframework.security.crypto.password.PasswordEncoder encoder;

    public AuthService(UserService usuarios, AcessoService acesso, JwtService jwt,
                       org.springframework.security.crypto.password.PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.acesso = acesso;
        this.jwt = jwt;
        this.encoder = encoder;
    }

    public AuthResponse cadastrar(RegisterRequest dados) {
        return new AuthResponse(null, UserResponse.from(usuarios.create(dados.nome(), dados.email(), dados.password())));
    }

    public AuthResponse consultar(Authentication authentication) {
        return new AuthResponse(null, UserResponse.from(acesso.usuarioAtual(authentication)));
    }

    // Busca o usuário e compara a senha com PasswordEncoder.matches, como na Aula 14.
    public AuthResponse login(LoginRequest dados) {
        usuarios.validarLimiteSenha(dados.password(), "password");
        try {
            var detalhes = usuarios.loadUserByUsername(dados.email());
            if (!encoder.matches(dados.password(), detalhes.getPassword())) {
                throw new org.springframework.security.authentication.BadCredentialsException("Credenciais inválidas.");
            }
            var usuario = usuarios.findByEmail(detalhes.getUsername());
            return new AuthResponse(jwt.generateToken(usuario.getEmail()), UserResponse.from(usuario));
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException erro) {
            throw new org.springframework.security.authentication.BadCredentialsException("Credenciais inválidas.");
        }
    }
}
