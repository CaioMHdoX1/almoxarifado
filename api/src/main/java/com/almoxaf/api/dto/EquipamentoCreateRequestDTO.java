package com.almoxaf.api.dto;

/** Corpo esperado em POST /api/equipamentos e PUT /api/equipamentos/{id}. */
public class EquipamentoCreateRequestDTO {

    private String nome;
    private String codigo;
    private String marca;
    private String descricao;
    private String tipo; // "equipamento" | "almoxarifado" — default "equipamento" se omitido
    private Integer quantidade; // obrigatório quando tipo = "almoxarifado"

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
