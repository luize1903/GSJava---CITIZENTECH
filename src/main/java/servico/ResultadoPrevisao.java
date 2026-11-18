package servico;

import dominio.Alerta;
import dominio.NivelAlerta;

/** Resultado de rodar uma previsão: valor, significado e (se houve) o alerta emitido. */
public class ResultadoPrevisao {

    private final String sensorId;
    private final String estrategia;
    private final boolean confiavel;
    private final double valorPrevisto;
    private final NivelAlerta nivel;
    private final Alerta alerta; // null quando não houve alerta
    private final String explicacao;

    public ResultadoPrevisao(String sensorId, String estrategia, boolean confiavel, double valorPrevisto,
                             NivelAlerta nivel, Alerta alerta, String explicacao) {
        this.sensorId = sensorId;
        this.estrategia = estrategia;
        this.confiavel = confiavel;
        this.valorPrevisto = valorPrevisto;
        this.nivel = nivel;
        this.alerta = alerta;
        this.explicacao = explicacao;
    }

    /** Histórico pequeno demais: o sistema "se cala" em vez de chutar. */
    public static ResultadoPrevisao historicoInsuficiente(String sensorId, String estrategia, int leituras, int minimo) {
        String texto = "Previsão suspensa: o sensor " + sensorId + " tem apenas " + leituras
                + " leitura(s) no banco (mínimo: " + minimo + "). Faça mais leituras antes de prever.";
        return new ResultadoPrevisao(sensorId, estrategia, false, Double.NaN, NivelAlerta.NORMAL, null, texto);
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public boolean isConfiavel() {
        return confiavel;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public Alerta getAlerta() {
        return alerta;
    }

    public String getExplicacao() {
        return explicacao;
    }
}
