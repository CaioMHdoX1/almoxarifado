package com.almoxaf.api.service;

import com.almoxaf.api.dto.EquipamentoBuscaResultadoDTO;
import com.almoxaf.api.dto.EquipamentoCreateRequestDTO;
import com.almoxaf.api.dto.EquipamentoResponseDTO;
import com.almoxaf.api.exception.RecursoNaoEncontradoException;
import com.almoxaf.api.exception.RegistroDuplicadoException;
import com.almoxaf.api.exception.RegraDeNegocioException;
import com.almoxaf.api.mapper.EquipamentoMapper;
import com.almoxaf.api.model.Equipamento;
import com.almoxaf.api.model.EquipamentoStatus;
import com.almoxaf.api.repository.EquipamentoRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EquipamentoService {

    private static final String TIPO_EQUIPAMENTO = "equipamento";
    private static final String TIPO_ALMOXARIFADO = "almoxarifado";

    private final EquipamentoRepository equipamentoRepository;

    public EquipamentoService() {
        this(new EquipamentoRepository());
    }

    /** Construtor usado pelos testes, para injetar um repository mockado. */
    public EquipamentoService(EquipamentoRepository equipamentoRepository) {
        this.equipamentoRepository = equipamentoRepository;
    }

    public EquipamentoResponseDTO criar(EquipamentoCreateRequestDTO dados) {
        DadosValidados validado = validar(dados);

        if (equipamentoRepository.buscarPorCodigo(validado.codigo()).isPresent()) {
            throw new RegistroDuplicadoException("Já existe um equipamento cadastrado com esse código.");
        }

        Equipamento criado = equipamentoRepository.criar(
                validado.nome(), validado.codigo(), validado.marca(),
                validado.descricao(), validado.tipo(), validado.quantidade());
        return EquipamentoMapper.paraResponseDTORecemCriado(criado);
    }

    public EquipamentoResponseDTO editar(long id, EquipamentoCreateRequestDTO dados) {
        DadosValidados validado = validar(dados);

        Equipamento existente = equipamentoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Equipamento não encontrado."));

        // Se o código mudou, precisa checar que o novo código não colide
        // com outro equipamento (o próprio item pode "manter" o código dele).
        equipamentoRepository.buscarPorCodigo(validado.codigo())
                .filter(outro -> !outro.getId().equals(existente.getId()))
                .ifPresent(outro -> {
                    throw new RegistroDuplicadoException("Já existe um equipamento cadastrado com esse código.");
                });

        // Trocar o tipo de um item que já tem alocação ativa quebraria a
        // regra "almoxarifado não é alocado individualmente" — bloqueia.
        if (TIPO_ALMOXARIFADO.equals(validado.tipo()) && equipamentoRepository.possuiAlocacaoAtiva(id)) {
            throw new RegraDeNegocioException(
                    "Não é possível mudar para \"Almoxarifado\" um item que está alocado. Registre a devolução antes.");
        }

        Equipamento atualizado = equipamentoRepository.editar(
                id, validado.nome(), validado.codigo(), validado.marca(),
                validado.descricao(), validado.tipo(), validado.quantidade());
        return EquipamentoMapper.paraResponseDTORecemCriado(atualizado);
    }

    public void remover(long id) {
        equipamentoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Equipamento não encontrado."));

        if (equipamentoRepository.possuiAlocacaoAtiva(id)) {
            throw new RegraDeNegocioException(
                    "Não é possível remover um equipamento alocado. Registre a devolução antes de remover.");
        }

        equipamentoRepository.remover(id);
    }

    /** Busca exata por código — mesma consulta que a leitura do QR code usa. */
    public EquipamentoResponseDTO buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new RegraDeNegocioException("Informe o código do equipamento.");
        }

        return equipamentoRepository.buscarStatusPorCodigo(codigo.trim())
                .map(EquipamentoMapper::paraResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhum equipamento encontrado com o código \"" + codigo.trim() + "\"."));
    }

    /** Lista tudo, com status calculado — usado pelas telas de Editar/Remover. */
    public List<EquipamentoResponseDTO> listarTodos() {
        return equipamentoRepository.listarTodosComStatus().stream()
                .map(EquipamentoMapper::paraResponseDTO)
                .toList();
    }

    /**
     * Busca por nome, agrupando os resultados: para cada nome distinto que
     * bate com o termo, devolve total/disponíveis/alocados + a lista de
     * itens — exatamente o que a especificação do projeto pediu.
     */
    public List<EquipamentoBuscaResultadoDTO> buscarPorNome(String termo) {
        if (termo == null || termo.isBlank()) {
            throw new RegraDeNegocioException("Informe um termo de busca.");
        }
        if (termo.length() > 150) {
            throw new RegraDeNegocioException("Termo de busca muito longo.");
        }

        List<EquipamentoStatus> linhas = equipamentoRepository.buscarStatusPorNomeContendo(termo.trim());

        // LinkedHashMap preserva a ordem (a query já vem ordenada por nome)
        Map<String, List<EquipamentoStatus>> agrupadoPorNome = new LinkedHashMap<>();
        for (EquipamentoStatus linha : linhas) {
            agrupadoPorNome.computeIfAbsent(linha.getNome(), k -> new ArrayList<>()).add(linha);
        }

        return agrupadoPorNome.entrySet().stream()
                .map(entrada -> {
                    List<EquipamentoStatus> itensDoGrupo = entrada.getValue();
                    long disponiveis = itensDoGrupo.stream().filter(EquipamentoStatus::isDisponivel).count();

                    List<EquipamentoResponseDTO> itens = itensDoGrupo.stream()
                            .map(EquipamentoMapper::paraResponseDTO)
                            .toList();

                    return new EquipamentoBuscaResultadoDTO(
                            entrada.getKey(),
                            itensDoGrupo.size(),
                            disponiveis,
                            itensDoGrupo.size() - disponiveis,
                            itens);
                })
                .toList();
    }

    private DadosValidados validar(EquipamentoCreateRequestDTO dados) {
        String nome = dados.getNome() != null ? dados.getNome().trim() : "";
        String codigo = dados.getCodigo() != null ? dados.getCodigo().trim() : "";
        String marca = dados.getMarca() != null ? dados.getMarca().trim() : "";
        String descricao = dados.getDescricao() != null ? dados.getDescricao().trim() : null;
        String tipo = dados.getTipo() != null && !dados.getTipo().isBlank()
                ? dados.getTipo().trim() : TIPO_EQUIPAMENTO;

        if (nome.length() < 2) {
            throw new RegraDeNegocioException("Informe o nome do equipamento.");
        }
        if (nome.length() > 150) {
            throw new RegraDeNegocioException("Nome não pode ter mais de 150 caracteres.");
        }
        if (codigo.isEmpty()) {
            throw new RegraDeNegocioException("Informe o código/patrimônio do equipamento.");
        }
        if (codigo.length() > 50) {
            throw new RegraDeNegocioException("Código não pode ter mais de 50 caracteres.");
        }
        if (marca.isEmpty()) {
            throw new RegraDeNegocioException("Informe a marca do equipamento.");
        }
        if (marca.length() > 100) {
            throw new RegraDeNegocioException("Marca não pode ter mais de 100 caracteres.");
        }
        if (descricao != null && descricao.length() > 2000) {
            throw new RegraDeNegocioException("Descrição não pode ter mais de 2000 caracteres.");
        }
        if (!TIPO_EQUIPAMENTO.equals(tipo) && !TIPO_ALMOXARIFADO.equals(tipo)) {
            throw new RegraDeNegocioException("Tipo inválido — use \"equipamento\" ou \"almoxarifado\".");
        }

        // O coração da regra pedida: item de almoxarifado SEMPRE exige
        // quantidade; item "equipamento" (rastreado individualmente por
        // código) nunca usa quantidade.
        Integer quantidade = dados.getQuantidade();
        if (TIPO_ALMOXARIFADO.equals(tipo)) {
            if (quantidade == null) {
                throw new RegraDeNegocioException("Informe a quantidade para um item de almoxarifado.");
            }
            if (quantidade < 0) {
                throw new RegraDeNegocioException("Quantidade não pode ser negativa.");
            }
        } else {
            quantidade = null; // ignora qualquer valor enviado por engano
        }

        return new DadosValidados(nome, codigo, marca, descricao, tipo, quantidade);
    }

    private record DadosValidados(String nome, String codigo, String marca, String descricao,
                                    String tipo, Integer quantidade) {
    }
}
