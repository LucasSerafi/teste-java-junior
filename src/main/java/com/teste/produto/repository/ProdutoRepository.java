package com.teste.produto.repository;

import com.teste.produto.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    // Buscar produtos por nome (case insensitive)
    List<Produto> findByNomeContainingIgnoreCase(String nome);
    
    // Buscar produtos com quantidade baixa
    @Query("SELECT p FROM Produto p WHERE p.quantidade <= :quantidade")
    List<Produto> findProdutosComQuantidadeBaixa(@Param("quantidade") Integer quantidade);
    
    // Verificar se existe produto com o mesmo nome (para evitar duplicatas)
    boolean existsByNomeIgnoreCase(String nome);
}

