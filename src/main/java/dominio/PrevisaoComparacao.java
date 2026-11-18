package dominio;

import excecao.DadosInvalidosException;
import java.util.List;


public class PrevisaoComparacao extends EstrategiaPrevisao {

    private final int janela;

    public PrevisaoComparacao(int janela) throws DadosInvalidosException {
        super("Comparação com período anterior");
        if (janela < 1) {
            throw new DadosInvalidosException("A janela de comparação deve ser no mínimo 1 (informado: " + janela + ").");
        }
        this.janela = janela;
    }

    @Override
    public String getDescricao() {
        return "última leitura + variação entre as médias de dois períodos de até " + janela + " leituras";
    }

    @Override
    public double prever(List<Medicao> historico) throws DadosInvalidosException {
        double ultima = super.prever(historico); // valida e devolve a última leitura
        int n = Math.min(janela, historico.size() / 2);
        if (n < 1) {
            return ultima; // só 1 leitura: não há com o que comparar
        }
        int fim = historico.size();
        double mediaRecente = media(historico, fim - n, fim);
        double mediaAnterior = media(historico, fim - 2 * n, fim - n);
        double variacao = mediaRecente - mediaAnterior;
        return Math.max(0, ultima + variacao);
    }

    private double media(List<Medicao> historico, int inicio, int fim) {
        double soma = 0;
        for (int i = inicio; i < fim; i++) {
            soma += historico.get(i).getValor();
        }
        return soma / (fim - inicio);
    }
}
