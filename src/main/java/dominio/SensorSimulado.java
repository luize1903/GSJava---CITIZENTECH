package dominio;

import excecao.FonteIndisponivelException;
import java.util.Random;


public abstract class SensorSimulado implements Sensor {

    private final String id;
    private final TipoSensor tipo;
    private final Random random;
    private final double passo;
    private final double minimoSimulado;
    private final double maximoSimulado;
    private final double probabilidadeFalha;
    private double valorAtual;
    private boolean ligado = true;

    protected SensorSimulado(String id, TipoSensor tipo, Random random, double valorInicial, double passo,
                             double minimoSimulado, double maximoSimulado, double probabilidadeFalha) {
        this.id = id;
        this.tipo = tipo;
        this.random = random;
        this.valorAtual = valorInicial;
        this.passo = passo;
        this.minimoSimulado = minimoSimulado;
        this.maximoSimulado = maximoSimulado;
        this.probabilidadeFalha = probabilidadeFalha;
    }

    @Override
    public double lerValor() throws FonteIndisponivelException {
        if (!ligado) {
            throw new FonteIndisponivelException("O sensor " + id + " está desligado.");
        }
        if (random.nextDouble() < probabilidadeFalha) {
            throw new FonteIndisponivelException("Falha de comunicação com o sensor " + id + " (simulada).");
        }
        double novo = valorAtual + random.nextGaussian() * passo;
        novo = Math.max(minimoSimulado, Math.min(maximoSimulado, novo));
        valorAtual = Math.round(novo * 10.0) / 10.0;
        return valorAtual;
    }

    @Override
    public boolean verificarFuncionamento() {
        return ligado;
    }

    public void setLigado(boolean ligado) {
        this.ligado = ligado;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TipoSensor getTipo() {
        return tipo;
    }
}
