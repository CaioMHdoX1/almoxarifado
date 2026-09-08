package com.almoxaf.api.mapper;

import com.almoxaf.api.dto.AdministradorResponseDTO;
import com.almoxaf.api.model.Administrador;

public final class AdministradorMapper {

    private AdministradorMapper() {
    }

    public static AdministradorResponseDTO paraResponseDTO(Administrador administrador) {
        return new AdministradorResponseDTO(administrador.getId(), administrador.getNome(), administrador.getEmail());
    }
}
