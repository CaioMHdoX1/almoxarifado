package com.almoxaf.api.dto;

public class PosseAtualDTO {

    private String equipamentoNome;
    private String equipamentoCodigo;
    private String usuarioNome;

    public PosseAtualDTO() {
    }

    public PosseAtualDTO(String equipamentoNome, String equipamentoCodigo, String usuarioNome) {
        this.equipamentoNome = equipamentoNome;
        this.equipamentoCodigo = equipamentoCodigo;
        this.usuarioNome = usuarioNome;
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
}
