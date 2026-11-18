import dominio.Alerta;
import dominio.DadosTerritorio;
import dominio.EstrategiaPrevisao;
import dominio.Formatacao;
import dominio.Limiares;
import dominio.Medicao;
import dominio.PontoMonitoramento;
import dominio.SensorNivelCorrego;
import dominio.SensorPluviometro;
import dominio.SensorSimulado;
import dominio.SensorUmidadeSolo;
import dominio.TipoSensor;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;                     // aqui nós fizemos a importação das bibliotecas necessarias 
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import persistencia.AlertaDAO;
import persistencia.ConexaoBanco;
import persistencia.MedicaoDAO;
import servico.ConfiguracaoSistema;
import servico.ExportadorCsv;
import servico.FabricaPrevisao;
import servico.ResultadoPrevisao;
import servico.ServicoMonitoramento;


public class SistemaMonitoramento {

    private static final double PROBABILIDADE_FALHA = 0.03;// chance de 3% de um sensor falhar, para simular a FonteIndisponivelException

    private final Scanner scanner = new Scanner(System.in);
    private final ConfiguracaoSistema config = ConfiguracaoSistema.getInstance(); // config é como se fosse algo coringa, todo sistema usa os mesmos limiares
    private final List<SensorSimulado> sensores = new ArrayList<>();
    private final List<PontoMonitoramento> pontos = new ArrayList<>();    
    private final ServicoMonitoramento servico;


// sensores para simulação: 2 de chuva, 2 de nível do córrego e 1 de umidade do solo
    public SistemaMonitoramento() throws DadosInvalidosException { 
        Random random = new Random();
        SensorSimulado plv1 = new SensorPluviometro("PLV-01", random, PROBABILIDADE_FALHA);
        SensorSimulado niv1 = new SensorNivelCorrego("NIV-01", random, PROBABILIDADE_FALHA);
        SensorSimulado umi1 = new SensorUmidadeSolo("UMI-01", random, PROBABILIDADE_FALHA);
        SensorSimulado plv2 = new SensorPluviometro("PLV-02", random, PROBABILIDADE_FALHA);
        SensorSimulado niv2 = new SensorNivelCorrego("NIV-02", random, PROBABILIDADE_FALHA);
        sensores.addAll(List.of(plv1, niv1, umi1, plv2, niv2));

        PontoMonitoramento ponte = new PontoMonitoramento("PM-01", "Ponte do Córrego", "Zona Baixa");
        ponte.adicionarSensor(plv1);
        ponte.adicionarSensor(niv1);
        ponte.adicionarSensor(umi1);
        PontoMonitoramento travessa = new PontoMonitoramento("PM-02", "Travessa da Várzea", "Zona Baixa");
        travessa.adicionarSensor(plv2);
        travessa.adicionarSensor(niv2);
        pontos.add(ponte);
        pontos.add(travessa);

        this.servico = new ServicoMonitoramento(new MedicaoDAO(), new AlertaDAO(), config);
    }

    public static void main(String[] args) {
        try {
            ConexaoBanco.inicializarSchema();
            new SistemaMonitoramento().executar();
        } catch (SQLException e) {
            System.err.println("Não foi possível preparar o banco de dados: " + e.getMessage());
        } catch (DadosInvalidosException e) {
            System.err.println("Configuração inicial inválida: " + e.getMessage());
        }
    }

    // Aqui eu fiz um tipo de "Looping utilizei o material de aula para fazer esta parte

