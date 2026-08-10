package com.almoxaf.api.service;

import com.almoxaf.api.dto.UsuarioResponseDTO;
import com.almoxaf.api.exception.CredenciaisInvalidasException;
import com.almoxaf.api.mapper.UsuarioMapper;
import com.almoxaf.api.model.Usuario;
import com.almoxaf.api.repository.UsuarioRepository;
import com.almoxaf.api.util.PasswordHasher;

import java.util.Optional;

public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService() {
        this(new UsuarioRepository());
    }

    /** Construtor usado pelos testes, para injetar um repository mockado. */
    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

   
    public UsuarioResponseDTO autenticar(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new CredenciaisInvalidasException("Informe email e senha.");
        }

        Optional<Usuario> usuarioEncontrado = usuarioRepository.buscarPorEmail(email);

        Usuario usuario = usuarioEncontrado
                .filter(u -> u.getSenhaHash() != null)
                .filter(u -> PasswordHasher.verificar(senha, u.getSenhaHash()))
                .orElseThrow(() -> new CredenciaisInvalidasException("Email ou senha inválidos."));

        return UsuarioMapper.paraResponseDTO(usuario);
    }
}
