package com.enzo.finpulse.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Categoria usada para classificar transações: "Alimentação", "Salário",
 * "Transporte", "Lazer", etc. Cada categoria pertence a um tipo
 * (RECEITA ou DESPESA), o que ajuda nos relatórios e gráficos.
 */
@Entity
@Table(name = "categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType tipo;

    @Column(length = 20)
    private String cor; // código hex usado pelo frontend nos gráficos, ex: "#FF6B6B"

    @Column(length = 40)
    private String icone; // nome do ícone (ex: "shopping-cart"), usado pelo frontend

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;
}
