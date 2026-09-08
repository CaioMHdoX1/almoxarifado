package com.almoxaf.api.service;

import com.almoxaf.api.dto.UsuarioCreateRequestDTO;
import com.almoxaf.api.exception.RegistroDuplicadoException;
import com.almoxaf.api.exception.RegraDeNegocioException;
import com.almoxaf.api.model.Usuario;
import com.almoxaf.api.repository.EquipamentoRepository;
import com.almoxaf.api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UsuarioServiceTest {

    private UsuarioRepository usuarioRepository;
    private EquipamentoRepository equipamentoRepository;
    private UsuarioService usuarioService;

    @BeforeEach
    void montarDependencias() {
        usuarioRepository = mock(UsuarioRepository.class);
        equipamentoRepository = mock(EquipamentoRepository.class);
        usuarioService = new UsuarioService(usuarioRepository, equipamentoRepository);
    }

    private UsuarioCreateRequestDTO dadosValidos() {
        UsuarioCreateRequestDTO dto = new UsuarioCreateRequestDTO();
        dto.setNome("Ana Beatriz Costa");
        dto.setCpf("111.222.333-44"); // com pontuação de propósito — service deve limpar
        dto.setProjeto("GREat");
        return dto;
    }

    @Test
    void deveRejeitarCpfComMenosDe11Digitos() {
        UsuarioCreateRequestDTO dto = dadosValidos();
        dto.setCpf("123");

        assertThrows(RegraDeNegocioException.class, () -> usuarioService.criar(dto));
    }

    @Test
    void deveRejeitarNomeMuitoCurto() {
        UsuarioCreateRequestDTO dto = dadosValidos();
        dto.setNome("Jo");

        assertThrows(RegraDeNegocioException.class, () -> usuarioService.criar(dto));
    }

    @Test
    void deveRejeitarProjetoVazio() {
        UsuarioCreateRequestDTO dto = dadosValidos();
        dto.setProjeto("   ");

        assertThrows(RegraDeNegocioException.class, () -> usuarioService.criar(dto));
    }

    @Test
    void deveRejeitarCpfJaCadastrado() {
        UsuarioCreateRequestDTO dto = dadosValidos();
        when(usuarioRepository.buscarPorCpf("11122233344"))
                .thenReturn(Optional.of(new Usuario()));

        assertThrows(RegistroDuplicadoException.class, () -> usuarioService.criar(dto));
    }

    @Test
    void deveLimparPontuacaoDoCpfAntesDeSalvar() {
        UsuarioCreateRequestDTO dto = dadosValidos();
        when(usuarioRepository.buscarPorCpf("11122233344")).thenReturn(Optional.empty());

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(1L);
        usuarioSalvo.setNome("Ana Beatriz Costa");
        usuarioSalvo.setCpf("11122233344");
        usuarioSalvo.setProjeto("GREat");
        when(usuarioRepository.criar("Ana Beatriz Costa", "11122233344", "GREat"))
                .thenReturn(usuarioSalvo);

        var resultado = usuarioService.criar(dto);

        assertEquals("11122233344", resultado.getCpf());
    }

    @Test
    void buscaPorNomeDeveTrazerEquipamentosDeCadaUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Ana Beatriz Costa");
        usuario.setCpf("11122233344");
        usuario.setProjeto("GREat");

        when(usuarioRepository.buscarPorNomeContendo("Ana")).thenReturn(List.of(usuario));
        when(equipamentoRepository.listarPorUsuarioId(1L)).thenReturn(List.of());

        var resultado = usuarioService.buscarPorNome("Ana");

        assertEquals(1, resultado.size());
        assertEquals("Ana Beatriz Costa", resultado.get(0).getUsuario().getNome());
    }

    @Test
    void buscaComTermoVazioDeveLancarErro() {
        assertThrows(RegraDeNegocioException.class, () -> usuarioService.buscarPorNome("  "));
    }
}
