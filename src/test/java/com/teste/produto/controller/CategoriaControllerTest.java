package com.teste.produto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teste.produto.model.Categoria;
import com.teste.produto.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaService categoriaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Categoria Teste");
        categoria.setDescricao("Descrição da categoria teste");
    }

    @Test
    void testListarTodas() throws Exception {
        // Arrange
        List<Categoria> categorias = Arrays.asList(categoria);
        when(categoriaService.listarTodas()).thenReturn(categorias);

        // Act & Assert
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("Categoria Teste"))
                .andExpect(jsonPath("$[0].descricao").value("Descrição da categoria teste"));

        verify(categoriaService).listarTodas();
    }

    @Test
    void testCriar() throws Exception {
        // Arrange
        Categoria novaCategoria = new Categoria();
        novaCategoria.setNome("Nova Categoria");
        novaCategoria.setDescricao("Nova descrição");

        Categoria categoriaSalva = new Categoria();
        categoriaSalva.setId(2L);
        categoriaSalva.setNome("Nova Categoria");
        categoriaSalva.setDescricao("Nova descrição");

        when(categoriaService.salvar(any(Categoria.class))).thenReturn(categoriaSalva);

        // Act & Assert
        mockMvc.perform(post("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novaCategoria)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.nome").value("Nova Categoria"))
                .andExpect(jsonPath("$.descricao").value("Nova descrição"));

        verify(categoriaService).salvar(any(Categoria.class));
    }

    @Test
    void testBuscarPorIdExistente() throws Exception {
        // Arrange
        when(categoriaService.buscarPorId(1L)).thenReturn(Optional.of(categoria));

        // Act & Assert
        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Categoria Teste"))
                .andExpect(jsonPath("$.descricao").value("Descrição da categoria teste"));

        verify(categoriaService).buscarPorId(1L);
    }

    @Test
    void testBuscarPorIdInexistente() throws Exception {
        // Arrange
        when(categoriaService.buscarPorId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/categorias/999"))
                .andExpect(status().isInternalServerError());

        verify(categoriaService).buscarPorId(999L);
    }

    @Test
    void testAtualizar() throws Exception {
        // Arrange
        Categoria categoriaAtualizada = new Categoria();
        categoriaAtualizada.setNome("Categoria Atualizada");
        categoriaAtualizada.setDescricao("Descrição atualizada");

        Categoria categoriaRetorno = new Categoria();
        categoriaRetorno.setId(1L);
        categoriaRetorno.setNome("Categoria Atualizada");
        categoriaRetorno.setDescricao("Descrição atualizada");

        when(categoriaService.atualizar(eq(1L), any(Categoria.class))).thenReturn(categoriaRetorno);

        // Act & Assert
        mockMvc.perform(put("/api/categorias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoriaAtualizada)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Categoria Atualizada"))
                .andExpect(jsonPath("$.descricao").value("Descrição atualizada"));

        verify(categoriaService).atualizar(eq(1L), any(Categoria.class));
    }

    @Test
    void testAtualizarCategoriaInexistente() throws Exception {
        // Arrange
        Categoria categoriaAtualizada = new Categoria();
        categoriaAtualizada.setNome("Categoria Atualizada");
        categoriaAtualizada.setDescricao("Descrição atualizada");

        when(categoriaService.atualizar(eq(999L), any(Categoria.class)))
                .thenThrow(new IllegalArgumentException("Categoria não encontrada com ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/api/categorias/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoriaAtualizada)))
                .andExpect(status().isBadRequest());

        verify(categoriaService).atualizar(eq(999L), any(Categoria.class));
    }

    @Test
    void testDeletar() throws Exception {
        // Arrange
        doNothing().when(categoriaService).deletar(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isOk());

        verify(categoriaService).deletar(1L);
    }

    @Test
    void testDeletarCategoriaInexistente() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Categoria não encontrada com ID: 999"))
                .when(categoriaService).deletar(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/categorias/999"))
                .andExpect(status().isBadRequest());

        verify(categoriaService).deletar(999L);
    }

    @Test
    void testDeletarCategoriaComProdutos() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Não é possível deletar categoria com produtos associados"))
                .when(categoriaService).deletar(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isBadRequest());

        verify(categoriaService).deletar(1L);
    }
}