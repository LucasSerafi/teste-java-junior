package com.teste.produto.exception;

public class CategoriaComProdutosException extends RuntimeException {
    
    public CategoriaComProdutosException(String message) {
        super(message);
    }
    
    public CategoriaComProdutosException() {
        super("Erro ao deletar categoria, existe produtos associados");
    }
}