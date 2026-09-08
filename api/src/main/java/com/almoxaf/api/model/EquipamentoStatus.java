package com.almoxaf.api.model;

/**
 * Espelha a view {@code vw_equipamentos_status} (ver db/init/001_schema.sql)
 * — já traz o status calculado (disponível/alocado) e quem está com o item.
 * Para itens tipo "almoxarifado", status é sempre "disponivel" (o controle
 * ali é por quantidade, não por alocação individual).
 */
public class EquipamentoStatus {

    private Long id;
    private String nome;
    private String codigo;
    private String marca;
    private String categoria;
    private String descricao;
    private String tipo;
    private Integer quantidade;
    private String status; // "disponivel" | "alocado"
    private Long usuarioAtualId;
    private String usuarioAtualNome;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUsuarioAtualId() {
        return usuarioAtualId;
    }

    public void setUsuarioAtualId(Long usuarioAtualId) {
        this.usuarioAtualId = usuarioAtualId;
    }

    public String getUsuarioAtualNome() {
        return usuarioAtualNome;
    }

    public void setUsuarioAtualNome(String usuarioAtualNome) {
        this.usuarioAtualNome = usuarioAtualNome;
    }

    public boolean isDisponivel() {
        return "disponivel".equals(status);
    }

    public boolean isAlmoxarifado() {
        return "almoxarifado".equals(tipo);
    }
}
