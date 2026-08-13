package com.almoxaf.api.service;

import com.almoxaf.api.dto.EquipamentoResumoDTO;
import com.almoxaf.api.dto.UsuarioComEquipamentosDTO;
import com.almoxaf.api.dto.UsuarioCreateRequestDTO;
import com.almoxaf.api.dto.UsuarioResponseDTO;
import com.almoxaf.api.exception.RegistroDuplicadoException;
import com.almoxaf.api.exception.RegraDeNegocioException;
import com.almoxaf.api.mapper.EquipamentoMapper;
import com.almoxaf.api.mapper.UsuarioMapper;
import com.almoxaf.api.model.Usuario;
import com.almoxaf.api.repository.EquipamentoRepository;
import com.almoxaf.api.repository.UsuarioRepository;

import java.util.List;
import java.util.regex.Pattern;

public class UsuarioService {

    private static final Pattern CPF_SOMENTE_DIGITOS = Pattern.compile("\\d{11}");

    private final UsuarioRepository usuarioRepository;
    private final EquipamentoRepository equipamentoRepository;

    public UsuarioService() {
        this(new UsuarioRepository(), new EquipamentoRepository());
    }

    public UsuarioService(UsuarioRepository usuarioRepository, EquipamentoRepository equipamentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.equipamentoRepository = equipamentoRepository;
    }

    public UsuarioResponseDTO criar(UsuarioCreateRequestDTO dados) {
        String nome = dados.getNome() != null ? dados.getNome().trim() : "";
        String cpf = dados.getCpf() != null ? dados.getCpf().replaceAll("\\D", "") : "";
        String projeto = dados.getProjeto() != null ? dados.getProjeto().trim() : "";

        if (nome.length() < 3) {
            throw new RegraDeNegocioException("Informe o nome completo do usuário.");
        }
        if (nome.length() > 150) {
            throw new RegraDeNegocioException("Nome não pode ter mais de 150 caracteres.");
        }
        if (!CPF_SOMENTE_DIGITOS.matcher(cpf).matches()) {
            throw new RegraDeNegocioException("CPF deve conter exatamente 11 dígitos.");
        }
        if (projeto.isEmpty()) {
            throw new RegraDeNegocioException("Informe o projeto associado ao usuário.");
        }
        if (projeto.length() > 150) {
            throw new RegraDeNegocioException("Nome do projeto não pode ter mais de 150 caracteres.");
        }

        if (usuarioRepository.buscarPorCpf(cpf).isPresent()) {
            throw new RegistroDuplicadoException("Já existe um usuário cadastrado com esse CPF.");
        }

        Usuario usuarioCriado = usuarioRepository.criar(nome, cpf, projeto);
        return UsuarioMapper.paraResponseDTO(usuarioCriado);
    }

    public List<UsuarioComEquipamentosDTO> buscarPorNome(String termo) {
        if (termo == null || termo.isBlank()) {
            throw new RegraDeNegocioException("Informe um termo de busca.");
        }
        if (termo.length() > 150) {
            throw new RegraDeNegocioException("Termo de busca muito longo.");
        }

        List<Usuario> usuariosEncontrados = usuarioRepository.buscarPorNomeContendo(termo.trim());

        return usuariosEncontrados.stream()
                .map(usuario -> {
                    List<EquipamentoResumoDTO> equipamentos = equipamentoRepository
                            .listarPorUsuarioId(usuario.getId())
                            .stream()
                            .map(EquipamentoMapper::paraResumoDTO)
                            .toList();

                    return new UsuarioComEquipamentosDTO(
                            UsuarioMapper.paraResponseDTO(usuario),
                            equipamentos);
                })
                .toList();
    }
}
