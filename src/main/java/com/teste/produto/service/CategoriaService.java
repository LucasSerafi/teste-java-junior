package com.teste.produto.service;

import com.teste.produto.exception.CategoriaComProdutosException;
import com.teste.produto.model.Categoria;
import com.teste.produto.repository.CategoriaRepository;
import com.teste.produto.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class CategoriaService {
	private final CategoriaRepository categoriaRepository;
	private final ProdutoRepository produtoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProdutoRepository produtoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Categoria salvar(Categoria categoria) {
        if (categoriaRepository.existsByNomeIgnoreCase(categoria.getNome())) {
            throw new IllegalArgumentException("Já existe uma categoria com este nome");
        }
        return categoriaRepository.save(categoria);
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public Categoria atualizar(Long id, Categoria categoria) {
        Optional<Categoria> optionalCategoria = buscarPorId(id);
        if (optionalCategoria.isEmpty()) {
            throw new IllegalArgumentException("Categoria não encontrada com ID: " + id);
        }

        Categoria categoriaExistente = optionalCategoria.get();

        if (!categoriaExistente.getNome().equals(categoria.getNome()) &&
            categoriaRepository.existsByNomeIgnoreCase(categoria.getNome())) {
            throw new IllegalArgumentException("Já existe uma categoria com este nome");
        }

        categoriaExistente.setNome(categoria.getNome());
        categoriaExistente.setDescricao(categoria.getDescricao());
        return categoriaRepository.save(categoriaExistente);
    }

    public void deletar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new IllegalArgumentException("Categoria não encontrada com ID: " + id);
        }
        if (produtoRepository.existsByCategoriaId(id)) {
            throw new IllegalArgumentException("Não é possível deletar categoria com produtos associados");
        }
        categoriaRepository.deleteById(id);
    }

}
