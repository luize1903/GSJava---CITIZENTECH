package dominio;

import excecao.DadosInvalidosException;
import java.util.EnumMap;
import java.util.Map;


public class DadosTerritorio {

    private String nome;
    private String regiao;
    private int populacaoExposta;
    private final Map<TipoSensor, Limiares> limiares = new EnumMap<>(TipoSensor.class);

    public DadosTerritorio(String nome, String regiao, int populacaoExposta) throws DadosInvalidosException {
        setNome(nome);
        setRegiao(regiao);
        setPopulacaoExposta(populacaoExposta);
        // Limiares iniciais (podem ser alterados pelo menu)
        limiares.put(TipoSensor.PLUVIOMETRO, new Limiares(25, 50, 80));       // mm/h
        limiares.put(TipoSensor.NIVEL_CORREGO, new Limiares(150, 250, 350));  // cm
        limiares.put(TipoSensor.UMIDADE_SOLO, new Limiares(70, 85, 95));      // %
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) throws DadosInvalidosException {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome do território não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) throws DadosInvalidosException {
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("A região do território não pode ser vazia.");
        }
        this.regiao = regiao.trim();
    }

    public int getPopulacaoExposta() {
        return populacaoExposta;
    }

    public void setPopulacaoExposta(int populacaoExposta) throws DadosInvalidosException {
        if (populacaoExposta < 0) {
            throw new DadosInvalidosException(
                    "A população exposta não pode ser negativa (informado: " + populacaoExposta + ").");
        }
        this.populacaoExposta = populacaoExposta;
    }

    public Limiares getLimiares(TipoSensor tipo) {
        return limiares.get(tipo);
    }

    //Aqui são: Código de acesso para alterar limiares: a validação acontece na criação de Limiares. 
    public void setLimiares(TipoSensor tipo, double atencao, double alerta, double critico)
            throws DadosInvalidosException {
        if (tipo == null) {
            throw new DadosInvalidosException("Informe o tipo de sensor para alterar os limiares.");
        }
        Limiares novos = new Limiares(atencao, alerta, critico); // lança se inválido
        limiares.put(tipo, novos);
    }
}
