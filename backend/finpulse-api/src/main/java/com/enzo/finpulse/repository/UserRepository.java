package com.enzo.finpulse.repository;

import com.enzo.finpulse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA gera a query automaticamente a partir do nome do método.
    // "findByEmail" -> SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
