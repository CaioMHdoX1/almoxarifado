package com.almoxaf.api.service;

import com.almoxaf.api.dto.AdministradorResponseDTO;
import com.almoxaf.api.exception.CredenciaisInvalidasException;
import com.almoxaf.api.mapper.AdministradorMapper;
import com.almoxaf.api.model.Administrador;
import com.almoxaf.api.repository.AdministradorRepository;
import com.almoxaf.api.util.PasswordHasher;

import java.util.Optional;

public class AuthService {

    private final AdministradorRepository administradorRepository;

    public AuthService() {
        this(new AdministradorRepository());
    }

    /** Construtor usado pelos testes, para injetar um repository mockado. */
    public AuthService(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    /**
     * Valida email/senha e devolve os dados públicos do administrador
     * autenticado. Não abre a sessão HTTP aqui — isso é responsabilidade do
     * {@code AuthServlet} (camada que conhece {@code HttpServletRequest}).
     */
    public AdministradorResponseDTO autenticar(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new CredenciaisInvalidasException("Informe email e senha.");
        }

        Optional<Administrador> encontrado = administradorRepository.buscarPorEmail(email);

        // Mensagem genérica de propósito: não revelar se o problema foi o
        // email ou a senha (evita enumeration attack — descobrir quais
        // emails existem cadastrados por tentativa e erro).
        Administrador administrador = encontrado
                .filter(a -> PasswordHasher.verificar(senha, a.getSenhaHash()))
                .orElseThrow(() -> new CredenciaisInvalidasException("Email ou senha inválidos."));

        return AdministradorMapper.paraResponseDTO(administrador);
    }
}
