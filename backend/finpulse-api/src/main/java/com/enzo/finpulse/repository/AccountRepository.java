package com.enzo.finpulse.repository;

import com.enzo.finpulse.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // Lista todas as contas de um usuário específico
    List<Account> findByUserId(Long userId);

    // Busca uma conta específica garantindo que pertence ao usuário
    // (importante para segurança: usuário A nunca pode acessar conta do usuário B)
    Optional<Account> findByIdAndUserId(Long id, Long userId);
}
