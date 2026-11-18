package dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

class MedicaoTest {

    // JUnit é um framework utilizado para criar e executar testes automatizados em Java,
// verificando se o código funciona conforme o esperado.

// @Test indica que o método abaixo é um teste automatizado,
// permitindo que o JUnit() execute e verifique seu funcionamento.
    @Test 
    void deveCriarMedicaoQuandoValorEstaNaFaixa() throws DadosInvalidosException {
        // Arrange
        double valor = 42.5;

        // Act
        Medicao medicao = new Medicao("PLV-01", TipoSensor.PLUVIOMETRO, valor);

        // Assert
        assertEquals(42.5, medicao.getValor());
        assertEquals("PLV-01", medicao.getSensorId());
    }
// Arrange: prepara os dados para o teste.
// Act: executa o método ou ação que será testada.
// Assert: verifica se o resultado obtido é igual ao esperado.
    @Test
    void deveLancarDadosInvalidosQuandoChuvaNegativa() {   
        // Arrange
        double chuvaNegativa = -5;                                              

        // Act
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> new Medicao("PLV-01", TipoSensor.PLUVIOMETRO, chuvaNegativa));

        // Assert
        assertTrue(erro.getMessage().contains("inválida"));
    }

    @Test
    void deveLancarDadosInvalidosQuandoUmidadeAcimaDe100() {
        // Arrange
        double umidadeImpossivel = 140;

        // Act + Assert
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao("UMI-01", TipoSensor.UMIDADE_SOLO, umidadeImpossivel));
    }

    @Test
    void deveLancarDadosInvalidosQuandoValorNaoENumero() {
        // Arrange
        double invalido = Double.NaN;

        // Act + Assert
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao("NIV-01", TipoSensor.NIVEL_CORREGO, invalido));
    }
}
