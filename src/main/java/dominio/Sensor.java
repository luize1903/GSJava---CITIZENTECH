package dominio;

import excecao.FonteIndisponivelException;

// simulado hoje, real amanhã. 
public interface Sensor {

    String getId();

    // Lê o valor atual. Lança FonteIndisponivelException se o sensor não responder. 
    double lerValor() throws FonteIndisponivelException;

    boolean verificarFuncionamento();

    TipoSensor getTipo();
}
