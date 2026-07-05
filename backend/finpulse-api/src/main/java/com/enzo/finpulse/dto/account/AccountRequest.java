package com.enzo.finpulse.dto.account;

import com.enzo.finpulse.model.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AccountRequest(
        @NotBlank(message = "O nome da conta é obrigatório")
        String nome,

        @NotNull(message = "O tipo da conta é obrigatório")
        AccountType tipo,

        BigDecimal saldoInicial
) {
}
