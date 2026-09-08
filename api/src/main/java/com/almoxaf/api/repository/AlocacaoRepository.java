package com.almoxaf.api.repository;

import com.almoxaf.api.config.DataSourceProvider;
import com.almoxaf.api.model.Movimentacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Consultas sobre {@code alocacoes} sob a ótica de "movimentação" (entrega
 * ou devolução) — usado pelo {@code RelatorioService}. CRUD de alocação em
 * si (criar uma alocação nova, registrar devolução) ainda não existe no
 * sistema — ver observação no README sobre isso.
 */
public class AlocacaoRepository {

    /** Equipamentos ENTREGUES no período (data_inicio dentro do intervalo). */
    public List<Movimentacao> listarEntregasNoPeriodo(OffsetDateTime inicio, OffsetDateTime fim) {
        String sql = """
                SELECT e.nome AS equipamento_nome, e.codigo AS equipamento_codigo,
                       u.nome AS usuario_nome, u.cpf AS usuario_cpf, a.data_inicio AS data
                FROM alocacoes a
                JOIN equipamentos e ON e.id = a.equipamento_id
                JOIN usuarios u ON u.id = a.usuario_id
                WHERE a.data_inicio >= ? AND a.data_inicio < ?
                ORDER BY a.data_inicio
                """;
        return listar(sql, inicio, fim);
    }

    /** Equipamentos DEVOLVIDOS no período (data_fim dentro do intervalo). */
    public List<Movimentacao> listarDevolucoesNoPeriodo(OffsetDateTime inicio, OffsetDateTime fim) {
        String sql = """
                SELECT e.nome AS equipamento_nome, e.codigo AS equipamento_codigo,
                       u.nome AS usuario_nome, u.cpf AS usuario_cpf, a.data_fim AS data
                FROM alocacoes a
                JOIN equipamentos e ON e.id = a.equipamento_id
                JOIN usuarios u ON u.id = a.usuario_id
                WHERE a.data_fim >= ? AND a.data_fim < ?
                ORDER BY a.data_fim
                """;
        return listar(sql, inicio, fim);
    }

    private List<Movimentacao> listar(String sql, OffsetDateTime inicio, OffsetDateTime fim) {
        try (Connection conexao = DataSourceProvider.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.from(inicio.toInstant()));
            stmt.setTimestamp(2, Timestamp.from(fim.toInstant()));

            try (ResultSet rs = stmt.executeQuery()) {
                List<Movimentacao> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(new Movimentacao(
                            rs.getString("equipamento_nome"),
                            rs.getString("equipamento_codigo"),
                            rs.getString("usuario_nome"),
                            rs.getString("usuario_cpf"),
                            rs.getObject("data", OffsetDateTime.class)));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao consultar movimentações.", e);
        }
    }
}
