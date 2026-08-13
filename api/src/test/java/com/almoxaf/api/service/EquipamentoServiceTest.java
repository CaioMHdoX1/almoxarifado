package com.almoxaf.api.service;

import com.almoxaf.api.dto.EquipamentoCreateRequestDTO;
import com.almoxaf.api.exception.RecursoNaoEncontradoException;
import com.almoxaf.api.exception.RegistroDuplicadoException;
import com.almoxaf.api.exception.RegraDeNegocioException;
import com.almoxaf.api.model.Equipamento;
import com.almoxaf.api.model.EquipamentoStatus;
import com.almoxaf.api.repository.EquipamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EquipamentoServiceTest {

    private EquipamentoRepository equipamentoRepository;
    private EquipamentoService equipamentoService;

    @BeforeEach
    void montarDependencias() {
        equipamentoRepository = mock(EquipamentoRepository.class);
        equipamentoService = new EquipamentoService(equipamentoRepository);
    }

    private EquipamentoCreateRequestDTO dadosValidos() {
        EquipamentoCreateRequestDTO dto = new EquipamentoCreateRequestDTO();
        dto.setNome("Notebook Dell Latitude 5520");
        dto.setCodigo("PAT-00812");
        dto.setMarca("Dell");
        return dto;
    }

    @Test
    void deveRejeitarCriacaoComCodigoDuplicado() {
        EquipamentoCreateRequestDTO dto = dadosValidos();
        when(equipamentoRepository.buscarPorCodigo("PAT-00812"))
                .thenReturn(Optional.of(new Equipamento()));

        assertThrows(RegistroDuplicadoException.class, () -> equipamentoService.criar(dto));
    }

    @Test
    void deveRejeitarNomeVazio() {
        EquipamentoCreateRequestDTO dto = dadosValidos();
        dto.setNome(" ");

        assertThrows(RegraDeNegocioException.class, () -> equipamentoService.criar(dto));
    }

    @Test
    void deveRejeitarRemocaoDeEquipamentoInexistente() {
        when(equipamentoRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> equipamentoService.remover(99L));
    }

    @Test
    void deveRejeitarRemocaoDeEquipamentoAlocado() {
        Equipamento existente = new Equipamento();
        existente.setId(1L);
        when(equipamentoRepository.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(equipamentoRepository.possuiAlocacaoAtiva(1L)).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> equipamentoService.remover(1L));
    }

    @Test
    void deveRejeitarEdicaoDeEquipamentoInexistente() {
        when(equipamentoRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> equipamentoService.editar(99L, dadosValidos()));
    }

    @Test
    void buscaPorNomeDeveAgruparECalcularTotais() {
        EquipamentoStatus item1 = new EquipamentoStatus();
        item1.setId(1L);
        item1.setNome("Notebook Dell Latitude 5520");
        item1.setCodigo("PAT-00812");
        item1.setMarca("Dell");
        item1.setStatus("alocado");
        item1.setUsuarioAtualId(10L);
        item1.setUsuarioAtualNome("Ana Beatriz Costa");

        EquipamentoStatus item2 = new EquipamentoStatus();
        item2.setId(2L);
        item2.setNome("Notebook Dell Latitude 5520");
        item2.setCodigo("PAT-00813");
        item2.setMarca("Dell");
        item2.setStatus("disponivel");

        when(equipamentoRepository.buscarStatusPorNomeContendo("Notebook"))
                .thenReturn(List.of(item1, item2));

        var resultado = equipamentoService.buscarPorNome("Notebook");

        assertEquals(1, resultado.size());
        assertEquals(2, resultado.get(0).getTotal());
        assertEquals(1, resultado.get(0).getDisponiveis());
        assertEquals(1, resultado.get(0).getAlocados());
    }

    @Test
    void buscaComTermoVazioDeveLancarErro() {
        assertThrows(RegraDeNegocioException.class, () -> equipamentoService.buscarPorNome(""));
    }

    @Test
    void buscaPorCodigoDeveRetornarEquipamentoQuandoExiste() {
        EquipamentoStatus item = new EquipamentoStatus();
        item.setId(1L);
        item.setNome("Notebook Dell Latitude 5520");
        item.setCodigo("PAT-00812");
        item.setMarca("Dell");
        item.setStatus("disponivel");

        when(equipamentoRepository.buscarStatusPorCodigo("PAT-00812")).thenReturn(Optional.of(item));

        var resultado = equipamentoService.buscarPorCodigo("PAT-00812");

        assertEquals("PAT-00812", resultado.getCodigo());
        assertEquals("disponivel", resultado.getStatus());
    }

    @Test
    void buscaPorCodigoInexistenteDeveLancarRecursoNaoEncontrado() {
        when(equipamentoRepository.buscarStatusPorCodigo("PAT-99999")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> equipamentoService.buscarPorCodigo("PAT-99999"));
    }

    @Test
    void buscaPorCodigoVazioDeveLancarErroDeNegocio() {
        assertThrows(RegraDeNegocioException.class, () -> equipamentoService.buscarPorCodigo(" "));
    }
}
