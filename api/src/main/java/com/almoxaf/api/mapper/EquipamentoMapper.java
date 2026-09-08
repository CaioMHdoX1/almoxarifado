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
                status.getDescricao(),
                status.getTipo(),
                status.getQuantidade(),
                status.getStatus(),
                usuarioAtual);
    }

    /** Usado logo após criar/editar um equipamento — nunca tem alocação nesse momento. */
    public static EquipamentoResponseDTO paraResponseDTORecemCriado(Equipamento equipamento) {
        // Itens "almoxarifado" nunca são alocados a alguém (controle é por
        // quantidade), então o status deles é sempre "disponivel".
        return new EquipamentoResponseDTO(
                equipamento.getId(),
                equipamento.getNome(),
                equipamento.getCodigo(),
                equipamento.getMarca(),
                equipamento.getDescricao(),
                equipamento.getTipo(),
                equipamento.getQuantidade(),
                "disponivel",
                null);
    }
}
