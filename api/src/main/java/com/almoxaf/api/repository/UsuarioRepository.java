package com.almoxaf.api.repository;

import com.almoxaf.api.config.DataSourceProvider;
import com.almoxaf.api.exception.RegistroDuplicadoException;
import com.almoxaf.api.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {

    public Usuario criar(String nome, String cpf, String projeto) {
        String sql = """
                INSERT INTO usuarios (nome, cpf, projeto)
                VALUES (?, ?, ?)
                """;

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nome);
            stmt.setString(2, cpf);
            stmt.setString(3, projeto);
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                chaves.next();
                long idGerado = chaves.getLong(1);
                return buscarPorId(idGerado)
                        .orElseThrow(() -> new IllegalStateException(
                                "Usuário recém-criado não encontrado (id=" + idGerado + ")."));
            }
        } catch (SQLException e) {
            
            if ("23505".equals(e.getSQLState())) {
                throw new RegistroDuplicadoException("Já existe um usuário cadastrado com esse CPF.");
            }
            throw new IllegalStateException("Erro ao criar usuário.", e);
        }
    }

    public Optional<Usuario> buscarPorCpf(String cpf) {
        String sql = """
                SELECT id, nome, cpf, projeto, email, senha_hash, criado_em
                FROM usuarios
                WHERE cpf = ?
                """;

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapearLinha(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar usuário por CPF.", e);
        }
    }

    public List<Usuario> buscarPorNomeContendo(String termo) {
        String sql = """
                SELECT id, nome, cpf, projeto, email, senha_hash, criado_em
                FROM usuarios
                WHERE LOWER(nome) LIKE LOWER(?)
                ORDER BY nome
                """;

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, "%" + termo + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                List<Usuario> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(mapearLinha(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar usuários por nome.", e);
        }
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = """
                SELECT id, nome, cpf, projeto, email, senha_hash, criado_em
                FROM usuarios
                WHERE email = ?
                """;

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapearLinha(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar usuário por email.", e);
        }
    }

    public Optional<Usuario> buscarPorId(long id) {
        String sql = """
                SELECT id, nome, cpf, projeto, email, senha_hash, criado_em
                FROM usuarios
                WHERE id = ?
                """;

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapearLinha(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar usuário por id.", e);
        }
    }

    private Usuario mapearLinha(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setCpf(rs.getString("cpf"));
        usuario.setProjeto(rs.getString("projeto"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenhaHash(rs.getString("senha_hash"));
        usuario.setCriadoEm(rs.getObject("criado_em", java.time.OffsetDateTime.class));
        return usuario;
    }
}
