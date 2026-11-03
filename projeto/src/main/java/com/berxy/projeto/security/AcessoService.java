package com.berxy.projeto.security;

import com.berxy.projeto.PlanoIntercambio.PlanoIntercambioRepository;
import com.berxy.projeto.user.Role;
import com.berxy.projeto.user.User;
import com.berxy.projeto.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class AcessoService {
    private final UserRepository usuarios;
    private final PlanoIntercambioRepository planos;

    public AcessoService(UserRepository usuarios, PlanoIntercambioRepository planos) {
        this.usuarios = usuarios;
        this.planos = planos;
    }

    public User usuarioAtual(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InsufficientAuthenticationException("Faça login para continuar.");
        }
        return usuarios.findByEmail(authentication.getName())
                .filter(User::isActive)
                .orElseThrow(() -> new InsufficientAuthenticationException("Faça login novamente."));
    }

    public void exigirUsuario(Authentication authentication, Long usuarioId) {
        User atual = usuarioAtual(authentication);
        if (atual.getRole() != Role.ADMIN && !atual.getId().equals(usuarioId)) {
            throw new AccessDeniedException("Você não tem acesso aos dados deste usuário.");
        }
    }

    public boolean permitir(Authentication authentication, HttpServletRequest request) {
        User atual = usuarioAtual(authentication);
        if (atual.getRole() == Role.ADMIN) { return true; }
        String caminho = request.getServletPath();
        // MockMvc também pode representar a rota apenas no requestURI.
        if (caminho.isEmpty()) {
            caminho = request.getRequestURI().substring(request.getContextPath().length());
        }
        String[] partes = caminho.split("/");
        if (caminho.equals("/auth/me") || caminho.equals("/auth/logout")) { return true; }
        if (caminho.equals("/planos-intercambio")) { return true; }
        if (caminho.equals("/cursos/pessoais") && request.getMethod().equals("POST")) return true;
        if (partes.length == 4 && partes[1].equals("cursos") && partes[2].equals("pessoais")
                && request.getMethod().equals("PUT")) return true;
        if (partes.length >= 3 && partes[1].equals("users")) {
            if (partes.length >= 4 && partes[3].equals("activate")) { return false; }
            return partes[2].equals(atual.getId().toString());
        }
        if (partes.length >= 3 && partes[1].equals("planos-intercambio")) {
            try {
                return planos.findById(Long.valueOf(partes[2]))
                        .map(plano -> plano.getUser().getId().equals(atual.getId()))
                        .orElse(true); // O controller mantém a resposta 404 para IDs inexistentes.
            } catch (NumberFormatException erro) {
                return false;
            }
        }
        return request.getMethod().equals("GET") && partes.length >= 2
                && java.util.Set.of("instituicoes", "cursos", "area-estudo", "modelos-tarefa").contains(partes[1]);
    }
}
