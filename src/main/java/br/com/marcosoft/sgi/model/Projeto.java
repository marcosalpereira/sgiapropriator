package br.com.marcosoft.sgi.model;

public class Projeto {
    private String mnemonico;
    private String ug;
    private boolean lotacaoSuperior;
    private String nomeProjeto;
    public String getMnemonico() {
        return mnemonico;
    }
    public void setMnemonico(String mnemonico) {
        this.mnemonico = mnemonico;
    }
    public String getUg() {
        return ug;
    }
    public void setUg(String ug) {
        this.ug = ug;
    }
    public boolean isLotacaoSuperior() {
        return lotacaoSuperior;
    }
    public void setLotacaoSuperior(boolean lotacaoSuperior) {
        this.lotacaoSuperior = lotacaoSuperior;
    }
    public String getNomeProjeto() {
        return nomeProjeto;
    }
    public void setNomeProjeto(String nomeProjeto) {
        this.nomeProjeto = nomeProjeto;
    }

    @Override
    public String toString() {
        return this.nomeProjeto;
    }
}
