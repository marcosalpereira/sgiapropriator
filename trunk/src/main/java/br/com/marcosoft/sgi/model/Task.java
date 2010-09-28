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

    private boolean ajustarInformacoes;

    public boolean isAjustarInformacoes() {
        return this.ajustarInformacoes;
    }

    public void setAjustarInformacoes(final boolean ajustarInformacoes) {
        this.ajustarInformacoes = ajustarInformacoes;
    }

    /**
     * Numero da linha na planilha.
     */
    private String numeroLinha;

    private boolean informacoesAjustadasUsuario;

    public String getId() {
        return this.descricao + this.ugCliente + this.lotacaoSuperior + this.projeto + this.macro + this.tipoHora
            + this.insumo + this.tipoInsumo;
    }

    public String getUgCliente() {
        return this.ugCliente;
    }

    public void setUgCliente(final String ugCliente) {
        this.ugCliente = ugCliente;
    }

    public boolean isLotacaoSuperior() {
        return this.lotacaoSuperior;
    }

    public void setLotacaoSuperior(final boolean lotacaoSuperior) {
        this.lotacaoSuperior = lotacaoSuperior;
    }

    public String getProjeto() {
        return this.projeto;
    }

    public void setProjeto(final String projeto) {
        this.projeto = projeto;
    }

    public String getMacro() {
        return this.macro;
    }

    public void setMacro(final String macro) {
        this.macro = macro;
    }

    public String getTipoHora() {
        return this.tipoHora;
    }

    public void setTipoHora(final String tipoHora) {
        this.tipoHora = tipoHora;
    }

    public String getInsumo() {
        return this.insumo;
    }

    public void setInsumo(final String insumo) {
        this.insumo = insumo;
    }

    public String getTipoInsumo() {
        return this.tipoInsumo;
    }

    public void setTipoInsumo(final String tipoInsumo) {
        this.tipoInsumo = tipoInsumo;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(final String atividade) {
        this.descricao = atividade;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Task) {
            final Task that = (Task) obj;
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

    public void setNumeroLinha(final String numeroLinha) {
        this.numeroLinha = numeroLinha;
    }

    public String getNumeroLinha() {
        return this.numeroLinha;
    }

    public boolean isAjustarInformacoesApropriacao() {
        final String projeto = getProjeto().trim();
        return projeto.length() == 0 || projeto.equals("Vou escolher no SGI");
    }

    public String getHoraInicio() {
        return this.horaInicio;
    }

    public void setHoraInicio(final String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraTermino() {
        return this.horaTermino;
    }

    public void setHoraTermino(final String horaTermino) {
        this.horaTermino = horaTermino;
    }

    public void setInformacoesAjustadasUsuario(final boolean informacoesAjustadasUsuario) {
        this.informacoesAjustadasUsuario = informacoesAjustadasUsuario;
    }

    public boolean isInformacoesAjustadasUsuario() {
        return this.informacoesAjustadasUsuario;
    }


}
