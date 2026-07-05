package com.enzo.finpulse.controller;

import com.enzo.finpulse.dto.report.DashboardSummaryResponse;
import com.enzo.finpulse.model.User;
import com.enzo.finpulse.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Retorna o resumo financeiro de um período.
     * Exemplo de uso: GET /api/reports/dashboard?inicio=2025-06-01&fim=2025-06-30
     * Se não informar as datas, usa o mês atual por padrão.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardSummaryResponse> dashboard(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @AuthenticationPrincipal User usuario) {

        if (inicio == null) inicio = LocalDate.now().withDayOfMonth(1);
        if (fim == null) fim = LocalDate.now();

        return ResponseEntity.ok(reportService.gerarResumo(usuario, inicio, fim));
    }
}
