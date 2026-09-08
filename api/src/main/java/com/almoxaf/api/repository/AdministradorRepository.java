package com.almoxaf.api.repository;

import com.almoxaf.api.config.DataSourceProvider;
import com.almoxaf.api.model.Administrador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Acesso a dados de {@code administradores}. De propósito não tem
 * {@code criar(...)} — o sistema é de administrador único, provisionado
 * via seed do banco. Se um dia precisar de múltiplos administradores com
 * cadastro pela aplicação, é aqui que entraria esse método.
 */
public class AdministradorRepository {

    public Optional<Administrador> buscarPorEmail(String email) {
        String sql = "SELECT id, nome, email, senha_hash, criado_em FROM administradores WHERE email = ?";

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapearLinha(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar administrador por email.", e);
        }
    }

    public Optional<Administrador> buscarPorId(long id) {
        String sql = "SELECT id, nome, email, senha_hash, criado_em FROM administradores WHERE id = ?";

        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapearLinha(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar administrador por id.", e);
        }
    }

    private Administrador mapearLinha(ResultSet rs) throws SQLException {
        Administrador administrador = new Administrador();
        administrador.setId(rs.getLong("id"));
        administrador.setNome(rs.getString("nome"));
        administrador.setEmail(rs.getString("email"));
        administrador.setSenhaHash(rs.getString("senha_hash"));
        administrador.setCriadoEm(rs.getObject("criado_em", OffsetDateTime.class));
        return administrador;
    }
}
