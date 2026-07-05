package com.enzo.finpulse.service;

import com.enzo.finpulse.dto.auth.AuthResponse;
import com.enzo.finpulse.dto.auth.LoginRequest;
import com.enzo.finpulse.dto.auth.RegisterRequest;
import com.enzo.finpulse.exception.BusinessException;
import com.enzo.finpulse.model.User;
import com.enzo.finpulse.repository.UserRepository;
import com.enzo.finpulse.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Contém as regras de negócio relacionadas a autenticação: cadastro de
 * novos usuários e login.
 *
 * Esse é um bom exemplo de por que separamos Service de Controller: o
 * Controller só vai "traduzir" HTTP em chamadas de método; toda a lógica
 * de "o que significa registrar um usuário" mora aqui.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse registrar(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Já existe uma conta cadastrada com esse e-mail");
        }

        User novoUsuario = User.builder()
                .nome(request.nome())
                .email(request.email())
                // NUNCA salvamos a senha em texto puro — o BCryptPasswordEncoder
                // gera um hash unidirecional (não é possível "descriptografar" de volta)
                .senhaHash(passwordEncoder.encode(request.senha()))
                .build();

        User usuarioSalvo = userRepository.save(novoUsuario);

        String token = jwtService.gerarToken(usuarioSalvo.getEmail());

        return new AuthResponse(
                token,
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail()
        );
    }

    public AuthResponse login(LoginRequest request) {
        // O AuthenticationManager faz o trabalho pesado: busca o usuário pelo
        // email (via UserDetailsService) e compara a senha informada com o
        // hash salvo no banco (via PasswordEncoder). Se não bater, ele lança
        // BadCredentialsException automaticamente — capturada pelo
        // GlobalExceptionHandler e transformada em HTTP 401.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );

        User usuario = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("E-mail ou senha inválidos"));

        String token = jwtService.gerarToken(usuario.getEmail());

        return new AuthResponse(
                token,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }
}
