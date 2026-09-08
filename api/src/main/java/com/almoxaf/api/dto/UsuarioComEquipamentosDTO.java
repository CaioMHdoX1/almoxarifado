package com.almoxaf.api.dto;

import java.util.List;

/**
 * Resposta de {@code GET /api/usuarios?nome=...} — exatamente o que a
 * especificação pediu: ao buscar um usuário por nome, vêm junto todos os
 * equipamentos atualmente alocados a ele.
 */
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
