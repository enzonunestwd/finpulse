package com.enzo.finpulse.exception;

/**
 * Lançada quando uma regra de negócio é violada — por exemplo,
 * tentar cadastrar um usuário com e-mail que já existe, ou tentar
 * fazer login com credenciais inválidas.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String mensagem) {
        super(mensagem);
    }
}
