package com.almoxaf.api.model;

import java.time.OffsetDateTime;

/**
 * Espelha a tabela {@code usuarios} — PESSOAS que podem estar de posse de
 * um equipamento. Não fazem login (isso é exclusividade de
 * {@link Administrador}), por isso não têm email nem senha.
 */
public class Usuario {

    private Long id;
    private String nome;
    private String cpf; // só dígitos
    private String projeto;
    private OffsetDateTime criadoEm;

    public Usuario() {
    }

    public Usuario(Long id, String nome, String cpf, String projeto, OffsetDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.projeto = projeto;
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

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
