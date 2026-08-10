package com.almoxaf.api.dto;

import java.util.List;

public class EquipamentoBuscaResultadoDTO {

    private String nome;
    private long total;
    private long disponiveis;
    private long alocados;
    private List<EquipamentoResponseDTO> itens;

    public EquipamentoBuscaResultadoDTO() {
    }

    public EquipamentoBuscaResultadoDTO(String nome, long total, long disponiveis, long alocados,
                                         List<EquipamentoResponseDTO> itens) {
        this.nome = nome;
        this.total = total;
        this.disponiveis = disponiveis;
        this.alocados = alocados;
        this.itens = itens;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getDisponiveis() {
        return disponiveis;
    }

    public void setDisponiveis(long disponiveis) {
        this.disponiveis = disponiveis;
    }

    public long getAlocados() {
        return alocados;
    }

    public void setAlocados(long alocados) {
        this.alocados = alocados;
    }

    public List<EquipamentoResponseDTO> getItens() {
        return itens;
    }

    public void setItens(List<EquipamentoResponseDTO> itens) {
        this.itens = itens;
    }
}
