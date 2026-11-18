package servico;

import dominio.EstrategiaPrevisao;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import excecao.DadosInvalidosException;

/**
 * Catálogo das estratégias disponíveis. Cada constante sabe criar a sua estratégia,
 * então a FabricaPrevisao não precisa de if/switch espalhado.
 * Para criar uma nova estratégia basta incluir uma constante aqui.
 */
public enum TipoPrevisao {
    MEDIA_MOVEL(cfg -> new PrevisaoMediaMovel(cfg.getJanelaMediaMovel())),
    COMPARACAO(cfg -> new PrevisaoComparacao(cfg.getJanelaMediaMovel()));

    @FunctionalInterface
    interface Criador {
        EstrategiaPrevisao criar(ConfiguracaoSistema config) throws DadosInvalidosException;
    }

    private final Criador criador;

    TipoPrevisao(Criador criador) {
        this.criador = criador;
    }

    EstrategiaPrevisao criar(ConfiguracaoSistema config) throws DadosInvalidosException {
        return criador.criar(config);
    }
}
