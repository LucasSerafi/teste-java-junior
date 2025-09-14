package com.teste.produto.controller;

import com.teste.produto.model.Categoria;
import com.teste.produto.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Categorias", description = "Operações relacionadas ao gerenciamento de categorias de produtos")
@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {
	 private final CategoriaService categoriaService;

	    public CategoriaController(CategoriaService categoriaService) {
	        this.categoriaService = categoriaService;
	    }

	    @Operation(summary = "Listar todas as categorias", description = "Retorna uma lista com todas as categorias cadastradas")
	    @ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso")
	    })
	    @GetMapping
	    public List<Categoria> listarTodas() {
	        return categoriaService.listarTodas();
	    }

	    @Operation(summary = "Criar nova categoria", description = "Cria uma nova categoria no sistema")
	    @ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Categoria criada com sucesso"),
	        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
	    })
	    @PostMapping
	    public Categoria criar(
	        @Parameter(description = "Dados da categoria a ser criada") @RequestBody Categoria categoria) {
	        return categoriaService.salvar(categoria);
	    }

	    @Operation(summary = "Buscar categoria por ID", description = "Retorna uma categoria específica pelo ID")
	    @ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
	        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
	    })
	    @GetMapping("/{id}")
	    public Categoria buscarPorId(
	        @Parameter(description = "ID da categoria") @PathVariable Long id) {
	        Optional<Categoria> categoria = categoriaService.buscarPorId(id);
	        return categoria.orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
	    }

	    @Operation(summary = "Atualizar categoria", description = "Atualiza os dados de uma categoria existente")
	    @ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
	        @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
	        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
	    })
	    @PutMapping("/{id}")
	    public Categoria atualizar(
	        @Parameter(description = "ID da categoria") @PathVariable Long id,
	        @Parameter(description = "Dados atualizados da categoria") @RequestBody Categoria categoria) {
	        return categoriaService.atualizar(id, categoria);
	    }

	    @Operation(summary = "Deletar categoria", description = "Remove uma categoria do sistema")
	    @ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Categoria deletada com sucesso"),
	        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
	    })
	    @DeleteMapping("/{id}")
	    public void deletar(
	        @Parameter(description = "ID da categoria a ser deletada") @PathVariable Long id) {
	        categoriaService.deletar(id);
	    }

}

