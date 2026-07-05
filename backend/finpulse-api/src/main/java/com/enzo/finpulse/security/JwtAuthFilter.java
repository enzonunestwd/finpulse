package com.enzo.finpulse.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que roda UMA VEZ por requisição (OncePerRequestFilter), ANTES
 * de qualquer Controller. É aqui que a "mágica" do JWT acontece:
 *
 * 1. Lê o cabeçalho "Authorization" da requisição.
 * 2. Se houver um token "Bearer ...", extrai e valida.
 * 3. Se for válido, registra no SecurityContext do Spring que esse
 *    usuário está autenticado para esta requisição.
 * 4. Se não houver token, ou for inválido, simplesmente deixa passar
 *    sem autenticar — e quem decide se a rota exige autenticação é a
 *    configuração do SecurityConfig (rotas protegidas vão retornar 401
 *    automaticamente se chegarem aqui sem autenticação válida).
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final FinpulseUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Se não tem cabeçalho Authorization ou não começa com "Bearer ", deixa passar
        // (a rota vai ser bloqueada depois, na configuração de segurança, se for protegida)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // remove o prefixo "Bearer "
        String email = jwtService.extrairEmail(token);

        // Só autentica se: (1) conseguimos extrair um email do token, e
        // (2) ainda não há ninguém autenticado nesse contexto de requisição
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtService.isTokenValido(token, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // credenciais nulas: já validamos via token, não precisa da senha aqui
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
