package com.enzo.finpulse.service;

import com.enzo.finpulse.dto.account.AccountRequest;
import com.enzo.finpulse.dto.account.AccountResponse;
import com.enzo.finpulse.exception.ResourceNotFoundException;
import com.enzo.finpulse.model.Account;
import com.enzo.finpulse.model.User;
import com.enzo.finpulse.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<AccountResponse> listarPorUsuario(User usuario) {
        return accountRepository.findByUserId(usuario.getId())
                .stream()
                .map(AccountResponse::fromEntity)
                .toList();
    }

    public AccountResponse buscarPorId(Long id, User usuario) {
        Account conta = buscarOuFalhar(id, usuario);
        return AccountResponse.fromEntity(conta);
    }

    public AccountResponse criar(AccountRequest request, User usuario) {
        Account conta = Account.builder()
                .nome(request.nome())
                .tipo(request.tipo())
                .saldo(request.saldoInicial() != null ? request.saldoInicial() : BigDecimal.ZERO)
                .user(usuario)
                .build();

        Account contaSalva = accountRepository.save(conta);
        return AccountResponse.fromEntity(contaSalva);
    }

    public AccountResponse atualizar(Long id, AccountRequest request, User usuario) {
        Account conta = buscarOuFalhar(id, usuario);

        conta.setNome(request.nome());
        conta.setTipo(request.tipo());
        // Note que NÃO atualizamos o saldo aqui diretamente — o saldo é
        // alterado através do registro de transações (ver TransactionService),
        // não editado manualmente. Isso preserva a consistência: o saldo
        // sempre reflete a soma real das transações da conta.

        Account contaAtualizada = accountRepository.save(conta);
        return AccountResponse.fromEntity(contaAtualizada);
    }

    public void deletar(Long id, User usuario) {
        Account conta = buscarOuFalhar(id, usuario);
        accountRepository.delete(conta);
    }

    /**
     * Método auxiliar reutilizado por todos os métodos acima.
     * Centraliza a regra de segurança: a conta só é encontrada se
     * pertencer ao usuário autenticado (ver explicação no caderno).
     */
    private Account buscarOuFalhar(Long id, User usuario) {
        return accountRepository.findByIdAndUserId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conta não encontrada com id: " + id));
    }
}
