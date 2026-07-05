package com.enzo.finpulse.dto.category;

import com.enzo.finpulse.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
        @NotBlank(message = "O nome da categoria é obrigatório")
        String nome,

        @NotNull(message = "O tipo (RECEITA ou DESPESA) é obrigatório")
        TransactionType tipo,

        String cor,
        String icone
) {
}
