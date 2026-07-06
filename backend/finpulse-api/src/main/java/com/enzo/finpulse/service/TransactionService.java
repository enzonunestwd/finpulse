package com.enzo.finpulse.service;

import com.enzo.finpulse.dto.transaction.TransactionRequest;
import com.enzo.finpulse.dto.transaction.TransactionResponse;
import com.enzo.finpulse.exception.ResourceNotFoundException;
import com.enzo.finpulse.model.*;
import com.enzo.finpulse.repository.AccountRepository;
import com.enzo.finpulse.repository.CategoryRepository;
import com.enzo.finpulse.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contém a regra de negócio mais sensível do sistema: registrar uma
 * transação e, ao mesmo tempo, manter o saldo da conta sempre correto.
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public List<TransactionResponse> listarPorUsuario(User usuario) {
        return transactionRepository.findByUserIdOrderByDataTransacaoDesc(usuario.getId())
                .stream()
                .map(TransactionResponse::fromEntity)
                .toList();
    }

    /**
     * @Transactional garante que, se qualquer parte deste método falhar
     * (ex: erro ao salvar a transação depois de já termos alterado o saldo
     * em memória), TODAS as alterações no banco são desfeitas (rollback).
     * Isso é essencial aqui: não podemos correr o risco de salvar uma
     * transação SEM atualizar o saldo da conta correspondente, ou vice-versa
     * — isso deixaria o sistema em um estado financeiro inconsistente.
     */
    @Transactional
    public TransactionResponse criar(TransactionRequest request, User usuario) {
        Account conta = accountRepository.findByIdAndUserId(request.accountId(), usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conta não encontrada com id: " + request.accountId()));

        // O bloco de buscar a categoria por ID foi removido daqui, pois agora ela é um texto livre.

        Transaction transacao = Transaction.builder()
                .descricao(request.descricao())
                .valor(request.valor())
                .tipo(request.tipo())
                .dataTransacao(request.dataTransacao())
                .account(conta)
                .category(request.categoryNome()) // Recebe a String enviada pelo front-end diretamente
                .user(usuario)
                .build();

        Transaction transacaoSalva = transactionRepository.save(transacao);

        // Atualiza o saldo da conta de acordo com o tipo da transação.
        // RECEITA soma, DESPESA subtrai.
        atualizarSaldoConta(conta, request.valor(), request.tipo());

        return TransactionResponse.fromEntity(transacaoSalva);
    }

    @Transactional
    public void deletar(Long id, User usuario) {
        Transaction transacao = transactionRepository.findByIdAndUserId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transação não encontrada com id: " + id));

        // Ao deletar uma transação, precisamos "desfazer" o efeito dela no saldo.
        // Por isso invertemos o tipo: se era uma RECEITA, ao remover, subtraímos
        // do saldo; se era uma DESPESA, ao remover, devolvemos (somamos) o valor.
        TransactionType tipoInvertido = transacao.getTipo() == TransactionType.RECEITA
                ? TransactionType.DESPESA
                : TransactionType.RECEITA;

        atualizarSaldoConta(transacao.getAccount(), transacao.getValor(), tipoInvertido);

        transactionRepository.delete(transacao);
    }

    private void atualizarSaldoConta(Account conta, BigDecimal valor, TransactionType tipo) {
        BigDecimal novoSaldo = tipo == TransactionType.RECEITA
                ? conta.getSaldo().add(valor)
                : conta.getSaldo().subtract(valor);

        conta.setSaldo(novoSaldo);
        accountRepository.save(conta);
    }
}