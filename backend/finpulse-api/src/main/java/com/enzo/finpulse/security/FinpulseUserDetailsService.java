package com.enzo.finpulse.security;

import com.enzo.finpulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementação da interface UserDetailsService do Spring Security.
 * Sempre que o Spring Security precisa "carregar" um usuário (durante o
 * login, ou ao validar um token JWT em uma requisição), ele chama o método
 * loadUserByUsername — que, no nosso caso, busca pelo email no banco.
 */
@Service
@RequiredArgsConstructor // Lombok: gera um construtor que injeta os campos "final" abaixo
public class FinpulseUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado com o e-mail: " + email));
    }
}
