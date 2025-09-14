package com.teste.produto.service;

import com.teste.produto.exception.NomeJaExisteException;
import com.teste.produto.model.Produto;
import com.teste.produto.repository.CategoriaRepository;
import com.teste.produto.repository.ProdutoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProdutoService {
    
    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;
    
    public List<Produto> listarTodos() {
        log.info("Listando todos os produtos");
        return produtoRepository.findAll();
    }
    
    public Optional<Produto> buscarPorId(Long id) {
        log.info("Buscando produto por ID: {}", id);
        return produtoRepository.findById(id);
    }
    
    public Produto salvar(Produto produto) {
        log.info("Salvando produto: {}", produto.getNome());
        if (produtoRepository.existsByNomeIgnoreCase(produto.getNome())) {
            throw new NomeJaExisteException("Já existe um produto com este nome");
        }
        return produtoRepository.save(produto);
    }
    
    public Produto atualizar(Long id, Produto produtoAtualizado) {
        log.info("Atualizando produto ID: {} com nome: {}", id, produtoAtualizado.getNome());
        Optional<Produto> produtoExistente = produtoRepository.findById(id);
        if (produtoExistente.isPresent()) {
            Produto produto = produtoExistente.get();

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
        log.info("Deletando produto ID: {}", id);
        if (produtoRepository.existsById(id)) {
            produtoRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Produto não encontrado com ID: " + id);
        }
    }
    
    public List<Produto> buscarPorNome(String nome) {
        log.info("Buscando produtos por nome: {}", nome);
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    public List<Produto> buscarProdutosComQuantidadeBaixa(Integer quantidade) {
        log.info("Buscando produtos com quantidade baixa: {}", quantidade);
        return produtoRepository.findProdutosComQuantidadeBaixa(quantidade);
    }

    public Page<Produto> buscarProdutosComQuantidadeBaixa(Integer quantidade, Pageable pageable) {
        log.info("Buscando produtos com quantidade baixa (paginado): quantidade={}, página={}", quantidade, pageable.getPageNumber());
        return produtoRepository.findProdutosComQuantidadeBaixa(quantidade, pageable);
    }

    public BigDecimal calcularValorTotalEstoque() {
        log.info("Calculando valor total do estoque");
        BigDecimal total = produtoRepository.calcularValorTotalEstoque();
        return total != null ? total : BigDecimal.ZERO;
    }

    public Page<Produto> buscarProdutosPorCategoria(Long categoriaId, Pageable pageable) {
        log.info("Buscando produtos por categoria ID: {}, página: {}", categoriaId, pageable.getPageNumber());
        return produtoRepository.findByCategoriaId(categoriaId, pageable);
    }
}

