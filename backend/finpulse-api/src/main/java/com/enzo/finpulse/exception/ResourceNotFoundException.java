package com.enzo.finpulse.exception;

/**
 * Lançada quando um recurso buscado (conta, categoria, transação, meta)
 * não existe ou não pertence ao usuário autenticado.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mensagem) {
        super(mensagem);
    }
}
