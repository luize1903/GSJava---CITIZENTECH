package dominio;

import java.util.Random;

// Simula um sensor de umidade do solo (%). 
public class SensorUmidadeSolo extends SensorSimulado implements Sensor {

    public SensorUmidadeSolo(String id, Random random, double probabilidadeFalha) {
        super(id, TipoSensor.UMIDADE_SOLO, random, 55, 8, 10, 100, probabilidadeFalha);
    }
}
