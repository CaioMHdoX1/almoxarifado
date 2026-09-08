package com.almoxaf.api.dto;

/** Versão resumida de equipamento, embutida na resposta de busca de usuário. */
public class EquipamentoResumoDTO {

    private Long id;
    private String nome;
    private String codigo;
    private String marca;

    public EquipamentoResumoDTO() {
    }

    public EquipamentoResumoDTO(Long id, String nome, String codigo, String marca) {
        this.id = id;
        this.nome = nome;
        this.codigo = codigo;
        this.marca = marca;
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
}
