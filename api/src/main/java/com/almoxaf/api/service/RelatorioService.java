package com.almoxaf.api.service;

import com.almoxaf.api.dto.*;
import com.almoxaf.api.exception.RecursoNaoEncontradoException;
import com.almoxaf.api.exception.RegraDeNegocioException;
import com.almoxaf.api.model.Movimentacao;
import com.almoxaf.api.model.Relatorio;
import com.almoxaf.api.repository.AlocacaoRepository;
import com.almoxaf.api.repository.EquipamentoRepository;
import com.almoxaf.api.repository.RelatorioRepository;
import com.almoxaf.api.util.JsonMapperFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public class RelatorioService {

    /** "Quinzenalmente" — 15 dias, tanto pro período padrão quanto pro agendador. */
    public static final Duration PERIODO_PADRAO = Duration.ofDays(15);

    private final AlocacaoRepository alocacaoRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final RelatorioRepository relatorioRepository;
    private final ObjectMapper jsonMapper = JsonMapperFactory.create();

    public RelatorioService() {
        this(new AlocacaoRepository(), new EquipamentoRepository(), new RelatorioRepository());
    }

    /** Construtor usado pelos testes, para injetar repositories mockados. */
    public RelatorioService(AlocacaoRepository alocacaoRepository, EquipamentoRepository equipamentoRepository,
                             RelatorioRepository relatorioRepository) {
        this.alocacaoRepository = alocacaoRepository;
        this.equipamentoRepository = equipamentoRepository;
        this.relatorioRepository = relatorioRepository;
    }

    /** Gera e salva um relatório para os últimos 15 dias (o caso de uso mais comum). */
    public RelatorioResumoDTO gerarUltimosQuinzeDias() {
        OffsetDateTime fim = OffsetDateTime.now();
        OffsetDateTime inicio = fim.minus(PERIODO_PADRAO);
        return gerar(inicio, fim);
    }

    /** Gera e salva um relatório para um período customizado (para o botão "gerar agora" na tela). */
    public RelatorioResumoDTO gerar(OffsetDateTime periodoInicio, OffsetDateTime periodoFim) {
        if (periodoInicio == null || periodoFim == null) {
            throw new RegraDeNegocioException("Informe o período inicial e final do relatório.");
        }
        if (!periodoFim.isAfter(periodoInicio)) {
            throw new RegraDeNegocioException("O fim do período deve ser depois do início.");
        }

        List<Movimentacao> entregasBrutas = alocacaoRepository.listarEntregasNoPeriodo(periodoInicio, periodoFim);
        List<Movimentacao> devolucoesBrutas = alocacaoRepository.listarDevolucoesNoPeriodo(periodoInicio, periodoFim);

        List<MovimentacaoDTO> entregas = entregasBrutas.stream().map(this::paraMovimentacaoDTO).toList();
        List<MovimentacaoDTO> devolucoes = devolucoesBrutas.stream().map(this::paraMovimentacaoDTO).toList();

        List<PosseAtualDTO> posseAtual = equipamentoRepository.listarTodosComStatus().stream()
                .filter(item -> item.getUsuarioAtualId() != null)
                .map(item -> new PosseAtualDTO(item.getNome(), item.getCodigo(), item.getUsuarioAtualNome()))
                .toList();

        RelatorioDetalheDTO detalhe = new RelatorioDetalheDTO(
                null, periodoInicio, periodoFim, OffsetDateTime.now(), entregas, devolucoes, posseAtual);

        String detalhesJson = serializar(detalhe);

        Relatorio salvo = relatorioRepository.salvar(
                periodoInicio, periodoFim, entregas.size(), devolucoes.size(), detalhesJson);

        return paraResumoDTO(salvo);
    }

    public List<RelatorioResumoDTO> listar() {
        return relatorioRepository.listarMaisRecentesPrimeiro().stream()
                .map(this::paraResumoDTO)
                .toList();
    }

    public RelatorioDetalheDTO buscarDetalhe(long id) {
        Relatorio relatorio = relatorioRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relatório não encontrado."));

        RelatorioDetalheDTO detalhe = desserializar(relatorio.getDetalhesJson());
        detalhe.setId(relatorio.getId()); // o JSON salvo não tinha o id ainda (gerado só depois de salvar)
        return detalhe;
    }

    /**
     * Usado pelo agendador (ver {@code AppContextListener}): decide se já
     * passou tempo suficiente desde o último relatório pra gerar um novo
     * automaticamente.
     */
    public boolean devePrecisarGerarAutomatico() {
        Optional<OffsetDateTime> ultimoGerado = relatorioRepository.buscarDataDoUltimoGerado();
        if (ultimoGerado.isEmpty()) {
            return true; // nunca gerou nenhum ainda
        }
        return Duration.between(ultimoGerado.get(), OffsetDateTime.now()).compareTo(PERIODO_PADRAO) >= 0;
    }

    private MovimentacaoDTO paraMovimentacaoDTO(Movimentacao m) {
        return new MovimentacaoDTO(m.getEquipamentoNome(), m.getEquipamentoCodigo(), m.getUsuarioNome(),
                m.getUsuarioCpf(), m.getData());
    }

    private RelatorioResumoDTO paraResumoDTO(Relatorio r) {
        return new RelatorioResumoDTO(r.getId(), r.getPeriodoInicio(), r.getPeriodoFim(), r.getGeradoEm(),
                r.getTotalEntregas(), r.getTotalDevolucoes());
    }

    private String serializar(RelatorioDetalheDTO detalhe) {
        try {
            return jsonMapper.writeValueAsString(detalhe);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erro ao serializar detalhes do relatório.", e);
        }
    }

    private RelatorioDetalheDTO desserializar(String json) {
        try {
            return jsonMapper.readValue(json, RelatorioDetalheDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erro ao ler detalhes do relatório salvo.", e);
        }
    }
}
