package com.enzo.finpulse.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa uma meta financeira do usuário, ex: "Juntar R$ 5.000 para
 * viagem até dezembro". O valorAtual é atualizado conforme o usuário
 * registra aportes (ou podemos calcular automaticamente a partir de
 * transações vinculadas — isso fica para uma versão futura).
 */
@Entity
@Table(name = "goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo; // ex: "Reserva de emergência", "Viagem para o Japão"

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorObjetivo;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal valorAtual = BigDecimal.ZERO;

    private LocalDate dataLimite; // opcional: prazo para alcançar a meta

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    /**
     * Calcula o percentual de progresso da meta (0 a 100).
     * Método utilitário usado pelo DTO de resposta — não é persistido no banco.
     */
    @Transient
    public BigDecimal getProgresso() {
        if (valorObjetivo == null || valorObjetivo.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal atual = valorAtual == null ? BigDecimal.ZERO : valorAtual;
        return atual
                .divide(valorObjetivo, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
