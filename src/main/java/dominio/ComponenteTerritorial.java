package dominio;

import excecao.DadosInvalidosException;


public abstract class ComponenteTerritorial {

    private final String id;
    private String nome;
    private String regiao;
    private String status;

    protected ComponenteTerritorial(String id, String nome, String regiao, String status)
            throws DadosInvalidosException {
        if (id == null || id.isBlank() || nome == null || nome.isBlank() || regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("Id, nome e região do componente são obrigatórios.");
        }
        this.id = id;
        this.nome = nome;
        this.regiao = regiao;
        this.status = status;
    }

    // Cada tipo de componente decide como calcular o próprio status. 
    public abstract String verificarStatus();

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public String getStatus() {
        return status;
    }

    protected void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + " - " + nome + " (" + regiao + ") [" + status + "]";
    }
}
