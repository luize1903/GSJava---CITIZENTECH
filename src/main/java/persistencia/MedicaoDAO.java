package persistencia;

import dominio.Medicao;
import dominio.TipoSensor;
import excecao.DadosInvalidosException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Acesso ao banco para medições. 
public class MedicaoDAO {

    private static final String SQL_INSERIR =
            "INSERT INTO medicao (sensor_id, tipo, valor, data_hora) VALUES (?, ?, ?, ?)";
    private static final String SQL_POR_SENSOR =
            "SELECT id, sensor_id, tipo, valor, data_hora FROM medicao WHERE sensor_id = ? "
                    + "ORDER BY data_hora ASC, id ASC";
    private static final String SQL_ULTIMAS =
            "SELECT id, sensor_id, tipo, valor, data_hora FROM medicao WHERE sensor_id = ? "
                    + "ORDER BY data_hora DESC, id DESC LIMIT ?";

    // Grava a medição e devolve o id gerado pelo banco. 
    public long salvar(Medicao medicao) throws SQLException {
        try (Connection conexao = ConexaoBanco.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, medicao.getSensorId());
            ps.setString(2, medicao.getTipo().name());
            ps.setDouble(3, medicao.getValor());
            ps.setTimestamp(4, Timestamp.valueOf(medicao.getDataHora()));
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                return chaves.next() ? chaves.getLong(1) : -1;
            }
        }
    }

    // Histórico completo do sensor, da leitura mais antiga para a mais recente. 
    public List<Medicao> buscarHistoricoPorSensor(String sensorId) throws SQLException {
        List<Medicao> medicoes = new ArrayList<>();
        try (Connection conexao = ConexaoBanco.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(SQL_POR_SENSOR)) {
            ps.setString(1, sensorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medicoes.add(mapear(rs));
                }
            }
        }
        return medicoes;
    }

    //As últimas N leituras do sensor, em ordem cronológica (a mais recente por último). 
    public List<Medicao> buscarUltimasPorSensor(String sensorId, int limite) throws SQLException {
        List<Medicao> medicoes = new ArrayList<>();
        try (Connection conexao = ConexaoBanco.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(SQL_ULTIMAS)) {
            ps.setString(1, sensorId);
            ps.setInt(2, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medicoes.add(mapear(rs));
                }
            }
        }
        Collections.reverse(medicoes); // veio da mais nova para a mais antiga
        return medicoes;
    }

    private Medicao mapear(ResultSet rs) throws SQLException {
        try {
            return new Medicao(
                    rs.getLong("id"),
                    rs.getString("sensor_id"),
                    TipoSensor.valueOf(rs.getString("tipo")),
                    rs.getDouble("valor"),
                    rs.getTimestamp("data_hora").toLocalDateTime());
        } catch (DadosInvalidosException | IllegalArgumentException e) {
            throw new SQLException("Registro de medição inconsistente no banco (id "
                    + rs.getLong("id") + "): " + e.getMessage(), e);
        }
    }
}
