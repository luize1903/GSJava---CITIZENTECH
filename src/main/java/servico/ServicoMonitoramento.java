package servico;
//confere se dois valores são iguais
import dominio.Alerta;
import dominio.EstrategiaPrevisao;
import dominio.Formatacao;
import dominio.Limiares;
import dominio.Medicao;
import dominio.NivelAlerta;
import dominio.Sensor;
import dominio.TipoSensor;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.sql.SQLException;
import java.util.List;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;


public class ServicoMonitoramento {

    private final MedicaoDAO medicaoDAO;
    private final AlertaDAO alertaDAO;
    private final ConfiguracaoSistema config;

    public ServicoMonitoramento(MedicaoDAO medicaoDAO, AlertaDAO alertaDAO, ConfiguracaoSistema config) {
        this.medicaoDAO = medicaoDAO;
        this.alertaDAO = alertaDAO;
        this.config = config;
    }

    // Lê o sensor, valida o valor (DadosInvalidosException) e grava no banco. 
    public Medicao lerESalvar(Sensor sensor) throws FonteIndisponivelException, DadosInvalidosException, SQLException {
        if (!sensor.verificarFuncionamento()) {
            throw new FonteIndisponivelException("O sensor " + sensor.getId() + " não está funcionando.");
        }
        double valor = sensor.lerValor();
        Medicao medicao = new Medicao(sensor.getId(), sensor.getTipo(), valor); // valida aqui
        medicaoDAO.salvar(medicao);
        return medicao;
    }

    // Registra uma medição informada manualmente (ex.: leitura de um pluviômetro de campo). 
    public Medicao registrarManual(String sensorId, TipoSensor tipo, double valor)
            throws DadosInvalidosException, SQLException {
        Medicao medicao = new Medicao(sensorId, tipo, valor);
        medicaoDAO.salvar(medicao);
        return medicao;
    }


    public ResultadoPrevisao preverEAlertar(Sensor sensor, EstrategiaPrevisao estrategia)
            throws DadosInvalidosException, SQLException {
        List<Medicao> historico = medicaoDAO.buscarUltimasPorSensor(sensor.getId(), ConfiguracaoSistema.TAMANHO_CONSULTA);
        int minimo = config.getMinimoLeituras();
        if (historico.size() < minimo) {
            return ResultadoPrevisao.historicoInsuficiente(sensor.getId(), estrategia.getNome(), historico.size(), minimo);
        }

        double previsto = estrategia.prever(historico); // polimorfismo: cada estratégia calcula do seu jeito
        TipoSensor tipo = sensor.getTipo();
        Limiares limiares = config.getTerritorio().getLimiares(tipo);
        NivelAlerta nivel = limiares.classificar(previsto);

        String valorTexto = Formatacao.numero(previsto) + " " + tipo.getUnidade();
        if (nivel == NivelAlerta.NORMAL) {
            String texto = "Previsão de " + valorTexto + " (" + estrategia.getDescricao()
                    + "): abaixo do limiar de atenção (" + Formatacao.numero(limiares.getAtencao()) + " "
                    + tipo.getUnidade() + "). Nenhum alerta.";
            return new ResultadoPrevisao(sensor.getId(), estrategia.getNome(), true, previsto, nivel, null, texto);
        }

        double limite = limiares.limitePara(nivel);
        String mensagem = montarMensagem(sensor, estrategia, previsto, nivel, limite);
        Alerta alerta = new Alerta(sensor.getId(), tipo, nivel, previsto, limite, estrategia.getNome(), mensagem);
        alertaDAO.salvar(alerta);
        return new ResultadoPrevisao(sensor.getId(), estrategia.getNome(), true, previsto, nivel, alerta, mensagem);
    }

    public List<Medicao> historicoPorSensor(String sensorId) throws SQLException {
        return medicaoDAO.buscarHistoricoPorSensor(sensorId);
    }

    public List<Alerta> alertasEmitidos() throws SQLException {
        return alertaDAO.listarTodos();
    }

    private String montarMensagem(Sensor sensor, EstrategiaPrevisao estrategia, double previsto,
                                  NivelAlerta nivel, double limite) {
        TipoSensor tipo = sensor.getTipo();
        return nivel.getRotulo() + ": sensor " + sensor.getId() + " (" + tipo.getDescricao() + ") com previsão de "
                + Formatacao.numero(previsto) + " " + tipo.getUnidade() + " pela estratégia '" + estrategia.getNome()
                + "' (" + estrategia.getDescricao() + "), acima do limiar de " + nivel.getRotulo() + " ("
                + Formatacao.numero(limite) + " " + tipo.getUnidade() + "). Sugestão: " + nivel.getSugestao()
                + ". O alerta apoia a decisão; quem decide é a pessoa responsável.";
    }
}
