package com.teste.produto.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    private String mensagem;
    private LocalDateTime timestamp;
    private List<CampoErro> campos;

    public ErrorResponse(String mensagem) {
        this.mensagem = mensagem;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String mensagem, List<CampoErro> campos) {
        this.mensagem = mensagem;
        this.timestamp = LocalDateTime.now();
        this.campos = campos;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<CampoErro> getCampos() {
        return campos;
    }

    public void setCampos(List<CampoErro> campos) {
        this.campos = campos;
    }

    public static class CampoErro {
        private String campo;
        private String mensagem;
        private Object valorRejeitado;

        public CampoErro(String campo, String mensagem, Object valorRejeitado) {
            this.campo = campo;
            this.mensagem = mensagem;
            this.valorRejeitado = valorRejeitado;
        }

        public String getCampo() {
            return campo;
        }

        public void setCampo(String campo) {
            this.campo = campo;
        }

        public String getMensagem() {
            return mensagem;
        }

        public void setMensagem(String mensagem) {
            this.mensagem = mensagem;
        }

        public Object getValorRejeitado() {
            return valorRejeitado;
        }

        public void setValorRejeitado(Object valorRejeitado) {
            this.valorRejeitado = valorRejeitado;
        }
    }
}