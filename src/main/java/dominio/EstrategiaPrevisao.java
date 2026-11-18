package dominio;

import excecao.DadosInvalidosException;
import java.util.List;


public abstract class EstrategiaPrevisao {

    private final String nome;

    protected EstrategiaPrevisao(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    //Aqui: Explica em linguagem simples como a previsão é calculada. 
    public abstract String getDescricao();

    public double prever(List<Medicao> historico) throws DadosInvalidosException {
        if (historico == null || historico.isEmpty()) {
            throw new DadosInvalidosException("Não há medições no histórico para calcular a previsão.");
        }
        return historico.get(historico.size() - 1).getValor();
    }
}
