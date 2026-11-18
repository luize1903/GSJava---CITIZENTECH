package servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dominio.EstrategiaPrevisao;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;       
import excecao.DadosInvalidosException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FabricaEConfiguracaoTest {

    @BeforeEach
    void restaurar() {
        ConfiguracaoSistema.getInstance().restaurarPadroes();
    }

    @Test
    void singletonDeveDevolverSempreAMesmaInstancia() {
        // Arrange + Act
        ConfiguracaoSistema a = ConfiguracaoSistema.getInstance();
        ConfiguracaoSistema b = ConfiguracaoSistema.getInstance();

        // Assert
        assertSame(a, b);
    }

    @Test
    void fabricaDeveCriarAEstrategiaCerta() throws DadosInvalidosException {
        // Arrange
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstance();

        // Act
        EstrategiaPrevisao media = FabricaPrevisao.criar(TipoPrevisao.MEDIA_MOVEL, config);
        EstrategiaPrevisao comparacao = FabricaPrevisao.criar(TipoPrevisao.COMPARACAO, config);

        // Assert
        assertInstanceOf(PrevisaoMediaMovel.class, media);
        assertInstanceOf(PrevisaoComparacao.class, comparacao);
    }

    @Test
    void fabricaDeveCriarUmaEstrategiaParaCadaTipo() throws DadosInvalidosException {
        // Arrange + Act
        List<EstrategiaPrevisao> todas = FabricaPrevisao.criarTodas(ConfiguracaoSistema.getInstance());

        // Assert
        assertEquals(TipoPrevisao.values().length, todas.size());
    }

    @Test
    void configuracaoDeveRejeitarJanelaInvalida() { 
        // Arrange
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstance();

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> config.setJanelaMediaMovel(1));
    }
}
