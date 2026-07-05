package com.enzo.finpulse.dto.category;

import com.enzo.finpulse.model.Category;
import com.enzo.finpulse.model.TransactionType;

public record CategoryResponse(
        Long id,
        String nome,
        TransactionType tipo,
        String cor,
        String icone
) {
    public static CategoryResponse fromEntity(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getNome(),
                category.getTipo(),
                category.getCor(),
                category.getIcone()
        );
    }
}
