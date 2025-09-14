package com.teste.produto.controller;

import com.teste.produto.model.Produto;
import com.teste.produto.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Tag(name = "Produtos", description = "Operações relacionadas ao gerenciamento de produtos")
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProdutoController {
    
    @Autowired
    private ProdutoService produtoService;
    
    @Operation(summary = "Listar todos os produtos", description = "Retorna uma lista com todos os produtos cadastrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        List<Produto> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }
    
    @Operation(summary = "Buscar produto por ID", description = "Retorna um produto específico pelo seu identificador único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(
        @Parameter(description = "ID do produto a ser buscado") @PathVariable Long id) {
        Optional<Produto> produto = produtoService.buscarPorId(id);
        return produto.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Criar novo produto", description = "Cria um novo produto no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<?> criar(
        @Parameter(description = "Dados do produto a ser criado") @Valid @RequestBody Produto produto) {
        try {
            Produto novoProduto = produtoService.salvar(produto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
        @Parameter(description = "ID do produto a ser atualizado") @PathVariable Long id, 
        @Parameter(description = "Novos dados do produto") @Valid @RequestBody Produto produto) {
        try {
            Produto produtoAtualizado = produtoService.atualizar(id, produto);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Deletar produto", description = "Remove um produto do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao deletar produto"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(
        @Parameter(description = "ID do produto a ser deletado") @PathVariable Long id) {
        try {
            produtoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(summary = "Buscar produtos por nome", description = "Busca produtos que contêm o nome especificado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de produtos encontrados")
    })
    @GetMapping("/search")
    public ResponseEntity<List<Produto>> buscarPorNome(
        @Parameter(description = "Nome ou parte do nome do produto") @RequestParam String nome) {
        List<Produto> produtos = produtoService.buscarPorNome(nome);
        return ResponseEntity.ok(produtos);
    }
    
    @Operation(summary = "Buscar produtos com estoque baixo (paginado)", description = "Retorna produtos com quantidade menor ou igual ao valor especificado com paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Página de produtos com estoque baixo")
    })
    @GetMapping("/low-stock")
    public ResponseEntity<Page<Produto>> buscarProdutosComQuantidadeBaixa(
        @Parameter(description = "Quantidade máxima para considerar estoque baixo") @RequestParam(defaultValue = "10") Integer quantidade,
        @Parameter(description = "Número da página (iniciando em 0)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
        @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "quantidade") String sortBy,
        @Parameter(description = "Direção da ordenação (asc ou desc)") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Produto> produtos = produtoService.buscarProdutosComQuantidadeBaixa(quantidade, pageable);
        return ResponseEntity.ok(produtos);
    }

    @Operation(summary = "Calcular valor total do estoque", description = "Retorna o valor total do estoque (preço × quantidade) de todos os produtos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Valor total do estoque calculado")
    })
    @GetMapping("/stock-value")
    public ResponseEntity<BigDecimal> calcularValorTotalEstoque() {
        BigDecimal valorTotal = produtoService.calcularValorTotalEstoque();
        return ResponseEntity.ok(valorTotal);
    }

    @Operation(summary = "Listar produtos por categoria (paginado)", description = "Retorna produtos de uma categoria específica com paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Página de produtos da categoria")
    })
    @GetMapping("/category/{categoriaId}")
    public ResponseEntity<Page<Produto>> buscarProdutosPorCategoria(
        @Parameter(description = "ID da categoria") @PathVariable Long categoriaId,
        @Parameter(description = "Número da página (iniciando em 0)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
        @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "nome") String sortBy,
        @Parameter(description = "Direção da ordenação (asc ou desc)") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Produto> produtos = produtoService.buscarProdutosPorCategoria(categoriaId, pageable);
        return ResponseEntity.ok(produtos);
    }
}

