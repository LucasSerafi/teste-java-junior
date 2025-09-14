package com.teste.produto.repository;

import com.teste.produto.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT p FROM Produto p WHERE p.quantidade <= :quantidade")
    List<Produto> findProdutosComQuantidadeBaixa(@Param("quantidade") Integer quantidade);

    @Query("SELECT p FROM Produto p WHERE p.quantidade <= :quantidade")
    Page<Produto> findProdutosComQuantidadeBaixa(@Param("quantidade") Integer quantidade, Pageable pageable);

    @Query("SELECT SUM(p.preco * p.quantidade) FROM Produto p")
    BigDecimal calcularValorTotalEstoque();

    Page<Produto> findByCategoriaId(Long categoriaId, Pageable pageable);

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByCategoriaId(Long categoriaId);
}

