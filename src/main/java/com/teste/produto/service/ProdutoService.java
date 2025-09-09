package com.teste.produto.service;

import com.teste.produto.model.Produto;
import com.teste.produto.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }
    
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }
    
    public Produto salvar(Produto produto) {
        // Validação de negócio: verificar se já existe produto com o mesmo nome
        if (produto.getId() == null && produtoRepository.existsByNomeIgnoreCase(produto.getNome())) {
            throw new IllegalArgumentException("Já existe um produto com este nome");
        }
        return produtoRepository.save(produto);
    }
    
    public Produto atualizar(Long id, Produto produtoAtualizado) {
        Optional<Produto> produtoExistente = produtoRepository.findById(id);
        if (produtoExistente.isPresent()) {
            Produto produto = produtoExistente.get();
            
            // Verificar se o nome não está sendo usado por outro produto
            if (!produto.getNome().equalsIgnoreCase(produtoAtualizado.getNome()) && 
                produtoRepository.existsByNomeIgnoreCase(produtoAtualizado.getNome())) {
                throw new IllegalArgumentException("Já existe um produto com este nome");
            }
            
            produto.setNome(produtoAtualizado.getNome());
            produto.setDescricao(produtoAtualizado.getDescricao());
            produto.setPreco(produtoAtualizado.getPreco());
            produto.setQuantidade(produtoAtualizado.getQuantidade());
            
            return produtoRepository.save(produto);
        } else {
            throw new IllegalArgumentException("Produto não encontrado com ID: " + id);
        }
    }
    
    public void deletar(Long id) {
        if (produtoRepository.existsById(id)) {
            produtoRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Produto não encontrado com ID: " + id);
        }
    }
    
    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    public List<Produto> buscarProdutosComQuantidadeBaixa(Integer quantidade) {
        return produtoRepository.findProdutosComQuantidadeBaixa(quantidade);
    }
}

