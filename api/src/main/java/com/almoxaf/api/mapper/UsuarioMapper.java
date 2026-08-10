package com.almoxaf.api.mapper;

import com.almoxaf.api.dto.UsuarioResponseDTO;
import com.almoxaf.api.model.Usuario;

public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static UsuarioResponseDTO paraResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getProjeto(),
                usuario.getEmail());
    }
}
