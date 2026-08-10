package com.almoxaf.api.model;

import java.time.OffsetDateTime;

public class Usuario {

    private Long id;
    private String nome;
    private String cpf;
    private String projeto;
    private String email;
    private String senhaHash;
    private OffsetDateTime criadoEm;

    public Usuario() {
    }

    public Usuario(Long id, String nome, String cpf, String projeto, String email, String senhaHash,
                   OffsetDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.projeto = projeto;
        this.email = email;
        this.senhaHash = senhaHash;
        this.criadoEm = criadoEm;
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

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
