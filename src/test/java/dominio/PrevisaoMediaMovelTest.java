package dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import excecao.DadosInvalidosException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PrevisaoMediaMovelTest {

    static List<Medicao> historico(double... valores) throws DadosInvalidosException {
        List<Medicao> lista = new ArrayList<>();
        for (double v : valores) {
            lista.add(new Medicao(null, "PLV-01", TipoSensor.PLUVIOMETRO, v, LocalDateTime.now()));
        }
        return lista;
    }

    @Test
    void deveCalcularMediaApenasDasUltimasNLeituras() throws DadosInvalidosException {
        // Arrange: janela 3  só 30, 40 e 50 entram na conta
        EstrategiaPrevisao estrategia = new PrevisaoMediaMovel(3);
        List<Medicao> historico = historico(10, 20, 30, 40, 50);

        // Act
        double previsto = estrategia.prever(historico);

        // Assert
        assertEquals(40.0, previsto, 0.0001);
    }

    @Test
    void deveUsarTodoOHistoricoQuandoHaMenosLeiturasQueAJanela() throws DadosInvalidosException {
        // Arrange
        EstrategiaPrevisao estrategia = new PrevisaoMediaMovel(10);
        List<Medicao> historico = historico(10, 20);

        // Act
        double previsto = estrategia.prever(historico);

        // Assert
        assertEquals(15.0, previsto, 0.0001);
    }

    @Test
    void deveLancarDadosInvalidosQuandoHistoricoVazio() throws DadosInvalidosException {
        // Arrange
        EstrategiaPrevisao estrategia = new PrevisaoMediaMovel(3);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> estrategia.prever(new ArrayList<>()));
    }

    @Test
    void deveLancarDadosInvalidosQuandoJanelaMenorQueUm() {
        // Arrange + Act + Assert
        assertThrows(DadosInvalidosException.class, () -> new PrevisaoMediaMovel(0));
    }

    @Test
    void comparacaoDeveProjetarAVariacaoEntreOsDoisPeriodos() throws DadosInvalidosException {
        // Arrange: período anterior = 10,10,10 (média 10); recente = 20,20,20 (média 20); última = 20
        EstrategiaPrevisao estrategia = new PrevisaoComparacao(3);
        List<Medicao> historico = historico(10, 10, 10, 20, 20, 20);

        // Act
        double previsto = estrategia.prever(historico);

        // Assert: 20 (última) + (20 - 10) = 30
        assertEquals(30.0, previsto, 0.0001);
    }

    @Test
    void estrategiasDevemPreverValoresDiferentesParaOMesmoHistorico() throws DadosInvalidosException {
        // Arrange: série em alta
        List<Medicao> historico = historico(10, 10, 10, 20, 20, 20);
        EstrategiaPrevisao mediaMovel = new PrevisaoMediaMovel(3);
        EstrategiaPrevisao comparacao = new PrevisaoComparacao(3);

        // Act
        double porMedia = mediaMovel.prever(historico);
        double porComparacao = comparacao.prever(historico);

        // Assert: média móvel = 20; comparação enxerga a tendência de alta = 30
        assertEquals(20.0, porMedia, 0.0001);
        assertEquals(30.0, porComparacao, 0.0001);
    }
}
