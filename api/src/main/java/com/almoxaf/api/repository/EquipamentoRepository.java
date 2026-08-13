package com.almoxaf.api.repository;

import com.almoxaf.api.config.DataSourceProvider;
import com.almoxaf.api.exception.RegistroDuplicadoException;
import com.almoxaf.api.model.Equipamento;
import com.almoxaf.api.model.EquipamentoStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EquipamentoRepository {

    public Equipamento criar(String nome, String codigo, String marca) {
        String sql = """
                INSERT INTO equipamentos (nome, codigo, marca)
                VALUES (?, ?, ?)
                """;

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nome);
            stmt.setString(2, codigo);
            stmt.setString(3, marca);
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                chaves.next();
                long idGerado = chaves.getLong(1);
                return buscarPorId(idGerado)
                        .orElseThrow(() -> new IllegalStateException(
                                "Equipamento recém-criado não encontrado (id=" + idGerado + ")."));
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new RegistroDuplicadoException("Já existe um equipamento cadastrado com esse código.");
            }
            throw new IllegalStateException("Erro ao criar equipamento.", e);
        }
    }

    public Equipamento editar(long id, String nome, String codigo, String marca) {
        String sql = """
                UPDATE equipamentos
                SET nome = ?, codigo = ?, marca = ?
                WHERE id = ?
                """;

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, codigo);
            stmt.setString(3, marca);
            stmt.setLong(4, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                return null; // o Service decide o que fazer (404)
            }
            return buscarPorId(id).orElse(null);
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new RegistroDuplicadoException("Já existe um equipamento cadastrado com esse código.");
            }
            throw new IllegalStateException("Erro ao editar equipamento.", e);
        }
    }

    public boolean remover(long id) {
        String sql = "DELETE FROM equipamentos WHERE id = ?";

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao remover equipamento.", e);
        }
    }

    public Optional<Equipamento> buscarPorId(long id) {
        String sql = """
                SELECT id, nome, codigo, marca, categoria, criado_em
                FROM equipamentos
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
            throw new IllegalStateException("Erro ao buscar equipamento por id.", e);
        }
    }

    public Optional<Equipamento> buscarPorCodigo(String codigo) {
        String sql = """
                SELECT id, nome, codigo, marca, categoria, criado_em
                FROM equipamentos
                WHERE codigo = ?
                """;

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, codigo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapearLinha(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar equipamento por código.", e);
        }
    }

    public boolean possuiAlocacaoAtiva(long equipamentoId) {
        String sql = "SELECT 1 FROM alocacoes WHERE equipamento_id = ? AND data_fim IS NULL";

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, equipamentoId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao verificar alocação do equipamento.", e);
        }
    }

    public List<EquipamentoStatus> listarTodosComStatus() {
        String sql = """
                SELECT id, nome, codigo, marca, status, usuario_atual_id, usuario_atual_nome
                FROM vw_equipamentos_status
                ORDER BY nome, codigo
                """;

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<EquipamentoStatus> resultado = new ArrayList<>();
            while (rs.next()) {
                resultado.add(mapearLinhaStatus(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar equipamentos.", e);
        }
    }

    public List<EquipamentoStatus> buscarStatusPorNomeContendo(String termo) {
        String sql = """
                SELECT id, nome, codigo, marca, status, usuario_atual_id, usuario_atual_nome
                FROM vw_equipamentos_status
                WHERE LOWER(nome) LIKE LOWER(?)
                ORDER BY nome, codigo
                """;

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, "%" + termo + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                List<EquipamentoStatus> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(mapearLinhaStatus(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar equipamentos por nome.", e);
        }
    }

    public Optional<EquipamentoStatus> buscarStatusPorCodigo(String codigo) {
        String sql = """
                SELECT id, nome, codigo, marca, status, usuario_atual_id, usuario_atual_nome
                FROM vw_equipamentos_status
                WHERE codigo = ?
                """;

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, codigo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapearLinhaStatus(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar equipamento por código.", e);
        }
    }

    public List<Equipamento> listarPorUsuarioId(long usuarioId) {
        String sql = """
                SELECT id, nome, codigo, marca, categoria
                FROM vw_equipamentos_status
                WHERE usuario_atual_id = ?
                ORDER BY nome
                """;

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, usuarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Equipamento> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(mapearLinha(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar equipamentos do usuário.", e);
        }
    }

    private Equipamento mapearLinha(ResultSet rs) throws SQLException {
        Equipamento equipamento = new Equipamento();
        equipamento.setId(rs.getLong("id"));
        equipamento.setNome(rs.getString("nome"));
        equipamento.setCodigo(rs.getString("codigo"));
        equipamento.setMarca(rs.getString("marca"));
        return equipamento;
    }

    private EquipamentoStatus mapearLinhaStatus(ResultSet rs) throws SQLException {
        EquipamentoStatus status = new EquipamentoStatus();
        status.setId(rs.getLong("id"));
        status.setNome(rs.getString("nome"));
        status.setCodigo(rs.getString("codigo"));
        status.setMarca(rs.getString("marca"));
        status.setStatus(rs.getString("status"));
        long usuarioAtualId = rs.getLong("usuario_atual_id");
        status.setUsuarioAtualId(rs.wasNull() ? null : usuarioAtualId);
        status.setUsuarioAtualNome(rs.getString("usuario_atual_nome"));
        return status;
    }
}
