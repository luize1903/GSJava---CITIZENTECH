package dominio;

import java.util.Random;

// Simula um medidor de nível do córrego (cm). 
public class SensorNivelCorrego extends SensorSimulado implements Sensor {

    public SensorNivelCorrego(String id, Random random, double probabilidadeFalha) {
        super(id, TipoSensor.NIVEL_CORREGO, random, 80, 25, 0, 400, probabilidadeFalha);
    }
}
