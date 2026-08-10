package com.almoxaf.api.model;

public class EquipamentoStatus {

    private Long id;
    private String nome;
    private String codigo;
    private String marca;
    private String status;
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
}
