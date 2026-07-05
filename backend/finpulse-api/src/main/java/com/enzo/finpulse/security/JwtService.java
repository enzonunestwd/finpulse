package com.enzo.finpulse.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

/**
 * Responsável por gerar e validar os tokens JWT (JSON Web Token).
 *
 * Como funciona o JWT, em resumo:
 * 1. Usuário faz login com email/senha.
 * 2. Se as credenciais estão corretas, geramos um "token" — uma string
 *    assinada digitalmente que contém o email do usuário e uma validade.
 * 3. O frontend guarda esse token e envia em toda requisição futura,
 *    no cabeçalho "Authorization: Bearer <token>".
 * 4. O backend valida a assinatura do token a cada requisição — se for
 *    válida e não tiver expirado, sabemos quem é o usuário sem precisar
 *    consultar o banco de dados nem guardar "sessão" em memória.
 *
 * Essa abordagem é chamada de autenticação "stateless" (sem estado) —
 * é o padrão usado por APIs REST modernas, em vez de sessions tradicionais.
 */
@Component
public class JwtService {

    // Chave secreta usada para assinar e validar os tokens.
    // Em produção, isso vem de variável de ambiente, NUNCA fica hardcoded no código.
    @Value("${jwt.secret}")
    private String secretKey;

    // Tempo de validade do token, em milissegundos (ex: 24h = 86400000)
    @Value("${jwt.expiration}")
    private long expirationMs;

    public String extrairEmail(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    public boolean isTokenValido(String token, String emailEsperado) {
        String email = extrairEmail(token);
        return email.equals(emailEsperado) && !isTokenExpirado(token);
    }

    public String gerarToken(String email) {
        return Jwts.builder()
                .setSubject(email) // "subject" do token = identificador do usuário (email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getChaveAssinatura(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpirado(String token) {
        Date expiracao = extrairClaim(token, Claims::getExpiration);
        return expiracao.before(new Date());
    }

    private <T> T extrairClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extrairTodosClaims(token);
        return resolver.apply(claims);
    }

    private Claims extrairTodosClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getChaveAssinatura())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getChaveAssinatura() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
