package com.teste.produto.exception;

import com.teste.produto.dto.ErrorResponse;
import com.teste.produto.dto.ErrorResponse.CampoErro;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<CampoErro> erros = new ArrayList<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String mensagemAmigavel = traduzirMensagem(error.getDefaultMessage(), error.getField());
            erros.add(new CampoErro(error.getField(), mensagemAmigavel, error.getRejectedValue()));
        });
        
        ErrorResponse response = new ErrorResponse(
            "Os dados fornecidos são inválidos. Verifique os campos e tente novamente.",
            erros
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<CampoErro> erros = new ArrayList<>();
        
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String campo = violation.getPropertyPath().toString();
            String mensagemAmigavel = traduzirMensagem(violation.getMessage(), campo);
            erros.add(new CampoErro(campo, mensagemAmigavel, violation.getInvalidValue()));
        }
        
        ErrorResponse response = new ErrorResponse(
            "Erro de validação nos dados fornecidos.",
            erros
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
            "O recurso solicitado não foi encontrado."
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String mensagem = "Erro de integridade dos dados.";
        
        if (ex.getMessage().contains("unique") || ex.getMessage().contains("duplicate")) {
            mensagem = "Já existe um registro com estas informações.";
        }
        
        ErrorResponse response = new ErrorResponse(mensagem);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(CategoriaComProdutosException.class)
    public ResponseEntity<ErrorResponse> handleCategoriaComProdutos(CategoriaComProdutosException ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(NomeJaExisteException.class)
    public ResponseEntity<ErrorResponse> handleNomeJaExiste(NomeJaExisteException ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse response = new ErrorResponse(
            ex.getMessage() != null ? ex.getMessage() : "Argumento inválido fornecido."
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse response = new ErrorResponse(
            "Ocorreu um erro interno no servidor. Tente novamente mais tarde."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String traduzirMensagem(String mensagemOriginal, String campo) {
        if (mensagemOriginal == null) return "Campo inválido";
        
        if (mensagemOriginal.contains("NotBlank") || mensagemOriginal.contains("obrigatório")) {
            return String.format("O campo '%s' é obrigatório e não pode estar vazio.", formatarCampo(campo));
        }
        
        if (mensagemOriginal.contains("NotNull")) {
            return String.format("O campo '%s' é obrigatório.", formatarCampo(campo));
        }
        
        if (mensagemOriginal.contains("Size")) {
            if (mensagemOriginal.contains("min") && mensagemOriginal.contains("max")) {
                return String.format("O campo '%s' deve ter entre os caracteres permitidos.", formatarCampo(campo));
            }
            return String.format("O campo '%s' possui tamanho inválido.", formatarCampo(campo));
        }
        
        if (mensagemOriginal.contains("DecimalMin") || mensagemOriginal.contains("Min")) {
            return String.format("O campo '%s' deve ter um valor válido.", formatarCampo(campo));
        }
        
        if (mensagemOriginal.contains("Email")) {
            return String.format("O campo '%s' deve conter um email válido.", formatarCampo(campo));
        }
        
        if (mensagemOriginal.contains("categoria")) {
            return "Já existe um produto com este nome nesta categoria.";
        }
        
        return mensagemOriginal.length() > 100 ? 
            String.format("O campo '%s' contém um valor inválido.", formatarCampo(campo)) : 
            mensagemOriginal;
    }

    private String formatarCampo(String campo) {
        if (campo == null) return "campo";
        
        switch (campo.toLowerCase()) {
            case "nome": return "nome";
            case "descricao": return "descrição";
            case "preco": return "preço";
            case "quantidade": return "quantidade";
            case "categoria": return "categoria";
            default: return campo;
        }
    }
}