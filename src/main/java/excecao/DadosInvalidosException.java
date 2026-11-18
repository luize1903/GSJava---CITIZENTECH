package excecao;

//nessa parte o sistema obriga quem chama a tratar o erro
public class DadosInvalidosException extends Exception {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
