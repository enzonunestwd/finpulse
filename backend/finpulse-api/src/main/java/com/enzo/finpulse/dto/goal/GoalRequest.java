package com.enzo.finpulse.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalRequest(
        @NotBlank(message = "O título da meta é obrigatório")
        String titulo,

        @NotNull(message = "O valor objetivo é obrigatório")
        @Positive(message = "O valor objetivo deve ser maior que zero")
        BigDecimal valorObjetivo,

        BigDecimal valorAtual,

        LocalDate dataLimite
) {
}
