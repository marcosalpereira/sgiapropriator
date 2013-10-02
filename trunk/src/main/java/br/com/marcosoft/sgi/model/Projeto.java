package br.com.marcosoft.sgi.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Projeto {
    private String ug;

    private boolean lotacaoSuperior;

    private String nomeProjeto;

    public Projeto(String nomeProjeto) {
        this.nomeProjeto = nomeProjeto;
    }

    public Projeto() {
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

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Projeto) {
            final Projeto that = (Projeto) obj;
            return new EqualsBuilder().append(this.getNomeProjeto(),
                that.getNomeProjeto()).isEquals();
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(3231, 243871).append(this.getNomeProjeto())
            .toHashCode();
    }

}
