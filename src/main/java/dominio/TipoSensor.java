package dominio;

//Fora essa medição é invalida
public enum TipoSensor {
    PLUVIOMETRO("Chuva", "mm/h", 0, 300),
    NIVEL_CORREGO("Nível do córrego", "cm", 0, 500),
    UMIDADE_SOLO("Umidade do solo", "%", 0, 100);

    private final String descricao;
    private final String unidade;
    private final double minimo;
    private final double maximo;

    TipoSensor(String descricao, String unidade, double minimo, double maximo) {
        this.descricao = descricao;
        this.unidade = unidade;
        this.minimo = minimo;
        this.maximo = maximo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getUnidade() {
        return unidade;
    }

    public double getMinimo() {
        return minimo;
    }

    public double getMaximo() {
        return maximo;
    }

    public boolean aceita(double valor) {
        return !Double.isNaN(valor) && valor >= minimo && valor <= maximo;
    }
}
