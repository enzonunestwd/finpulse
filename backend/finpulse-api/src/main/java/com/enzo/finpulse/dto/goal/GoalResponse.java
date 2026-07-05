package com.enzo.finpulse.dto.goal;

import com.enzo.finpulse.model.Goal;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalResponse(
        Long id,
        String titulo,
        BigDecimal valorObjetivo,
        BigDecimal valorAtual,
        BigDecimal progresso, // percentual de 0 a 100, calculado
        LocalDate dataLimite
) {
    public static GoalResponse fromEntity(Goal goal) {
        return new GoalResponse(
                goal.getId(),
                goal.getTitulo(),
                goal.getValorObjetivo(),
                goal.getValorAtual(),
                goal.getProgresso(),
                goal.getDataLimite()
        );
    }
}
