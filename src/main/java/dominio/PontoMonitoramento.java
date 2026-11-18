package dominio;

import excecao.DadosInvalidosException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PontoMonitoramento extends ComponenteTerritorial {

    private final List<Sensor> sensores = new ArrayList<>();

    public PontoMonitoramento(String id, String nome, String regiao) throws DadosInvalidosException {
        super(id, nome, regiao, "OPERACIONAL");
    }

    public void adicionarSensor(Sensor sensor) {
        sensores.add(sensor);
    }

    public List<Sensor> getSensores() {
        return Collections.unmodifiableList(sensores);
    }

    @Override
    public String verificarStatus() {
        long ativos = sensores.stream().filter(Sensor::verificarFuncionamento).count();
        String novoStatus;
        if (sensores.isEmpty() || ativos == 0) {
            novoStatus = "OFFLINE";
        } else if (ativos < sensores.size()) {
            novoStatus = "DEGRADADO";
        } else {
            novoStatus = "OPERACIONAL";
        }
        setStatus(novoStatus);
        return novoStatus;
    }
}
