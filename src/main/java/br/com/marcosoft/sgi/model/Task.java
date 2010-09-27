package br.com.marcosoft.sgi.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * An Task.
 */
public class Task {

    private String ugCliente;

    private boolean lotacaoSuperior;

    private String projeto;

    private String macro;

    private String tipoHora;

    private String insumo;

    private String tipoInsumo;

    private String descricao;
    
    private String horaInicio;
    
    private String horaTermino;

    /**
     * Numero da linha na planilha.
     */
    private String numeroLinha;

    private boolean projetoEscolhidoUsuario;

    public String getId() {
        return descricao + ugCliente + lotacaoSuperior + projeto + macro + tipoHora
            + insumo + tipoInsumo;
    }

    public String getUgCliente() {
        return ugCliente;
    }

    public void setUgCliente(String ugCliente) {
        this.ugCliente = ugCliente;
    }

    public boolean isLotacaoSuperior() {
        return lotacaoSuperior;
    }

    public void setLotacaoSuperior(boolean lotacaoSuperior) {
        this.lotacaoSuperior = lotacaoSuperior;
    }

    public String getProjeto() {
        return projeto;
    }

    public void setProjeto(String projeto) {
        this.projeto = projeto;
    }

    public String getMacro() {
        return macro;
    }

    public void setMacro(String macro) {
        this.macro = macro;
    }

    public String getTipoHora() {
        return tipoHora;
    }

    public void setTipoHora(String tipoHora) {
        this.tipoHora = tipoHora;
    }

    public String getInsumo() {
        return insumo;
    }

    public void setInsumo(String insumo) {
        this.insumo = insumo;
    }

    public String getTipoInsumo() {
        return tipoInsumo;
    }

    public void setTipoInsumo(String tipoInsumo) {
        this.tipoInsumo = tipoInsumo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String atividade) {
        this.descricao = atividade;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Task) {
            Task that = (Task) obj;
            return new EqualsBuilder()
                .append(this.getId(), that.getId())
                .isEquals();
        }

        return false;
    }

    /**{@inheritDoc}*/
    @Override
    public int hashCode() {
        return new HashCodeBuilder(1233, 13441).append(this.getId()).toHashCode();
    }

    @Override
    public String toString() {
        return this.descricao;
    }

    public void setNumeroLinha(String numeroLinha) {
        this.numeroLinha = numeroLinha;
    }
    
    public String getNumeroLinha() {
        return numeroLinha;
    }

    public boolean isProjetoVazio() {
        String projeto = getProjeto().trim();
        return projeto.length() == 0 || projeto.equals("Vou escolher no SGI");
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraTermino() {
        return horaTermino;
    }

    public void setHoraTermino(String horaTermino) {
        this.horaTermino = horaTermino;
    }

    public void setProjetoEscolhidoUsuario(boolean projetoModificado) {
        this.projetoEscolhidoUsuario = projetoModificado;        
    }
    
    public boolean isProjetoEscolhidoUsuario() {
        return projetoEscolhidoUsuario;
    }

    
}
