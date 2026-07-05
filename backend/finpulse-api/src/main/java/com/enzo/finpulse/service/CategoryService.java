package com.enzo.finpulse.service;

import com.enzo.finpulse.dto.category.CategoryRequest;
import com.enzo.finpulse.dto.category.CategoryResponse;
import com.enzo.finpulse.exception.ResourceNotFoundException;
import com.enzo.finpulse.model.Category;
import com.enzo.finpulse.model.User;
import com.enzo.finpulse.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> listarPorUsuario(User usuario) {
        return categoryRepository.findByUserId(usuario.getId())
                .stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    public CategoryResponse criar(CategoryRequest request, User usuario) {
        Category categoria = Category.builder()
                .nome(request.nome())
                .tipo(request.tipo())
                .cor(request.cor())
                .icone(request.icone())
                .user(usuario)
                .build();

        Category categoriaSalva = categoryRepository.save(categoria);
        return CategoryResponse.fromEntity(categoriaSalva);
    }

    public CategoryResponse atualizar(Long id, CategoryRequest request, User usuario) {
        Category categoria = buscarOuFalhar(id, usuario);

        categoria.setNome(request.nome());
        categoria.setTipo(request.tipo());
        categoria.setCor(request.cor());
        categoria.setIcone(request.icone());

        return CategoryResponse.fromEntity(categoryRepository.save(categoria));
    }

    public void deletar(Long id, User usuario) {
        Category categoria = buscarOuFalhar(id, usuario);
        categoryRepository.delete(categoria);
    }

    private Category buscarOuFalhar(Long id, User usuario) {
        return categoryRepository.findByIdAndUserId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria não encontrada com id: " + id));
    }
}
