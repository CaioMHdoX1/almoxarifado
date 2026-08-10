package com.almoxaf.api.model;

import java.time.OffsetDateTime;

public class Equipamento {

    private Long id;
    private String nome;
    private String codigo;
    private String marca;
    private String categoria;
    private OffsetDateTime criadoEm;

    public Equipamento() {
    }

    public Equipamento(Long id, String nome, String codigo, String marca, String categoria,
                        OffsetDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.codigo = codigo;
        this.marca = marca;
        this.categoria = categoria;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
