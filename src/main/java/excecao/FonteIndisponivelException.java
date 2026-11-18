package excecao;

//Aqui os dados da falha, quando está sem cominicação ou quando gerou leituras indisponíveis
public class FonteIndisponivelException extends Exception {

    public FonteIndisponivelException(String mensagem) {
        super(mensagem);
    }

    public FonteIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
