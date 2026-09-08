package com.almoxaf.api.dto;

/** Corpo esperado em POST /api/usuarios. */
public class UsuarioCreateRequestDTO {

    private String nome;
    private String cpf;
    private String projeto;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getProjeto() {
        return projeto;
    }

    public void setProjeto(String projeto) {
        this.projeto = projeto;
    }
}
