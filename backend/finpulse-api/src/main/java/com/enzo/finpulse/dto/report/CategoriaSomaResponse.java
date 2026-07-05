package com.enzo.finpulse.dto.report;

import java.math.BigDecimal;

/**
 * Representa um "fatia" do gráfico de pizza: o total gasto/recebido
 * em uma categoria específica.
 */
public record CategoriaSomaResponse(
        String categoria,
        BigDecimal total
) {
}
