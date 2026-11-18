package servico;

import dominio.DadosTerritorio;
import excecao.DadosInvalidosException;

/**
 * PADRÃO SINGLETON: existe uma única instância com as configurações do sistema
 * (dados do território com limiares, janela da média móvel, mínimo de leituras).
 * O construtor é privado; todos acessam por getInstance().
 */
public final class ConfiguracaoSistema {

    // Quantas leituras recentes o serviço busca no banco para prever. 
    public static final int TAMANHO_CONSULTA = 20;

    private static ConfiguracaoSistema instancia;

    private DadosTerritorio territorio;
    private int janelaMediaMovel;
    private int minimoLeituras;

    private ConfiguracaoSistema() {
        restaurarPadroes();
    }

    public static synchronized ConfiguracaoSistema getInstance() {
        if (instancia == null) {
            instancia = new ConfiguracaoSistema();
        }
        return instancia;
    }

    /** Volta tudo para os valores iniciais (também útil para os testes). */
    public final synchronized void restaurarPadroes() {
        try {
            this.territorio = new DadosTerritorio("Várzea do Córrego", "Zona Baixa", 1200);
        } catch (DadosInvalidosException e) {
            throw new IllegalStateException("Configuração padrão do território é inválida: " + e.getMessage(), e);
        }
        this.janelaMediaMovel = 5;
        this.minimoLeituras = 5;
    }

    public DadosTerritorio getTerritorio() {
        return territorio;
    }

    public int getJanelaMediaMovel() {
        return janelaMediaMovel;
    }

    public void setJanelaMediaMovel(int janela) throws DadosInvalidosException {
        if (janela < 2 || janela > TAMANHO_CONSULTA) {
            throw new DadosInvalidosException("A janela da média móvel deve ficar entre 2 e "
                    + TAMANHO_CONSULTA + " (informado: " + janela + ").");
        }
        this.janelaMediaMovel = janela;
    }

    public int getMinimoLeituras() {
        return minimoLeituras;
    }

    public void setMinimoLeituras(int minimo) throws DadosInvalidosException {
        if (minimo < 2 || minimo > TAMANHO_CONSULTA) {
            throw new DadosInvalidosException("O mínimo de leituras para prever deve ficar entre 2 e "
                    + TAMANHO_CONSULTA + " (informado: " + minimo + ").");
        }
        this.minimoLeituras = minimo;
    }
}
