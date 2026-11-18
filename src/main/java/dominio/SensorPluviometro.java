package dominio;

import java.util.Random;

//Simula um pluviômetro (chuva em mm/h). 
public class SensorPluviometro extends SensorSimulado implements Sensor {

    public SensorPluviometro(String id, Random random, double probabilidadeFalha) {
        super(id, TipoSensor.PLUVIOMETRO, random, 10, 10, 0, 100, probabilidadeFalha);
    }
}
