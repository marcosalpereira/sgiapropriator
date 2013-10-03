package br.com.marcosoft.sgi.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Projeto {
    private final String dados;
    private final Map<Integer, String> mapDados = new HashMap<Integer, String>();

    public Projeto(String dados) {
        this.dados = dados;
        final String[] dadosSplit = dados.split(";");
        mapear(dadosSplit);
    }

    private void mapear(final String[] dadosSplit) {
        mapDados.clear();
        for (int i = 0; i < dadosSplit.length; i++) {
            mapDados.put(i, dadosSplit[i]);
        }
    }
    
    public Projeto(String[] dadosSplit) {
        this.dados = StringUtils.join(dadosSplit, ";");
        mapear(dadosSplit);
    }
    
    protected String getDado(int i) {
        return mapDados.get(i);
    }
    
    protected void setDado(int i, String valor) {
        mapDados.put(i, valor);
    }

    public String getDados() {
        return dados;
    }

    @Override
    public String toString() {
        return this.dados;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Projeto) {
            final Projeto that = (Projeto) obj;
            return new EqualsBuilder().append(this.getDados(),
                that.getDados()).isEquals();
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(3231, 243871).append(this.getDados())
            .toHashCode();
    }

}