    private void executar() {
        System.out.println("=== Territory Predictive Insights: chuva e alagamento ===");
        boolean ativo = true;
        while (ativo) {
            exibirMenu();
            String opcao = lerLinha("Escolha uma opção: ");
            if (opcao == null) {
                break;               // laço de repetição quebrado com o break
            }
            try {
                switch (opcao.trim()) {
                    case "1" -> verificarSensores();
                    
                    case "2" -> rodarPrevisoes();
                    case "3" -> menuHistorico();  
                    case "4" -> menuGerenciarDados();
                    case "5" -> statusCompleto();
                    case "0" -> {
                        System.out.println("Encerrando. Os dados ficam salvos no banco para a próxima execução.");
                        ativo = false;
                    }
                    default -> System.out.println("Opção inválida. Digite um número do menu.");
                }
            } catch (DadosInvalidosException e) {
                System.out.println("[Dado inválido] " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("[Erro no banco de dados] " + e.getMessage());
            } catch (IOException e) {
                System.out.println("[Erro ao gravar arquivo] " + e.getMessage());
            }
        }
    }

    private void exibirMenu() {
        System.out.println();
        System.out.println("1 - Verificar sensores (ler e gravar medições)");
        System.out.println("2 - Rodar previsões e alertas");
        System.out.println("3 - Ver histórico");
        System.out.println("4 - Gerenciar dados do território");
        System.out.println("5 - Status completo");
        System.out.println("0 - Encerrar");
    }

    // sensores

    private void verificarSensores() throws DadosInvalidosException, SQLException {
        int rodadas = lerInteiro("Quantas rodadas de leitura executar (1 a 50)? ");
        if (rodadas < 1 || rodadas > 50) {
            throw new DadosInvalidosException("O número de rodadas deve estar entre 1 e 50 (informado: " + rodadas + ").");
        }
        int gravadas = 0;
        int falhas = 0;
        for (int i = 1; i <= rodadas; i++) {
            for (SensorSimulado sensor : sensores) {
                try {
                    Medicao medicao = servico.lerESalvar(sensor);
                    gravadas++;
                    if (rodadas == 1) {
                        System.out.println("  gravada: " + medicao);
                    }
                } catch (FonteIndisponivelException e) {
                    falhas++;
                    System.out.println("  [Sensor indisponível] " + e.getMessage());
                } catch (DadosInvalidosException e) {
                    falhas++;
                    System.out.println("  [Leitura rejeitada] " + e.getMessage());
                }
            }
        }
        System.out.println("Resumo: " + gravadas + " medição(ões) gravada(s), " + falhas + " falha(s).");
    }

    // aqui são as previsões

    private void rodarPrevisoes() throws DadosInvalidosException, SQLException {
        List<EstrategiaPrevisao> estrategias = FabricaPrevisao.criarTodas(config);
        for (SensorSimulado sensor : sensores) {
            System.out.println();
            System.out.println("Sensor " + sensor.getId() + " (" + sensor.getTipo().getDescricao() + ")");
            for (EstrategiaPrevisao estrategia : estrategias) { // polimorfismo: mesma chamada, cálculos diferentes
                ResultadoPrevisao r = servico.preverEAlertar(sensor, estrategia);
                if (!r.isConfiavel()) {
                    System.out.println("  [" + estrategia.getNome() + "] " + r.getExplicacao());
                } else {
                    System.out.println("  [" + estrategia.getNome() + "] previsto: "
                            + Formatacao.numero(r.getValorPrevisto()) + " " + sensor.getTipo().getUnidade()
                            + " -> nível " + r.getNivel().getRotulo());
                    System.out.println("      " + r.getExplicacao());
                }
            }
        }
    }

    // histórico

    private void menuHistorico() throws DadosInvalidosException, SQLException, IOException {
        System.out.println("  1 - Medições por sensor");
        System.out.println("  2 - Alertas já emitidos");
        System.out.println("  3 - Exportar medições de um sensor para CSV");
        String opcao = lerLinha("  Opção: ");
        if (opcao == null) {
            return;
        }
        switch (opcao.trim()) {
            case "1" -> {
                SensorSimulado sensor = escolherSensor();
                List<Medicao> medicoes = servico.historicoPorSensor(sensor.getId());
                if (medicoes.isEmpty()) {
                    System.out.println("Nenhuma medição gravada para " + sensor.getId() + " ainda.");
                }
                medicoes.forEach(m -> System.out.println("  " + m));
            }
            case "2" -> {
                List<Alerta> alertas = servico.alertasEmitidos();
                if (alertas.isEmpty()) {
                    System.out.println("Nenhum alerta emitido até agora.");
                }
                alertas.forEach(a -> System.out.println("  " + a));
            }
            case "3" -> {
                SensorSimulado sensor = escolherSensor();
                List<Medicao> medicoes = servico.historicoPorSensor(sensor.getId());
                Path arquivo = Path.of("historico_" + sensor.getId() + ".csv");
                ExportadorCsv.exportarMedicoes(medicoes, arquivo);
                System.out.println("Exportadas " + medicoes.size() + " medição(ões) para " + arquivo.toAbsolutePath());
            }
            default -> System.out.println("Opção inválida.");
        }
    }


    private void menuGerenciarDados() throws DadosInvalidosException, SQLException { // dados do território
        DadosTerritorio territorio = config.getTerritorio();
        System.out.println("  1 - Ver dados e limiares");
        System.out.println("  2 - Alterar população exposta");
        System.out.println("  3 - Alterar limiares de um tipo de sensor");
        System.out.println("  4 - Alterar janela da média móvel");
        System.out.println("  5 - Alterar mínimo de leituras para prever");
        System.out.println("  6 - Registrar medição manual");
        System.out.println("  7 - Ligar/desligar sensor (simular falha)");
        String opcao = lerLinha("  Opção: ");
        if (opcao == null) {
            return;
        }
        switch (opcao.trim()) {
            case "1" -> exibirDadosTerritorio();
            case "2" -> {
                territorio.setPopulacaoExposta(lerInteiro("  Nova população exposta (estimativa): "));
                System.out.println("População exposta atualizada.");
            }
            case "3" -> {
                TipoSensor tipo = escolherTipo();
                double atencao = lerDecimal("  Limiar de ATENÇÃO: ");
                double alerta = lerDecimal("  Limiar de ALERTA: ");
                double critico = lerDecimal("  Limiar CRÍTICO: ");
                territorio.setLimiares(tipo, atencao, alerta, critico);
                System.out.println("Limiares de " + tipo.getDescricao() + " atualizados.");
            }
            case "4" -> {
                config.setJanelaMediaMovel(lerInteiro("  Nova janela (2 a " + ConfiguracaoSistema.TAMANHO_CONSULTA + "): "));
                System.out.println("Janela atualizada para " + config.getJanelaMediaMovel() + ".");
            }
            case "5" -> {
                config.setMinimoLeituras(lerInteiro("  Novo mínimo de leituras (2 a " + ConfiguracaoSistema.TAMANHO_CONSULTA + "): "));
                System.out.println("Mínimo atualizado para " + config.getMinimoLeituras() + ".");
            }
            case "6" -> {
                SensorSimulado sensor = escolherSensor();
                double valor = lerDecimal("  Valor medido (" + sensor.getTipo().getUnidade() + "): ");
                Medicao medicao = servico.registrarManual(sensor.getId(), sensor.getTipo(), valor);
                System.out.println("Medição registrada: " + medicao);
            }
            case "7" -> {
                SensorSimulado sensor = escolherSensor();
                sensor.setLigado(!sensor.verificarFuncionamento());
                System.out.println("Sensor " + sensor.getId() + " agora está "
                        + (sensor.verificarFuncionamento() ? "LIGADO" : "DESLIGADO") + ".");
            }
            default -> System.out.println("Opção inválida.");
        }
    }

    private void exibirDadosTerritorio() {
        DadosTerritorio t = config.getTerritorio();
        System.out.println("  Território: " + t.getNome() + " | Região: " + t.getRegiao()
                + " | População exposta (estimativa): " + t.getPopulacaoExposta());
        for (TipoSensor tipo : TipoSensor.values()) {
            Limiares l = t.getLimiares(tipo);
            System.out.println("  " + tipo.getDescricao() + " (" + tipo.getUnidade() + "): atenção "
                    + Formatacao.numero(l.getAtencao()) + " | alerta " + Formatacao.numero(l.getAlerta())
                    + " | crítico " + Formatacao.numero(l.getCritico()));
        }
        System.out.println("  Janela da média móvel: " + config.getJanelaMediaMovel()
                + " | Mínimo de leituras para prever: " + config.getMinimoLeituras());
    }

    // status

    private void statusCompleto() throws SQLException {
        exibirDadosTerritorio();
        System.out.println();
        for (PontoMonitoramento ponto : pontos) {
            System.out.println("Ponto " + ponto.getId() + " - " + ponto.getNome() + ": " + ponto.verificarStatus());
            for (var sensor : ponto.getSensores()) {
                List<Medicao> historico = servico.historicoPorSensor(sensor.getId());
                String ultima = historico.isEmpty() ? "sem leituras"
                        : "última: " + historico.get(historico.size() - 1);
                System.out.println("   " + sensor.getId() + " (" + sensor.getTipo().getDescricao() + "): "
                        + (sensor.verificarFuncionamento() ? "funcionando" : "DESLIGADO")
                        + " | " + historico.size() + " leitura(s) | " + ultima);
            }
        }
        System.out.println();
        System.out.println("Alertas emitidos no total: " + servico.alertasEmitidos().size());
    }

    // entrada do usuário

    private SensorSimulado escolherSensor() throws DadosInvalidosException {
        for (int i = 0; i < sensores.size(); i++) {
            SensorSimulado s = sensores.get(i);
            System.out.println("   " + (i + 1) + ") " + s.getId() + " - " + s.getTipo().getDescricao());
        }
        int numero = lerInteiro("  Número do sensor: ");
        if (numero < 1 || numero > sensores.size()) {
            throw new DadosInvalidosException("Sensor inexistente: " + numero + " (escolha de 1 a " + sensores.size() + ").");
        }
        return sensores.get(numero - 1);  
    }

    private TipoSensor escolherTipo() throws DadosInvalidosException {
        TipoSensor[] tipos = TipoSensor.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println("   " + (i + 1) + ") " + tipos[i].getDescricao() + " (" + tipos[i].getUnidade() + ")");
        }
        int numero = lerInteiro("  Número do tipo: ");
        if (numero < 1 || numero > tipos.length) {
            throw new DadosInvalidosException("Tipo inexistente: " + numero + " (escolha de 1 a " + tipos.length + ").");
        }
        return tipos[numero - 1];
    }

    private String lerLinha(String pergunta) {
        System.out.print(pergunta);
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    private int lerInteiro(String pergunta) throws DadosInvalidosException {
        String texto = lerLinha(pergunta);
        try {
            return Integer.parseInt(texto == null ? "" : texto.trim());
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("'" + texto + "' não é um número inteiro válido.");
        }
    }

    private double lerDecimal(String pergunta) throws DadosInvalidosException {
        String texto = lerLinha(pergunta);
        try {
            return Double.parseDouble(texto == null ? "" : texto.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("'" + texto + "' não é um número válido.");
        }
    }
}
