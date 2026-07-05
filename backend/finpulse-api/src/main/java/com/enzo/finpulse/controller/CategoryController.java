package com.enzo.finpulse.controller;

import com.enzo.finpulse.dto.category.CategoryRequest;
import com.enzo.finpulse.dto.category.CategoryResponse;
import com.enzo.finpulse.model.User;
import com.enzo.finpulse.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> listar(@AuthenticationPrincipal User usuario) {
        return ResponseEntity.ok(categoryService.listarPorUsuario(usuario));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> criar(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal User usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.criar(request, usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal User usuario) {
        return ResponseEntity.ok(categoryService.atualizar(id, request, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal User usuario) {
        categoryService.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
