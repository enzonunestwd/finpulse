package com.enzo.finpulse.dto.auth;

/**
 * Resposta devolvida após login ou cadastro bem-sucedido.
 * Contém o token JWT que o frontend vai guardar e enviar em todas
 * as próximas requisições (no cabeçalho Authorization).
 */
public record AuthResponse(
        String token,
        Long userId,
        String nome,
        String email
) {
}
