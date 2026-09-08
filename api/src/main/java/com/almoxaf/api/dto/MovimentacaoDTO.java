package com.almoxaf.api.dto;

import java.time.OffsetDateTime;

public class MovimentacaoDTO {

    private String equipamentoNome;
    private String equipamentoCodigo;
    private String usuarioNome;
    private String usuarioCpf;
    private OffsetDateTime data;

    public MovimentacaoDTO() {
    }

    public MovimentacaoDTO(String equipamentoNome, String equipamentoCodigo, String usuarioNome,
                            String usuarioCpf, OffsetDateTime data) {
        this.equipamentoNome = equipamentoNome;
        this.equipamentoCodigo = equipamentoCodigo;
        this.usuarioNome = usuarioNome;
        this.usuarioCpf = usuarioCpf;
        this.data = data;
    }

    public String getEquipamentoNome() {
        return equipamentoNome;
    }

    public void setEquipamentoNome(String equipamentoNome) {
        this.equipamentoNome = equipamentoNome;
    }

    public String getEquipamentoCodigo() {
        return equipamentoCodigo;
    }

    public void setEquipamentoCodigo(String equipamentoCodigo) {
        this.equipamentoCodigo = equipamentoCodigo;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }

    public String getUsuarioCpf() {
        return usuarioCpf;
    }

    public void setUsuarioCpf(String usuarioCpf) {
        this.usuarioCpf = usuarioCpf;
    }

    public OffsetDateTime getData() {
        return data;
    }

    public void setData(OffsetDateTime data) {
        this.data = data;
    }
}
