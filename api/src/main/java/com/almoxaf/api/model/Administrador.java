package com.almoxaf.api.model;

import java.time.OffsetDateTime;

/**
 * Espelha a tabela {@code administradores} — é quem FAZ LOGIN no sistema.
 * Separado de propósito de {@link Usuario} (que são as pessoas que ficam
 * de posse de um equipamento, mas nunca logam).
 */
public class Administrador {

    private Long id;
    private String nome;
    private String email;
    private String senhaHash;
    private OffsetDateTime criadoEm;

    public Administrador() {
    }

    public Administrador(Long id, String nome, String email, String senhaHash, OffsetDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
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
