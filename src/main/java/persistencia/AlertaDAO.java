package persistencia;

import dominio.Alerta;
import dominio.NivelAlerta;
import dominio.TipoSensor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

//Acesso ao banco para alertas emitidos. 
public class AlertaDAO {

    private static final String SQL_INSERIR =
            "INSERT INTO alerta (sensor_id, tipo, nivel, valor_previsto, limite, estrategia, mensagem, data_hora) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_LISTAR =
            "SELECT id, sensor_id, tipo, nivel, valor_previsto, limite, estrategia, mensagem, data_hora "
                    + "FROM alerta ORDER BY data_hora DESC, id DESC";

    public long salvar(Alerta alerta) throws SQLException {
        try (Connection conexao = ConexaoBanco.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, alerta.getSensorId());
            ps.setString(2, alerta.getTipo().name());
            ps.setString(3, alerta.getNivel().name());
            ps.setDouble(4, alerta.getValorPrevisto());
            ps.setDouble(5, alerta.getLimiteUltrapassado());
            ps.setString(6, alerta.getEstrategia());
            ps.setString(7, alerta.getMensagem());
            ps.setTimestamp(8, Timestamp.valueOf(alerta.getDataHora()));
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                return chaves.next() ? chaves.getLong(1) : -1;
            }
        }
    }

    // Todos os alertas já emitidos, do mais recente para o mais antigo. 
    public List<Alerta> listarTodos() throws SQLException {
        List<Alerta> alertas = new ArrayList<>();
        try (Connection conexao = ConexaoBanco.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                try {
                    alertas.add(new Alerta(
                            rs.getLong("id"),
                            rs.getString("sensor_id"),
                            TipoSensor.valueOf(rs.getString("tipo")),
                            NivelAlerta.valueOf(rs.getString("nivel")),
                            rs.getDouble("valor_previsto"),
                            rs.getDouble("limite"),
                            rs.getString("estrategia"),
                            rs.getString("mensagem"),
                            rs.getTimestamp("data_hora").toLocalDateTime()));
                } catch (IllegalArgumentException e) {
                    throw new SQLException("Registro de alerta inconsistente no banco (id "
                            + rs.getLong("id") + "): " + e.getMessage(), e);
                }
            }
        }
        return alertas;
    }
}
