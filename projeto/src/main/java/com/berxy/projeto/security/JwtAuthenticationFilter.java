package com.berxy.projeto.security;

import com.berxy.projeto.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Mesmo fluxo do filtro apresentado na Aula 14: Bearer -> validar -> buscar usuário.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final UserService usuarios;
    private final AcessoService acesso;

    public JwtAuthenticationFilter(JwtService jwt, UserService usuarios, AcessoService acesso) {
        this.jwt = jwt;
        this.usuarios = usuarios;
        this.acesso = acesso;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String caminho = request.getRequestURI().substring(request.getContextPath().length());
        return request.getMethod().equals("POST")
                && java.util.Set.of("/auth/login", "/auth/register", "/users").contains(caminho);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String cabecalho = request.getHeader("Authorization");
        if (cabecalho != null && cabecalho.startsWith("Bearer ")) {
            String token = cabecalho.substring(7);
            // Confere antes de extrair: token expirado ou adulterado não pode virar erro 500.
            if (!jwt.isTokenValid(token)) {
                response.sendError(401, "Token inválido ou expirado.");
                return;
            }
            try {
                var usuario = usuarios.loadUserByUsername(jwt.extractUsername(token));
                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AuthenticationException erro) {
                response.sendError(401, "Faça login novamente.");
                return;
            }
        }
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                if (!acesso.permitir(authentication, request)) {
                    response.sendError(403, "Acesso negado.");
                    return;
                }
            } catch (AuthenticationException erro) {
                response.sendError(401, "Faça login novamente.");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
