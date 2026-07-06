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
        Long categoryId,   // Ficará nulo já que agora é texto livre
        String categoryNome,
        String categoryCor // Ficará nulo já que agora é texto livre
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
                null,             // 🧠 MUDOU AQUI: Passa null pois não existe mais ID de tabela
                t.getCategory(),  // 🧠 MUDOU AQUI: Pega a própria String direto da transação
                null              // 🧠 MUDOU AQUI: Passa null pois texto livre não tem cor cadastrada
        );
    }
}