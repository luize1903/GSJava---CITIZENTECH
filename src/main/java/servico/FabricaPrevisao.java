package servico;

import dominio.EstrategiaPrevisao;
import excecao.DadosInvalidosException;
import java.util.ArrayList;
import java.util.List;

/**
 * PADRÃO FACTORY: concentra a criação das estratégias de previsão a partir da configuração.
 * Quem usa (menu, serviço, testes) não conhece as classes concretas nem usa "new" nelas.
 */
public final class FabricaPrevisao {

    private FabricaPrevisao() {
    }

    public static EstrategiaPrevisao criar(TipoPrevisao tipo, ConfiguracaoSistema config)
            throws DadosInvalidosException {
        if (tipo == null) {
            throw new DadosInvalidosException("Informe o tipo de previsão a ser criado.");
        }
        return tipo.criar(config);
    }

    /** Cria uma estratégia de cada tipo (usado para rodar todas de forma polimórfica). */
    public static List<EstrategiaPrevisao> criarTodas(ConfiguracaoSistema config) throws DadosInvalidosException {
        List<EstrategiaPrevisao> estrategias = new ArrayList<>();
        for (TipoPrevisao tipo : TipoPrevisao.values()) {
            estrategias.add(criar(tipo, config));
        }
        return estrategias;
    }
}
