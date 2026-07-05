package com.enzo.finpulse.dto.transaction;

import com.enzo.finpulse.model.Transaction;
import com.enzo.finpulse.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,
        String descricao,
        BigDecimal valor,
        TransactionType tipo,
        LocalDate dataTransacao,
        Long accountId,
        String accountNome,
        Long categoryId,
        String categoryNome,
        String categoryCor
) {
    public static TransactionResponse fromEntity(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getDescricao(),
                t.getValor(),
                t.getTipo(),
                t.getDataTransacao(),
                t.getAccount().getId(),
                t.getAccount().getNome(),
                t.getCategory().getId(),
                t.getCategory().getNome(),
                t.getCategory().getCor()
        );
    }
}
