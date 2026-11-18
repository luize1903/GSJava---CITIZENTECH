package dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

class DadosTerritorioTest {

    @Test
    void deveAtualizarLimiaresValidos() throws DadosInvalidosException {
      
        DadosTerritorio territorio = new DadosTerritorio("Várzea", "Zona Baixa", 500);

        // Act
        territorio.setLimiares(TipoSensor.PLUVIOMETRO, 20, 40, 70);

        // Assert
        assertEquals(40.0, territorio.getLimiares(TipoSensor.PLUVIOMETRO).getAlerta());
    }

    @Test
    void deveLancarDadosInvalidosQuandoLimiaresNaoSaoCrescentes() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio territorio = new DadosTerritorio("Várzea", "Zona Baixa", 500);

        // Act + Assert
        assertThrows(DadosInvalidosException.class,
                () -> territorio.setLimiares(TipoSensor.PLUVIOMETRO, 50, 40, 70));
    }

    @Test
    void deveLancarDadosInvalidosQuandoPopulacaoNegativa() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio territorio = new DadosTerritorio("Várzea", "Zona Baixa", 500);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> territorio.setPopulacaoExposta(-1));
    }

    @Test
    void deveClassificarNiveisDeAcordoComOsLimites() throws DadosInvalidosException {
        // Arrange
        Limiares limiares = new Limiares(25, 50, 80);

        // Act + Assert
        assertEquals(NivelAlerta.NORMAL, limiares.classificar(24.9));
        assertEquals(NivelAlerta.ATENCAO, limiares.classificar(25));
        assertEquals(NivelAlerta.ALERTA, limiares.classificar(60));
        assertEquals(NivelAlerta.CRITICO, limiares.classificar(80));
    }
}
