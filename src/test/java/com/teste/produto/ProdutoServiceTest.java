package com.teste.produto;

import com.teste.produto.model.Produto;
import com.teste.produto.repository.ProdutoRepository;
import com.teste.produto.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setDescricao("Descrição do produto teste");
        produto.setPreco(new BigDecimal("99.99"));
        produto.setQuantidade(10);
    }

    @Test
    void testListarTodos() {
        // Arrange
        List<Produto> produtos = Arrays.asList(produto);
        when(produtoRepository.findAll()).thenReturn(produtos);

        // Act
        List<Produto> resultado = produtoService.listarTodos();

        // Assert
        assertEquals(1, resultado.size());
        assertEquals(produto.getNome(), resultado.get(0).getNome());
        verify(produtoRepository).findAll();
    }

    @Test
    void testBuscarPorId() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        // Act
        Optional<Produto> resultado = produtoService.buscarPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(produto.getNome(), resultado.get().getNome());
        verify(produtoRepository).findById(1L);
    }

    @Test
    void testSalvarProdutoNovo() {
        // Arrange
        Produto novoProduto = new Produto();
        novoProduto.setNome("Novo Produto");
        novoProduto.setPreco(new BigDecimal("50.00"));
        novoProduto.setQuantidade(5);

        when(produtoRepository.existsByNomeIgnoreCase(anyString())).thenReturn(false);
        when(produtoRepository.save(any(Produto.class))).thenReturn(novoProduto);

        // Act
        Produto resultado = produtoService.salvar(novoProduto);

        // Assert
        assertNotNull(resultado);
        assertEquals(novoProduto.getNome(), resultado.getNome());
        verify(produtoRepository).existsByNomeIgnoreCase(novoProduto.getNome());
        verify(produtoRepository).save(novoProduto);
    }

    @Test
    void testSalvarProdutoComNomeDuplicado() {
        // Arrange
        Produto novoProduto = new Produto();
        novoProduto.setNome("Produto Existente");

        when(produtoRepository.existsByNomeIgnoreCase(anyString())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> produtoService.salvar(novoProduto)
        );

        assertEquals("Já existe um produto com este nome", exception.getMessage());
        verify(produtoRepository).existsByNomeIgnoreCase(novoProduto.getNome());
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void testAtualizarProduto() {
        // Arrange
        Produto produtoAtualizado = new Produto();
        produtoAtualizado.setNome("Produto Atualizado");
        produtoAtualizado.setDescricao("Nova descrição");
        produtoAtualizado.setPreco(new BigDecimal("199.99"));
        produtoAtualizado.setQuantidade(20);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.existsByNomeIgnoreCase(anyString())).thenReturn(false);
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        // Act
        Produto resultado = produtoService.atualizar(1L, produtoAtualizado);

        // Assert
        assertNotNull(resultado);
        verify(produtoRepository).findById(1L);
        verify(produtoRepository).save(produto);
    }

    @Test
    void testAtualizarProdutoInexistente() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> produtoService.atualizar(1L, produto)
        );

        assertEquals("Produto não encontrado com ID: 1", exception.getMessage());
        verify(produtoRepository).findById(1L);
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void testDeletarProduto() {
        // Arrange
        when(produtoRepository.existsById(1L)).thenReturn(true);

        // Act
        produtoService.deletar(1L);

        // Assert
        verify(produtoRepository).existsById(1L);
        verify(produtoRepository).deleteById(1L);
    }

    @Test
    void testDeletarProdutoInexistente() {
        // Arrange
        when(produtoRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> produtoService.deletar(1L)
        );

        assertEquals("Produto não encontrado com ID: 1", exception.getMessage());
        verify(produtoRepository).existsById(1L);
        verify(produtoRepository, never()).deleteById(any());
    }
}

