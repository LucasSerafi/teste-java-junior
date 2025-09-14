package com.teste.produto.exception;

public class NomeJaExisteException extends RuntimeException {

    public NomeJaExisteException(String message) {
        super(message);
    }

    public NomeJaExisteException() {
        super("Erro ao deletar categoria, existe produtos associados");
    }
}