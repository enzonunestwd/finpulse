package com.enzo.finpulse.dto.transaction;

import com.enzo.finpulse.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
        @NotBlank(message = "A descrição é obrigatória")
        String descricao,

        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser maior que zero")
        BigDecimal valor,

        @NotNull(message = "O tipo (RECEITA ou DESPESA) é obrigatório")
        TransactionType tipo,

        @NotNull(message = "A data da transação é obrigatória")
        LocalDate dataTransacao,

        @NotNull(message = "A conta é obrigatória")
        Long accountId,

        @NotBlank(message = "A categoria é obrigatória")
        String categoryNome
) {
}
