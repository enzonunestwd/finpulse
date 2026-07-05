package com.enzo.finpulse.dto.account;

import com.enzo.finpulse.model.Account;
import com.enzo.finpulse.model.AccountType;

import java.math.BigDecimal;

/**
 * DTO de resposta. Convertendo a entidade Account para esse record,
 * evitamos expor o objeto "user" inteiro (que viria junto se devolvêssemos
 * a entidade diretamente) e controlamos exatamente o formato do JSON.
 */
public record AccountResponse(
        Long id,
        String nome,
        AccountType tipo,
        BigDecimal saldo
) {
    // Método "fábrica": converte uma entidade Account em um AccountResponse.
    // Centralizar essa conversão aqui evita repetir esse código em vários services.
    public static AccountResponse fromEntity(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getNome(),
                account.getTipo(),
                account.getSaldo()
        );
    }
}
