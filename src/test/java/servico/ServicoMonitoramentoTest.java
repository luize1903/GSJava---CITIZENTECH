package servico;

// confere se dois valores são iguais
import static org.junit.jupiter.api.Assertions.assertEquals;
// confere se a condição é falsa
import static org.junit.jupiter.api.Assertions.assertFalse;
// confere se o valor não é nulo
import static org.junit.jupiter.api.Assertions.assertNotNull;
// confere se o código lança a exceção esperada
import static org.junit.jupiter.api.Assertions.assertThrows;
// confere se a condição é verdadeira
import static org.junit.jupiter.api.Assertions.assertTrue;
// aqui tivemos que pesquisar, ele faz aceitar qualquer objeto do tipo indicado, por exemplo any(Alerta.class)
import static org.mockito.ArgumentMatchers.any;
// aceita qualquer número inteiro
import static org.mockito.ArgumentMatchers.anyInt;
// é usado quando o outro parâmetro é curinga
import static org.mockito.ArgumentMatchers.eq;
// verifica que um método NÃO foi chamado
import static org.mockito.Mockito.mock;
// verifica que um método foi chamado um número exato de vezes
import static org.mockito.Mockito.never;
// confere se o método foi chamado no mock
import static org.mockito.Mockito.times;
// define o que o mock deve responder, por exemplo when(sensor.getId()).thenReturn("PLV-01")
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dominio.Alerta;
import dominio.EstrategiaPrevisao;
import dominio.Medicao;
import dominio.NivelAlerta;
import dominio.PrevisaoMediaMovel;
import dominio.Sensor;
import dominio.TipoSensor;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;


class ServicoMonitoramentoTest {

    private MedicaoDAO medicaoDAO;
    private AlertaDAO alertaDAO;
    private Sensor sensor;
    private ServicoMonitoramento servico;

    @BeforeEach
    void preparar() {
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstance();
        config.restaurarPadroes();
        medicaoDAO = mock(MedicaoDAO.class);
        alertaDAO = mock(AlertaDAO.class);
        sensor = mock(Sensor.class);
        when(sensor.getId()).thenReturn("PLV-01");
        when(sensor.getTipo()).thenReturn(TipoSensor.PLUVIOMETRO);
        servico = new ServicoMonitoramento(medicaoDAO, alertaDAO, config);
    }

    private List<Medicao> historico(double... valores) throws DadosInvalidosException {
        List<Medicao> lista = new ArrayList<>();
        for (double v : valores) {
            lista.add(new Medicao(null, "PLV-01", TipoSensor.PLUVIOMETRO, v, LocalDateTime.now()));
        }
        return lista;
    }

    @Test
    void deveEmitirAlertaCriticoQuandoPrevisaoUltrapassaOLimiteCritico() throws Exception {
        // Arrange: média das 5 leituras = 90 mm/h (limiar crítico = 80)
        when(medicaoDAO.buscarUltimasPorSensor(eq("PLV-01"), anyInt())).thenReturn(historico(85, 90, 90, 95, 90));
        EstrategiaPrevisao estrategia = new PrevisaoMediaMovel(5);

        // Act
        ResultadoPrevisao resultado = servico.preverEAlertar(sensor, estrategia);

        // Assert
        assertEquals(NivelAlerta.CRITICO, resultado.getNivel());
        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaDAO, times(1)).salvar(captor.capture());
        Alerta salvo = captor.getValue();
        assertEquals("PLV-01", salvo.getSensorId());
        assertEquals(80.0, salvo.getLimiteUltrapassado(), 0.0001);
        assertTrue(salvo.getMensagem().contains("PLV-01"));
    }

    @Test
    void naoDeveEmitirAlertaQuandoPrevisaoEstaAbaixoDoLimiar() throws Exception {
        // Arrange: média = 8 mm/h (limiar de atenção = 25)
        when(medicaoDAO.buscarUltimasPorSensor(eq("PLV-01"), anyInt())).thenReturn(historico(5, 8, 10, 7, 10));

        // Act
        ResultadoPrevisao resultado = servico.preverEAlertar(sensor, new PrevisaoMediaMovel(5));

        // Assert
        assertEquals(NivelAlerta.NORMAL, resultado.getNivel());
        verify(alertaDAO, never()).salvar(any(Alerta.class));
    }

    @Test
    void deveSeCalarQuandoHistoricoEMenorQueOMinimo() throws Exception {
        // Arrange: só 3 leituras, mínimo padrão = 5, mesmo com valores altíssimos
        when(medicaoDAO.buscarUltimasPorSensor(eq("PLV-01"), anyInt())).thenReturn(historico(95, 99, 98));

        // Act
        ResultadoPrevisao resultado = servico.preverEAlertar(sensor, new PrevisaoMediaMovel(5));

        // Assert
        assertFalse(resultado.isConfiavel());
        assertNotNull(resultado.getExplicacao());
        verify(alertaDAO, never()).salvar(any(Alerta.class));
    }

    @Test
    void deveLancarFonteIndisponivelQuandoSensorFalha() throws Exception {
        // Arrange
        when(sensor.verificarFuncionamento()).thenReturn(true);
        when(sensor.lerValor()).thenThrow(new FonteIndisponivelException("sem sinal"));

        // Act + Assert
        assertThrows(FonteIndisponivelException.class, () -> servico.lerESalvar(sensor));
        verify(medicaoDAO, never()).salvar(any(Medicao.class));
    }

    @Test
    void naoDeveGravarMedicaoInvalida() throws Exception {
        // Arrange: sensor devolve chuva negativa
        when(sensor.verificarFuncionamento()).thenReturn(true);
        when(sensor.lerValor()).thenReturn(-10.0);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> servico.lerESalvar(sensor));
        verify(medicaoDAO, never()).salvar(any(Medicao.class));
    }

    @Test
    void deveGravarMedicaoValidaNoRepositorio() throws Exception {
        // Arrange
        when(sensor.verificarFuncionamento()).thenReturn(true);
        when(sensor.lerValor()).thenReturn(33.3);

        // Act
        Medicao medicao = servico.lerESalvar(sensor);

        // Assert
        assertEquals(33.3, medicao.getValor(), 0.0001);
        verify(medicaoDAO, times(1)).salvar(any(Medicao.class));
    }
}
