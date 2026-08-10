package com.almoxaf.api.dto;

public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String cpf;
    private String projeto;
    private String email;

    public UsuarioResponseDTO() {
    }

    public UsuarioResponseDTO(Long id, String nome, String cpf, String projeto, String email) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.projeto = projeto;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
