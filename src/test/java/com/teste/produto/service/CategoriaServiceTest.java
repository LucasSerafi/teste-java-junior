package com.teste.produto.service;

import com.teste.produto.model.Categoria;
import com.teste.produto.repository.CategoriaRepository;
import com.teste.produto.repository.ProdutoRepository;
import com.teste.produto.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Categoria Teste");
        categoria.setDescricao("Descrição da categoria teste");
    }

    @Test
    void testListarTodas() {
        // Arrange
        List<Categoria> categorias = Arrays.asList(categoria);
        when(categoriaRepository.findAll()).thenReturn(categorias);

        // Act
        List<Categoria> resultado = categoriaService.listarTodas();

        // Assert
        assertEquals(1, resultado.size());
        assertEquals(categoria.getNome(), resultado.get(0).getNome());
        verify(categoriaRepository).findAll();
    }

    @Test
    void testBuscarPorId() {
        // Arrange
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // Act
        Optional<Categoria> resultado = categoriaService.buscarPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(categoria.getNome(), resultado.get().getNome());
        verify(categoriaRepository).findById(1L);
    }

    @Test
    void testSalvarCategoriaNova() {
        // Arrange
        Categoria novaCategoria = new Categoria();
        novaCategoria.setNome("Nova Categoria");
        novaCategoria.setDescricao("Nova descrição");

        when(categoriaRepository.existsByNomeIgnoreCase(anyString())).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(novaCategoria);

        // Act
        Categoria resultado = categoriaService.salvar(novaCategoria);

        // Assert
        assertNotNull(resultado);
        assertEquals(novaCategoria.getNome(), resultado.getNome());
        verify(categoriaRepository).existsByNomeIgnoreCase(novaCategoria.getNome());
        verify(categoriaRepository).save(novaCategoria);
    }

    @Test
    void testSalvarCategoriaComNomeDuplicado() {
        // Arrange
        Categoria novaCategoria = new Categoria();
        novaCategoria.setNome("Categoria Existente");

        when(categoriaRepository.existsByNomeIgnoreCase(anyString())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoriaService.salvar(novaCategoria)
        );

        assertEquals("Já existe uma categoria com este nome", exception.getMessage());
        verify(categoriaRepository).existsByNomeIgnoreCase(novaCategoria.getNome());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void testAtualizarCategoria() {
        // Arrange
        Categoria categoriaAtualizada = new Categoria();
        categoriaAtualizada.setNome("Categoria Atualizada");
        categoriaAtualizada.setDescricao("Nova descrição");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsByNomeIgnoreCase(anyString())).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        // Act
        Categoria resultado = categoriaService.atualizar(1L, categoriaAtualizada);

        // Assert
        assertNotNull(resultado);
        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void testAtualizarCategoriaInexistente() {
        // Arrange
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoriaService.atualizar(1L, categoria)
        );

        assertEquals("Categoria não encontrada com ID: 1", exception.getMessage());
        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void testDeletarCategoria() {
        // Arrange
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(produtoRepository.existsByCategoriaId(1L)).thenReturn(false);

        // Act
        categoriaService.deletar(1L);

        // Assert
        verify(categoriaRepository).existsById(1L);
        verify(produtoRepository).existsByCategoriaId(1L);
        verify(categoriaRepository).deleteById(1L);
    }

    @Test
    void testDeletarCategoriaInexistente() {
        // Arrange
        when(categoriaRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoriaService.deletar(1L)
        );

        assertEquals("Categoria não encontrada com ID: 1", exception.getMessage());
        verify(categoriaRepository).existsById(1L);
        verify(categoriaRepository, never()).deleteById(any());
    }

    @Test
    void testDeletarCategoriaComProdutosAssociados() {
        // Arrange
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(produtoRepository.existsByCategoriaId(1L)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoriaService.deletar(1L)
        );

        assertEquals("Não é possível deletar categoria com produtos associados", exception.getMessage());
        verify(categoriaRepository).existsById(1L);
        verify(produtoRepository).existsByCategoriaId(1L);
        verify(categoriaRepository, never()).deleteById(any());
    }
}