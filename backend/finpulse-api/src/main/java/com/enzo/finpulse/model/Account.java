package com.enzo.finpulse.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa uma conta financeira do usuário: conta corrente, cartão de
 * crédito, investimento, etc. Cada transação está sempre ligada a uma conta.
 */
@Entity
@Table(name = "accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome; // ex: "Nubank", "Cartão Inter", "Reserva de emergência"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountType tipo;

    // Saldo atual da conta. Usamos BigDecimal (nunca double/float) para
    // evitar erros de arredondamento em valores monetários.
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference // evita loop infinito ao serializar User -> Account -> User -> ...
    private User user;

    @PrePersist
    protected void aoCriar() {
        this.criadoEm = LocalDateTime.now();
        if (this.saldo == null) {
            this.saldo = BigDecimal.ZERO;
        }
    }
}
