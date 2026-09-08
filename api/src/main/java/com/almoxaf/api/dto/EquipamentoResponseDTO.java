package com.almoxaf.api.dto;

/** Formato completo de equipamento que a API expõe (listagem, editar, remover). */
public class EquipamentoResponseDTO {

    private Long id;
    private String nome;
    private String codigo;
    private String marca;
    private String descricao;
    private String tipo; // "equipamento" | "almoxarifado"
    private Integer quantidade; // só preenchido quando tipo = "almoxarifado"
    private String status; // "disponivel" | "alocado"
    private UsuarioAtualDTO usuarioAtual; // null se disponível ou se for "almoxarifado"

    public EquipamentoResponseDTO() {
    }

    public EquipamentoResponseDTO(Long id, String nome, String codigo, String marca, String descricao,
                                   String tipo, Integer quantidade, String status,
                                   UsuarioAtualDTO usuarioAtual) {
        this.id = id;
        this.nome = nome;
        this.codigo = codigo;
        this.marca = marca;
        this.descricao = descricao;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.status = status;
        this.usuarioAtual = usuarioAtual;
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

    public UsuarioAtualDTO getUsuarioAtual() {
        return usuarioAtual;
    }

    public void setUsuarioAtual(UsuarioAtualDTO usuarioAtual) {
        this.usuarioAtual = usuarioAtual;
    }

    /** Só o essencial de quem está com o equipamento — não o usuário inteiro. */
    public record UsuarioAtualDTO(Long id, String nome) {
    }
}
