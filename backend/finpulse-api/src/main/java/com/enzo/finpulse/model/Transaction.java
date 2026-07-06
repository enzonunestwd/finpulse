package com.enzo.finpulse.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representa um lançamento financeiro: uma receita ou despesa específica.
 * É a entidade mais usada do sistema — toda movimentação de dinheiro
 * registrada pelo usuário gera uma Transaction.
 */
@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    // 🚀 COLE ISTO NO LUGAR:
    @Column(name = "category_nome")
    private String category;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String descricao; // ex: "Mercado Extra", "Salário Junho"

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor; // sempre positivo; o sinal (entra/sai) vem do "tipo"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType tipo;

    @Column(nullable = false)
    private LocalDate dataTransacao; // data em que o gasto/receita ocorreu (não é a data de criação do registro)

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @JsonBackReference
    private Account account;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @PrePersist
    protected void aoCriar() {
        this.criadoEm = LocalDateTime.now();
    }
}
