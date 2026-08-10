package com.almoxaf.api.mapper;

import com.almoxaf.api.dto.EquipamentoResponseDTO;
import com.almoxaf.api.dto.EquipamentoResumoDTO;
import com.almoxaf.api.model.Equipamento;
import com.almoxaf.api.model.EquipamentoStatus;

public final class EquipamentoMapper {

    private EquipamentoMapper() {
    }

    public static EquipamentoResumoDTO paraResumoDTO(Equipamento equipamento) {
        return new EquipamentoResumoDTO(
                equipamento.getId(),
                equipamento.getNome(),
                equipamento.getCodigo(),
                equipamento.getMarca());
    }

    public static EquipamentoResponseDTO paraResponseDTO(EquipamentoStatus status) {
        EquipamentoResponseDTO.UsuarioAtualDTO usuarioAtual = status.getUsuarioAtualId() != null
                ? new EquipamentoResponseDTO.UsuarioAtualDTO(status.getUsuarioAtualId(), status.getUsuarioAtualNome())
                : null;

        return new EquipamentoResponseDTO(
                status.getId(),
                status.getNome(),
                status.getCodigo(),
                status.getMarca(),
                status.getStatus(),
                usuarioAtual);
    }

    public static EquipamentoResponseDTO paraResponseDTORecemCriado(Equipamento equipamento) {
        return new EquipamentoResponseDTO(
                equipamento.getId(),
                equipamento.getNome(),
                equipamento.getCodigo(),
                equipamento.getMarca(),
                "disponivel",
                null);
    }
}
