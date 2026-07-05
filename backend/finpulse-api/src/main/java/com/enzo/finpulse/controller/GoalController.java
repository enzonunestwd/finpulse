package com.enzo.finpulse.controller;

import com.enzo.finpulse.dto.goal.GoalRequest;
import com.enzo.finpulse.dto.goal.GoalResponse;
import com.enzo.finpulse.model.User;
import com.enzo.finpulse.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @GetMapping
    public ResponseEntity<List<GoalResponse>> listar(@AuthenticationPrincipal User usuario) {
        return ResponseEntity.ok(goalService.listarPorUsuario(usuario));
    }

    @PostMapping
    public ResponseEntity<GoalResponse> criar(
            @Valid @RequestBody GoalRequest request,
            @AuthenticationPrincipal User usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goalService.criar(request, usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody GoalRequest request,
            @AuthenticationPrincipal User usuario) {
        return ResponseEntity.ok(goalService.atualizar(id, request, usuario));
    }

    @PatchMapping("/{id}/aporte")
    public ResponseEntity<GoalResponse> adicionarAporte(
            @PathVariable Long id,
            @RequestParam BigDecimal valor,
            @AuthenticationPrincipal User usuario) {
        return ResponseEntity.ok(goalService.adicionarAporte(id, valor, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal User usuario) {
        goalService.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
