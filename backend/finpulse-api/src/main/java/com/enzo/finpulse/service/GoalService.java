package com.enzo.finpulse.service;

import com.enzo.finpulse.dto.goal.GoalRequest;
import com.enzo.finpulse.dto.goal.GoalResponse;
import com.enzo.finpulse.exception.ResourceNotFoundException;
import com.enzo.finpulse.model.Goal;
import com.enzo.finpulse.model.User;
import com.enzo.finpulse.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

    public List<GoalResponse> listarPorUsuario(User usuario) {
        return goalRepository.findByUserId(usuario.getId())
                .stream()
                .map(GoalResponse::fromEntity)
                .toList();
    }

    public GoalResponse criar(GoalRequest request, User usuario) {
        Goal meta = Goal.builder()
                .titulo(request.titulo())
                .valorObjetivo(request.valorObjetivo())
                .valorAtual(request.valorAtual() != null ? request.valorAtual() : BigDecimal.ZERO)
                .dataLimite(request.dataLimite())
                .user(usuario)
                .build();

        return GoalResponse.fromEntity(goalRepository.save(meta));
    }

    public GoalResponse atualizar(Long id, GoalRequest request, User usuario) {
        Goal meta = buscarOuFalhar(id, usuario);

        meta.setTitulo(request.titulo());
        meta.setValorObjetivo(request.valorObjetivo());
        if (request.valorAtual() != null) {
            meta.setValorAtual(request.valorAtual());
        }
        meta.setDataLimite(request.dataLimite());

        return GoalResponse.fromEntity(goalRepository.save(meta));
    }

    /**
     * Registra um "aporte" (depósito) em direção à meta — ex: o usuário
     * guardou R$ 200 esse mês para a meta "Viagem para o Japão".
     * Separamos isso de "atualizar" porque a ação de aportar é incremental
     * (soma ao valor atual), diferente de editar o título ou prazo.
     */
    public GoalResponse adicionarAporte(Long id, BigDecimal valorAporte, User usuario) {
        Goal meta = buscarOuFalhar(id, usuario);
        meta.setValorAtual(meta.getValorAtual().add(valorAporte));
        return GoalResponse.fromEntity(goalRepository.save(meta));
    }

    public void deletar(Long id, User usuario) {
        Goal meta = buscarOuFalhar(id, usuario);
        goalRepository.delete(meta);
    }

    private Goal buscarOuFalhar(Long id, User usuario) {
        return goalRepository.findByIdAndUserId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Meta não encontrada com id: " + id));
    }
}
