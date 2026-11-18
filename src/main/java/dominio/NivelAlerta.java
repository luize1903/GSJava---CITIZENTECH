package dominio;

// Níveis de alerta, do mais leve ao mais grave. NORMAL significa "sem alerta". 
public enum NivelAlerta {
    NORMAL("NORMAL", "nenhuma ação necessária"),
    ATENCAO("ATENÇÃO", "acompanhar as próximas leituras com mais frequência"),
    ALERTA("ALERTA", "avisar a liderança comunitária e preparar ações preventivas nos pontos baixos"),
    CRITICO("CRÍTICO", "acionar a Defesa Civil (199) e orientar a saída das áreas mais baixas");

    private final String rotulo;
    private final String sugestao;

    NivelAlerta(String rotulo, String sugestao) {
        this.rotulo = rotulo;
        this.sugestao = sugestao;
    }

    public String getRotulo() {
        return rotulo;
    }

    public String getSugestao() {
        return sugestao;
    }
}
