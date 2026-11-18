package dominio;

import excecao.DadosInvalidosException;
import java.time.LocalDateTime;


public class Medicao {

    private final Long id; // null até ser gravada no banco
    private final String sensorId;
    private final TipoSensor tipo;
    private final double valor;
    private final LocalDateTime dataHora;

    /** Cria uma medição nova (agora, ainda sem id). */
    public Medicao(String sensorId, TipoSensor tipo, double valor) throws DadosInvalidosException {
        this(null, sensorId, tipo, valor, LocalDateTime.now());
    }

    // Aqui: cria uma medição completa
    public Medicao(Long id, String sensorId, TipoSensor tipo, double valor, LocalDateTime dataHora)
            throws DadosInvalidosException {
        if (sensorId == null || sensorId.isBlank()) {
            throw new DadosInvalidosException("A medição precisa informar o sensor de origem.");
        }
        if (tipo == null) {
            throw new DadosInvalidosException("A medição do sensor " + sensorId + " precisa de um tipo.");
        }
        if (!tipo.aceita(valor)) {
            throw new DadosInvalidosException(String.format(
                    "Medição de %s inválida no sensor %s: %s %s. Faixa aceita: %s a %s %s.",
                    tipo.getDescricao().toLowerCase(), sensorId,
                    Double.isNaN(valor) ? "NaN" : Formatacao.numero(valor), tipo.getUnidade(),
                    Formatacao.numero(tipo.getMinimo()), Formatacao.numero(tipo.getMaximo()),
                    tipo.getUnidade()));
        }
        if (dataHora == null) {
            throw new DadosInvalidosException("A medição precisa ter data e hora.");
        }
        this.id = id;
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.valor = valor;
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

    public double getValor() {
        return valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s %s", dataHora.toString().replace('T', ' ').substring(0, 19),
                sensorId, Formatacao.numero(valor), tipo.getUnidade());
    }
}
