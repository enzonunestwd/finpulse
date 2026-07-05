package com.enzo.finpulse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Representa um usuário do sistema FinPulse.
 *
 * Cada usuário tem suas próprias contas (Account), categorias (Category),
 * transações (Transaction) e metas financeiras (Goal). Isso garante que
 * os dados de um usuário nunca se misturem com os de outro.
 *
 * Implementamos UserDetails (interface do Spring Security) diretamente
 * nessa entidade. Isso evita criar uma classe extra só para representar
 * "o usuário autenticado" — o Spring Security usa essa mesma classe
 * para saber quem está logado em cada requisição.
 */
@Entity
@Table(name = "users")
@Data // Lombok: gera getters, setters, toString, equals e hashCode automaticamente
@Builder // Lombok: permite criar objetos com User.builder().nome("x").build()
@NoArgsConstructor // Construtor vazio, exigido pelo JPA
@AllArgsConstructor // Construtor com todos os campos, usado pelo @Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String senhaHash; // nunca guardamos a senha em texto puro, só o hash (BCrypt)

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    // Um usuário possui várias contas (relação 1:N)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Account> contas = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Category> categorias = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Goal> metas = new ArrayList<>();

    @PrePersist
    protected void aoCriar() {
        this.criadoEm = LocalDateTime.now();
    }

    // ---- Métodos exigidos pela interface UserDetails (Spring Security) ----
    // Eles dizem ao Spring Security "como" verificar esse usuário durante o login.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Por enquanto não temos sistema de papéis/permissões (ex: ADMIN, USER),
        // então devolvemos uma lista vazia. Se no futuro quisermos diferenciar
        // permissões, é aqui que adicionaríamos as "roles".
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return this.senhaHash;
    }

    @Override
    public String getUsername() {
        // Usamos o email como "username" para fins de login no Spring Security
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
