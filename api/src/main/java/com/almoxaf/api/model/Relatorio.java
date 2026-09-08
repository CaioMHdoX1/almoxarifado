package com.almoxaf.api.model;

import java.time.OffsetDateTime;

/** Espelha a tabela {@code relatorios} — snapshot gerado a cada 15 dias (ou sob demanda). */
public class Relatorio {

    private Long id;
    private OffsetDateTime periodoInicio;
    private OffsetDateTime periodoFim;
    private OffsetDateTime geradoEm;
    private int totalEntregas;
    private int totalDevolucoes;
    private String detalhesJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OffsetDateTime getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(OffsetDateTime periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public OffsetDateTime getPeriodoFim() {
        return periodoFim;
    }

    public void setPeriodoFim(OffsetDateTime periodoFim) {
        this.periodoFim = periodoFim;
    }

    public OffsetDateTime getGeradoEm() {
        return geradoEm;
    }

    public void setGeradoEm(OffsetDateTime geradoEm) {
        this.geradoEm = geradoEm;
    }

    public int getTotalEntregas() {
        return totalEntregas;
    }

    public void setTotalEntregas(int totalEntregas) {
        this.totalEntregas = totalEntregas;
    }

    public int getTotalDevolucoes() {
        return totalDevolucoes;
    }

    public void setTotalDevolucoes(int totalDevolucoes) {
        this.totalDevolucoes = totalDevolucoes;
    }

    public String getDetalhesJson() {
        return detalhesJson;
    }

    public void setDetalhesJson(String detalhesJson) {
        this.detalhesJson = detalhesJson;
    }
}
