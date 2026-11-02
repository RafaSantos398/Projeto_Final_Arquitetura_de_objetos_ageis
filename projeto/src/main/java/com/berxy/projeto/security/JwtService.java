package com.berxy.projeto.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// Emissão e validação de JWT conforme a Aula 14.
@Service
public class JwtService {
    private final SecretKey chave;
    private final long expiracao;

    public JwtService(@Value("${jwt.secret}") String segredo,
                      @Value("${jwt.expiration:3600000}") long expiracao) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        if (expiracao <= 0) { throw new IllegalArgumentException("A validade do JWT deve ser positiva."); }
        this.expiracao = expiracao;
    }

    public String generateToken(String email) {
        return Jwts.builder().subject(email).issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiracao))
                .signWith(chave, Jwts.SIG.HS256).compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(chave).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(chave).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException erro) {
            return false;
        }
    }
}
