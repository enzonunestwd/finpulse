package com.enzo.finpulse.service;

import com.enzo.finpulse.dto.report.CategoriaSomaResponse;
import com.enzo.finpulse.dto.report.DashboardSummaryResponse;
import com.enzo.finpulse.model.TransactionType;
import com.enzo.finpulse.model.User;
import com.enzo.finpulse.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;

    /**
     * Monta o resumo do dashboard para um período (ex: mês atual).
     * Esse método faz várias consultas ao banco e "empacota" tudo em um
     * único DTO — assim o frontend faz UMA chamada e recebe tudo que
     * precisa para montar a tela inicial (cards + gráficos).
     */
    public DashboardSummaryResponse gerarResumo(User usuario, LocalDate inicio, LocalDate fim) {
        BigDecimal totalReceitas = transactionRepository.somarPorTipoEPeriodo(
                usuario.getId(), TransactionType.RECEITA, inicio, fim);

        BigDecimal totalDespesas = transactionRepository.somarPorTipoEPeriodo(
                usuario.getId(), TransactionType.DESPESA, inicio, fim);

        BigDecimal saldoPeriodo = totalReceitas.subtract(totalDespesas);

        List<CategoriaSomaResponse> despesasPorCategoria = mapearParaCategoriaSoma(
                transactionRepository.somarPorCategoriaEPeriodo(
                        usuario.getId(), TransactionType.DESPESA, inicio, fim));

        List<CategoriaSomaResponse> receitasPorCategoria = mapearParaCategoriaSoma(
                transactionRepository.somarPorCategoriaEPeriodo(
                        usuario.getId(), TransactionType.RECEITA, inicio, fim));

        return new DashboardSummaryResponse(
                totalReceitas,
                totalDespesas,
                saldoPeriodo,
                despesasPorCategoria,
                receitasPorCategoria
        );
    }

    /**
     * A query @Query no repositório devolve List<Object[]> (cada posição
     * do array é uma coluna do SELECT: [0] = categoria, [1] = total).
     * Esse método converte esse formato "genérico" para o DTO tipado
     * que o resto da aplicação usa — mantendo o Object[] isolado só
     * aqui, sem "vazar" para o Controller ou para o frontend.
     */
    private List<CategoriaSomaResponse> mapearParaCategoriaSoma(List<Object[]> resultado) {
        return resultado.stream()
                .map(linha -> new CategoriaSomaResponse(
                        (String) linha[0],
                        (BigDecimal) linha[1]
                ))
                .toList();
    }
}
