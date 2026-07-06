package com.enzo.finpulse.repository;

import com.enzo.finpulse.model.Transaction;
import com.enzo.finpulse.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdOrderByDataTransacaoDesc(Long userId);

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    List<Transaction> findByUserIdAndDataTransacaoBetween(
            Long userId, LocalDate inicio, LocalDate fim);

    List<Transaction> findByAccountId(Long accountId);

    // Soma o total de receitas ou despesas de um usuário em um período.
    // Usado para montar os relatórios e gráficos do dashboard.
    @Query("""
            SELECT COALESCE(SUM(t.valor), 0) FROM Transaction t
            WHERE t.user.id = :userId
            AND t.tipo = :tipo
            AND t.dataTransacao BETWEEN :inicio AND :fim
            """)
    BigDecimal somarPorTipoEPeriodo(
            @Param("userId") Long userId,
            @Param("tipo") TransactionType tipo,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    // Agrupa o total gasto/recebido por categoria em um período.
    // Resultado: lista de [nomeCategoria, totalSomado] — usada no gráfico de pizza.
    @Query("""
            SELECT t.category AS categoria, COALESCE(SUM(t.valor), 0) AS total
            FROM Transaction t
            WHERE t.user.id = :userId
            AND t.tipo = :tipo
            AND t.dataTransacao BETWEEN :inicio AND :fim
            GROUP BY t.category
            ORDER BY total DESC
            """)
    List<Object[]> somarPorCategoriaEPeriodo(
            @Param("userId") Long userId,
            @Param("tipo") TransactionType tipo,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);
}