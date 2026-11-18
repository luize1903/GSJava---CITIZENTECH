package dominio;

import java.time.LocalDateTime;

//Alerta emitido a partir de uma previsão. Guarda o "porquê" na mensagem
public class Alerta {

    private final Long id;
    private final String sensorId;
    private final TipoSensor tipo;
    private final NivelAlerta nivel;
    private final double valorPrevisto;
    private final double limiteUltrapassado;
    private final String estrategia;
    private final String mensagem;
    private final LocalDateTime dataHora;

    // Construtor para alerta novo: ainda não tem id (o banco gera ao salvar)
// e a data e hora são as de agora. Reaproveita o construtor completo com this(...).
    public Alerta(String sensorId, TipoSensor tipo, NivelAlerta nivel, double valorPrevisto,
                  double limiteUltrapassado, String estrategia, String mensagem) {
        this(null, sensorId, tipo, nivel, valorPrevisto, limiteUltrapassado, estrategia, mensagem,
                LocalDateTime.now());
    }

    public Alerta(Long id, String sensorId, TipoSensor tipo, NivelAlerta nivel, double valorPrevisto,
                  double limiteUltrapassado, String estrategia, String mensagem, LocalDateTime dataHora) {
        this.id = id;
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.nivel = nivel;
        this.valorPrevisto = valorPrevisto;
        this.limiteUltrapassado = limiteUltrapassado;
        this.estrategia = estrategia;
        this.mensagem = mensagem;
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public TipoSensor getTipo() {
        return tipo;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public double getLimiteUltrapassado() {
        return limiteUltrapassado;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return dataHora.toString().replace('T', ' ').substring(0, 19) + " | " + mensagem;
    }
}
