package dominio;

import excecao.DadosInvalidosException;

// (ATENÇÃO < ALERTA < CRÍTICO) 
public class Limiares {

    private final double atencao;
    private final double alerta;
    private final double critico;

    public Limiares(double atencao, double alerta, double critico) throws DadosInvalidosException {
        boolean finitos = Double.isFinite(atencao) && Double.isFinite(alerta) && Double.isFinite(critico);
        if (!finitos || !(atencao > 0 && atencao < alerta && alerta < critico)) {
            throw new DadosInvalidosException(String.format(
                    "Limiares inválidos (atenção=%s, alerta=%s, crítico=%s): devem ser positivos e crescentes "
                            + "(atenção < alerta < crítico).",
                    Formatacao.numero(atencao), Formatacao.numero(alerta), Formatacao.numero(critico)));
        }
        this.atencao = atencao;
        this.alerta = alerta;
        this.critico = critico;
    }

    // Devolve o maior nível cujo limite foi atingido pelo valor. 
    public NivelAlerta classificar(double valor) {
        if (valor >= critico) {
            return NivelAlerta.CRITICO;
        }
        if (valor >= alerta) {
            return NivelAlerta.ALERTA;
        }
        if (valor >= atencao) {
            return NivelAlerta.ATENCAO;
        }
        return NivelAlerta.NORMAL;
    }

    // Limite que define o nível informado (0 para NORMAL). 
    public double limitePara(NivelAlerta nivel) {
        return switch (nivel) {
            case CRITICO -> critico;
            case ALERTA -> alerta;
            case ATENCAO -> atencao;
            case NORMAL -> 0;
        };
    }

    public double getAtencao() {
        return atencao;
    }

    public double getAlerta() {
        return alerta;
    }

    public double getCritico() {
        return critico;
    }
}
