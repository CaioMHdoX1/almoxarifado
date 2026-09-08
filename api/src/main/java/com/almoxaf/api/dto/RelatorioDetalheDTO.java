package com.almoxaf.api.dto;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Conteúdo completo de um relatório — é isto que fica serializado em JSON
 * na coluna {@code relatorios.detalhes_json}.
 */
public class RelatorioDetalheDTO {

    private Long id;
    private OffsetDateTime periodoInicio;
    private OffsetDateTime periodoFim;
    private OffsetDateTime geradoEm;
    private List<MovimentacaoDTO> entregas;
    private List<MovimentacaoDTO> devolucoes;
    private List<PosseAtualDTO> posseAtual; // "quem está com o quê" no momento da geração

    public RelatorioDetalheDTO() {
    }

    public RelatorioDetalheDTO(Long id, OffsetDateTime periodoInicio, OffsetDateTime periodoFim,
                                OffsetDateTime geradoEm, List<MovimentacaoDTO> entregas,
                                List<MovimentacaoDTO> devolucoes, List<PosseAtualDTO> posseAtual) {
        this.id = id;
        this.periodoInicio = periodoInicio;
        this.periodoFim = periodoFim;
        this.geradoEm = geradoEm;
        this.entregas = entregas;
        this.devolucoes = devolucoes;
        this.posseAtual = posseAtual;
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

    public List<MovimentacaoDTO> getEntregas() {
        return entregas;
    }

    public void setEntregas(List<MovimentacaoDTO> entregas) {
        this.entregas = entregas;
    }

    public List<MovimentacaoDTO> getDevolucoes() {
        return devolucoes;
    }

    public void setDevolucoes(List<MovimentacaoDTO> devolucoes) {
        this.devolucoes = devolucoes;
    }

    public List<PosseAtualDTO> getPosseAtual() {
        return posseAtual;
    }

    public void setPosseAtual(List<PosseAtualDTO> posseAtual) {
        this.posseAtual = posseAtual;
    }
}
