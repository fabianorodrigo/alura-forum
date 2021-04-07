package br.com.alura.forum.controller.dto;

public class TokenDTO {

    private String token;
    private String tipoAutenticacao;

    public TokenDTO(String token, String tipo) {
        this.token = token;
        this.tipoAutenticacao = tipo;
    }

    public String getToken() {
        return token;
    }

    public String getTipoAutenticacao() {
        return tipoAutenticacao;
    }

}
