package com.almoxaf.api.dto;

import java.time.OffsetDateTime;

public class RelatorioResumoDTO {

    private Long id;
    private OffsetDateTime periodoInicio;
    private OffsetDateTime periodoFim;
    private OffsetDateTime geradoEm;
    private int totalEntregas;
    private int totalDevolucoes;

    public RelatorioResumoDTO() {
    }

    public RelatorioResumoDTO(Long id, OffsetDateTime periodoInicio, OffsetDateTime periodoFim,
                               OffsetDateTime geradoEm, int totalEntregas, int totalDevolucoes) {
        this.id = id;
        this.periodoInicio = periodoInicio;
        this.periodoFim = periodoFim;
        this.geradoEm = geradoEm;
        this.totalEntregas = totalEntregas;
        this.totalDevolucoes = totalDevolucoes;
    }

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
}
