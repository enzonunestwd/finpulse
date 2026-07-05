package com.enzo.finpulse.controller;

import com.enzo.finpulse.dto.transaction.TransactionRequest;
import com.enzo.finpulse.dto.transaction.TransactionResponse;
import com.enzo.finpulse.model.User;
import com.enzo.finpulse.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> listar(@AuthenticationPrincipal User usuario) {
        return ResponseEntity.ok(transactionService.listarPorUsuario(usuario));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> criar(
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal User usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.criar(request, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal User usuario) {
        transactionService.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
