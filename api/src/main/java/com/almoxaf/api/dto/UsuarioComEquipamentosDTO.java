package com.almoxaf.api.dto;

import java.util.List;

public class UsuarioComEquipamentosDTO {

    private UsuarioResponseDTO usuario;
    private List<EquipamentoResumoDTO> equipamentos;

    public UsuarioComEquipamentosDTO() {
    }

    public UsuarioComEquipamentosDTO(UsuarioResponseDTO usuario, List<EquipamentoResumoDTO> equipamentos) {
        this.usuario = usuario;
        this.equipamentos = equipamentos;
    }

    public UsuarioResponseDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResponseDTO usuario) {
        this.usuario = usuario;
    }

    public List<EquipamentoResumoDTO> getEquipamentos() {
        return equipamentos;
    }

    public void setEquipamentos(List<EquipamentoResumoDTO> equipamentos) {
        this.equipamentos = equipamentos;
    }
}
