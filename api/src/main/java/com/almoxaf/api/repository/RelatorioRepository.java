package com.almoxaf.api.repository;

import com.almoxaf.api.config.DataSourceProvider;
import com.almoxaf.api.model.Relatorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RelatorioRepository {

    public Relatorio salvar(OffsetDateTime periodoInicio, OffsetDateTime periodoFim, int totalEntregas,
                             int totalDevolucoes, String detalhesJson) {
        String sql = """
                INSERT INTO relatorios (periodo_inicio, periodo_fim, total_entregas, total_devolucoes, detalhes_json)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setTimestamp(1, Timestamp.from(periodoInicio.toInstant()));
            stmt.setTimestamp(2, Timestamp.from(periodoFim.toInstant()));
            stmt.setInt(3, totalEntregas);
            stmt.setInt(4, totalDevolucoes);
            stmt.setString(5, detalhesJson);
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                chaves.next();
                long idGerado = chaves.getLong(1);
                return buscarPorId(idGerado)
                        .orElseThrow(() -> new IllegalStateException("Relatório recém-criado não encontrado."));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar relatório.", e);
        }
    }

    public List<Relatorio> listarMaisRecentesPrimeiro() {
        String sql = """
                SELECT id, periodo_inicio, periodo_fim, gerado_em, total_entregas, total_devolucoes, detalhes_json
                FROM relatorios
                ORDER BY gerado_em DESC
                """;

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<Relatorio> resultado = new ArrayList<>();
            while (rs.next()) {
                resultado.add(mapearLinha(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar relatórios.", e);
        }
    }

    public Optional<Relatorio> buscarPorId(long id) {
        String sql = """
                SELECT id, periodo_inicio, periodo_fim, gerado_em, total_entregas, total_devolucoes, detalhes_json
                FROM relatorios
                WHERE id = ?
                """;

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapearLinha(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar relatório por id.", e);
        }
    }

    /** Usado pelo agendador — pra saber se já passou tempo suficiente pra gerar um novo. */
    public Optional<OffsetDateTime> buscarDataDoUltimoGerado() {
        String sql = "SELECT gerado_em FROM relatorios ORDER BY gerado_em DESC LIMIT 1";

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (!rs.next()) return Optional.empty();
            return Optional.of(rs.getObject("gerado_em", OffsetDateTime.class));
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao consultar último relatório gerado.", e);
        }
    }

    private Relatorio mapearLinha(ResultSet rs) throws SQLException {
        Relatorio relatorio = new Relatorio();
        relatorio.setId(rs.getLong("id"));
        relatorio.setPeriodoInicio(rs.getObject("periodo_inicio", OffsetDateTime.class));
        relatorio.setPeriodoFim(rs.getObject("periodo_fim", OffsetDateTime.class));
        relatorio.setGeradoEm(rs.getObject("gerado_em", OffsetDateTime.class));
        relatorio.setTotalEntregas(rs.getInt("total_entregas"));
        relatorio.setTotalDevolucoes(rs.getInt("total_devolucoes"));
        relatorio.setDetalhesJson(rs.getString("detalhes_json"));
        return relatorio;
    }
}
