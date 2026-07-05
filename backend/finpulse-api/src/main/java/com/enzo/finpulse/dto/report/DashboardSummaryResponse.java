package com.enzo.finpulse.dto.report;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resumo financeiro de um período (ex: mês atual). É o DTO que alimenta
 * o dashboard principal do frontend: cards de "total recebido", "total
 * gasto", "saldo do período" e os dados para os gráficos.
 */
public record DashboardSummaryResponse(
        BigDecimal totalReceitas,
        BigDecimal totalDespesas,
        BigDecimal saldoPeriodo,
        List<CategoriaSomaResponse> despesasPorCategoria,
        List<CategoriaSomaResponse> receitasPorCategoria
) {
}
