package servico;

import dominio.Medicao;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** BÔNUS: exporta o histórico consultado para um arquivo CSV. */
public final class ExportadorCsv {

    private ExportadorCsv() {
    }

    public static void exportarMedicoes(List<Medicao> medicoes, Path destino) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            escritor.write("sensor_id,tipo,valor,unidade,data_hora");
            escritor.newLine();
            for (Medicao m : medicoes) {
                escritor.write(m.getSensorId() + "," + m.getTipo().name() + "," + m.getValor() + ","
                        + m.getTipo().getUnidade() + "," + m.getDataHora());
                escritor.newLine();
            }
        }
    }
}
