package com.teste.produto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teste.produto.model.Categoria;
import com.teste.produto.model.Produto;
import com.teste.produto.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoController.class)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoService produtoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Produto produto;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Categoria Teste");

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setDescricao("Descrição do produto teste");
        produto.setPreco(new BigDecimal("99.99"));
        produto.setQuantidade(10);
        produto.setCategoria(categoria);
    }

    @Test
    void testListarTodos() throws Exception {
        // Arrange
        List<Produto> produtos = Arrays.asList(produto);
        when(produtoService.listarTodos()).thenReturn(produtos);

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("Produto Teste"))
                .andExpect(jsonPath("$[0].preco").value(99.99))
                .andExpect(jsonPath("$[0].quantidade").value(10));

        verify(produtoService).listarTodos();
    }

    @Test
    void testBuscarPorIdExistente() throws Exception {
        // Arrange
        when(produtoService.buscarPorId(1L)).thenReturn(Optional.of(produto));

        // Act & Assert
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Produto Teste"))
                .andExpect(jsonPath("$.preco").value(99.99))
                .andExpect(jsonPath("$.quantidade").value(10));

        verify(produtoService).buscarPorId(1L);
    }

    @Test
    void testBuscarPorIdInexistente() throws Exception {
        // Arrange
        when(produtoService.buscarPorId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(produtoService).buscarPorId(999L);
    }

    @Test
    void testCriar() throws Exception {
        // Arrange
        Produto novoProduto = new Produto();
        novoProduto.setNome("Novo Produto");
        novoProduto.setDescricao("Nova descrição");
        novoProduto.setPreco(new BigDecimal("149.99"));
        novoProduto.setQuantidade(5);
        novoProduto.setCategoria(categoria);

        Produto produtoSalvo = new Produto();
        produtoSalvo.setId(2L);
        produtoSalvo.setNome("Novo Produto");
        produtoSalvo.setDescricao("Nova descrição");
        produtoSalvo.setPreco(new BigDecimal("149.99"));
        produtoSalvo.setQuantidade(5);
        produtoSalvo.setCategoria(categoria);

        when(produtoService.salvar(any(Produto.class))).thenReturn(produtoSalvo);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoProduto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.nome").value("Novo Produto"))
                .andExpect(jsonPath("$.preco").value(149.99))
                .andExpect(jsonPath("$.quantidade").value(5));

        verify(produtoService).salvar(any(Produto.class));
    }

    @Test
    void testCriarComErro() throws Exception {
        // Arrange
        Produto novoProduto = new Produto();
        novoProduto.setNome("Produto Inválido");
        // Deixa preco e quantidade nulos para causar erro de validação

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoProduto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").exists());

        // Não verifica o service porque a validação falha antes
        verify(produtoService, never()).salvar(any(Produto.class));
    }

    @Test
    void testCriarComErroDoService() throws Exception {
        // Arrange
        Produto novoProduto = new Produto();
        novoProduto.setNome("Produto Inválido");
        novoProduto.setDescricao("Descrição");
        novoProduto.setPreco(new BigDecimal("99.99"));
        novoProduto.setQuantidade(10);
        novoProduto.setCategoria(categoria);

        when(produtoService.salvar(any(Produto.class)))
                .thenThrow(new IllegalArgumentException("Dados inválidos"));

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoProduto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Dados inválidos"));

        verify(produtoService).salvar(any(Produto.class));
    }

    @Test
    void testAtualizar() throws Exception {
        // Arrange
        Produto produtoAtualizado = new Produto();
        produtoAtualizado.setNome("Produto Atualizado");
        produtoAtualizado.setDescricao("Descrição atualizada");
        produtoAtualizado.setPreco(new BigDecimal("199.99"));
        produtoAtualizado.setQuantidade(15);
        produtoAtualizado.setCategoria(categoria);

        Produto produtoRetorno = new Produto();
        produtoRetorno.setId(1L);
        produtoRetorno.setNome("Produto Atualizado");
        produtoRetorno.setDescricao("Descrição atualizada");
        produtoRetorno.setPreco(new BigDecimal("199.99"));
        produtoRetorno.setQuantidade(15);
        produtoRetorno.setCategoria(categoria);

        when(produtoService.atualizar(eq(1L), any(Produto.class))).thenReturn(produtoRetorno);

        // Act & Assert
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produtoAtualizado)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Produto Atualizado"))
                .andExpect(jsonPath("$.preco").value(199.99))
                .andExpect(jsonPath("$.quantidade").value(15));

        verify(produtoService).atualizar(eq(1L), any(Produto.class));
    }

    @Test
    void testAtualizarComErro() throws Exception {
        // Arrange
        Produto produtoAtualizado = new Produto();
        produtoAtualizado.setNome("Produto Atualizado");
        // Deixa preco e quantidade nulos para causar erro de validação

        // Act & Assert
        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produtoAtualizado)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").exists());

        // Não verifica o service porque a validação falha antes
        verify(produtoService, never()).atualizar(eq(999L), any(Produto.class));
    }

    @Test
    void testAtualizarComErroDoService() throws Exception {
        // Arrange
        Produto produtoAtualizado = new Produto();
        produtoAtualizado.setNome("Produto Atualizado");
        produtoAtualizado.setDescricao("Descrição");
        produtoAtualizado.setPreco(new BigDecimal("99.99"));
        produtoAtualizado.setQuantidade(10);
        produtoAtualizado.setCategoria(categoria);

        when(produtoService.atualizar(eq(999L), any(Produto.class)))
                .thenThrow(new IllegalArgumentException("Produto não encontrado"));

        // Act & Assert
        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produtoAtualizado)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Produto não encontrado"));

        verify(produtoService).atualizar(eq(999L), any(Produto.class));
    }

    @Test
    void testDeletar() throws Exception {
        // Arrange
        doNothing().when(produtoService).deletar(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(produtoService).deletar(1L);
    }

    @Test
    void testDeletarComErro() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Produto não encontrado"))
                .when(produtoService).deletar(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Produto não encontrado"));

        verify(produtoService).deletar(999L);
    }

    @Test
    void testBuscarPorNome() throws Exception {
        // Arrange
        List<Produto> produtos = Arrays.asList(produto);
        when(produtoService.buscarPorNome("Teste")).thenReturn(produtos);

        // Act & Assert
        mockMvc.perform(get("/api/products/search")
                .param("nome", "Teste"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Produto Teste"));

        verify(produtoService).buscarPorNome("Teste");
    }

    @Test
    void testBuscarProdutosComQuantidadeBaixa() throws Exception {
        // Arrange
        List<Produto> produtos = Arrays.asList(produto);
        Page<Produto> page = new PageImpl<>(produtos, PageRequest.of(0, 10), 1);
        when(produtoService.buscarProdutosComQuantidadeBaixa(eq(10), any(Pageable.class)))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/products/low-stock")
                .param("quantidade", "10")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nome").value("Produto Teste"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(produtoService).buscarProdutosComQuantidadeBaixa(eq(10), any(Pageable.class));
    }

    @Test
    void testCalcularValorTotalEstoque() throws Exception {
        // Arrange
        BigDecimal valorTotal = new BigDecimal("1500.00");
        when(produtoService.calcularValorTotalEstoque()).thenReturn(valorTotal);

        // Act & Assert
        mockMvc.perform(get("/api/products/stock-value"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("1500.00"));

        verify(produtoService).calcularValorTotalEstoque();
    }

    @Test
    void testBuscarProdutosPorCategoria() throws Exception {
        // Arrange
        List<Produto> produtos = Arrays.asList(produto);
        Page<Produto> page = new PageImpl<>(produtos, PageRequest.of(0, 10), 1);
        when(produtoService.buscarProdutosPorCategoria(eq(1L), any(Pageable.class)))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/products/category/1")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nome").value("Produto Teste"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(produtoService).buscarProdutosPorCategoria(eq(1L), any(Pageable.class));
    }
}