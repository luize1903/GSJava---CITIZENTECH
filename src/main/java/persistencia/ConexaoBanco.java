package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

//Aqui conexão no banco h2, então ele vai criar tabelas na primeira execução, um detalhe é que por padrão o bnco é um arquivo local por isso os dados vão sobreviver ao reniciar o programar
public final class ConexaoBanco {

    private static final String URL_PADRAO = "jdbc:h2:file:./data/territorio";
    private static final String USUARIO = "sa";
    private static final String SENHA = "";

    private ConexaoBanco() {
    }

    public static Connection obterConexao() throws SQLException {
        String url = System.getProperty("tpi.db.url", URL_PADRAO);
        return DriverManager.getConnection(url, USUARIO, SENHA);
    }

    public static void inicializarSchema() throws SQLException {
        String criarMedicao = "CREATE TABLE IF NOT EXISTS medicao ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "sensor_id VARCHAR(30) NOT NULL, "
                + "tipo VARCHAR(30) NOT NULL, "
                + "valor DOUBLE NOT NULL, "
                + "data_hora TIMESTAMP NOT NULL)";
        String criarAlerta = "CREATE TABLE IF NOT EXISTS alerta ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "sensor_id VARCHAR(30) NOT NULL, "
                + "tipo VARCHAR(30) NOT NULL, "
                + "nivel VARCHAR(20) NOT NULL, "
                + "valor_previsto DOUBLE NOT NULL, "
                + "limite DOUBLE NOT NULL, "
                + "estrategia VARCHAR(60) NOT NULL, "
                + "mensagem VARCHAR(1000) NOT NULL, "
                + "data_hora TIMESTAMP NOT NULL)";
        try (Connection conexao = obterConexao(); Statement comando = conexao.createStatement()) {
            comando.execute(criarMedicao);
            comando.execute(criarAlerta);
        }
    }
}
