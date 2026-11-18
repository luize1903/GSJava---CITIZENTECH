package dominio;

import excecao.DadosInvalidosException;
import java.util.List;

// média das últimas N medições. 
public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    private final int janela;

    public PrevisaoMediaMovel(int janela) throws DadosInvalidosException {
        super("Média móvel"); // chama o construtor da classe pai
        if (janela < 1) {
            throw new DadosInvalidosException("A janela da média móvel deve ser no mínimo 1 (informado: " + janela + ").");
        }
        this.janela = janela;
    }

    @Override
    public String getDescricao() {
        return "média das últimas " + janela + " leituras";
    }

    @Override
    public double prever(List<Medicao> historico) throws DadosInvalidosException {
        super.prever(historico); // reaproveita a validação da classe pai
        int n = Math.min(janela, historico.size());
        double soma = 0;
        for (int i = historico.size() - n; i < historico.size(); i++) {
            soma += historico.get(i).getValor();
        }
        return soma / n;
    }
}
