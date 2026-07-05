package com.enzo.finpulse.controller;

import com.enzo.finpulse.dto.account.AccountRequest;
import com.enzo.finpulse.dto.account.AccountResponse;
import com.enzo.finpulse.model.User;
import com.enzo.finpulse.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponse>> listar(@AuthenticationPrincipal User usuario) {
        return ResponseEntity.ok(accountService.listarPorUsuario(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal User usuario) {
        return ResponseEntity.ok(accountService.buscarPorId(id, usuario));
    }

    @PostMapping
    public ResponseEntity<AccountResponse> criar(
            @Valid @RequestBody AccountRequest request,
            @AuthenticationPrincipal User usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.criar(request, usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AccountRequest request,
            @AuthenticationPrincipal User usuario) {
        return ResponseEntity.ok(accountService.atualizar(id, request, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal User usuario) {
        accountService.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
