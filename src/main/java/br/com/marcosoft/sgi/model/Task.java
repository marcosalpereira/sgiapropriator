package br.com.marcosoft.sgi.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.marcosoft.sgi.util.Util;

/**
 * An Task.
 */
public class Task {

    private Projeto projeto;

    private String macro;

    private String tipoHora;

    private String insumo;

    private String tipoInsumo;

    private String descricao;

    private String horaInicio;

    private String horaTermino;

    private String sistema;

    private boolean ajustarInformacoes;

    public boolean isAjustarInformacoes() {
        return this.ajustarInformacoes || isProjetoNaoInformado();
    }

    private boolean isProjetoNaoInformado() {
        return projeto == null || projeto.getDados().trim().length() == 0;
    }

    public void setAjustarInformacoes(final boolean ajustarInformacoes) {
        this.ajustarInformacoes = ajustarInformacoes;
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    /**
     * Numero da linha na planilha.
     */
    private String numeroLinha;

    private boolean controlarMudancas;

    private boolean ugClienteMudou;

    private boolean lotacaoSuperiorMudou;

    private boolean projetoMudou;

    private boolean macroMudou;

    private boolean tipoHoraMudou;

    private boolean insumoMudou;

    private boolean tipoInsumoMudou;

    private boolean descricaoMudou;

    public String getId() {
        return this.descricao + this.projeto + this.macro + this.tipoHora
            + this.insumo + this.tipoInsumo;
    }

    public String getUgCliente() {
        return ((ProjetoSgi) this.projeto).getUg();
    }

    public void setUgCliente(final String ugCliente) {
        final ProjetoSgi projetoSgi = (ProjetoSgi)this.projeto;
        this.ugClienteMudou = (this.controlarMudancas && !ugCliente.equals(projetoSgi.getUg()));
        projetoSgi.setUg(ugCliente);
    }

    public String getNomeProjeto() {
        return projeto.getNome();
    }

    public void setNomeProjeto(final String nomeProjeto) {
        this.projetoMudou = (this.controlarMudancas && !nomeProjeto.equals(projeto.getNome()));
        projeto.setNome(nomeProjeto);
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    public String getMacro() {
        return this.macro;
    }

    public void setMacro(final String macro) {
        this.macroMudou = (this.controlarMudancas && !macro.equals(this.macro));
        this.macro = macro;
    }

    public String getTipoHora() {
        return this.tipoHora;
    }

    public void setTipoHora(final String tipoHora) {
        this.tipoHoraMudou = (this.controlarMudancas && !tipoHora.equals(this.tipoHora));
        this.tipoHora = tipoHora;
    }

    public String getInsumo() {
        return this.insumo;
    }

    public void setInsumo(final String insumo) {
        this.insumoMudou = (this.controlarMudancas && !insumo.equals(this.insumo));
        this.insumo = insumo;
    }

    public String getTipoInsumo() {
        return this.tipoInsumo;
    }

    public void setTipoInsumo(final String tipoInsumo) {
        this.tipoInsumoMudou = (this.controlarMudancas && !tipoInsumo.equals(this.tipoInsumo));
        this.tipoInsumo = tipoInsumo;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(final String descricao) {
        this.descricaoMudou = (this.controlarMudancas && !descricao.equals(this.descricao));
        this.descricao = descricao;
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

    public boolean isInformacoesAjustadasUsuario() {
        return this.ugClienteMudou || this.lotacaoSuperiorMudou || this.projetoMudou
            || this.macroMudou || this.tipoHoraMudou || this.insumoMudou
            || this.tipoInsumoMudou || this.descricaoMudou;
    }

    public void setControlarMudancas(final boolean controlarMudancas) {
        this.controlarMudancas = controlarMudancas;
    }

    public boolean isUgClienteMudou() {
        return this.ugClienteMudou;
    }

    public boolean isLotacaoSuperiorMudou() {
        return this.lotacaoSuperiorMudou;
    }

    public boolean isProjetoMudou() {
        return this.projetoMudou;
    }

    public boolean isMacroMudou() {
        return this.macroMudou;
    }

    public boolean isTipoHoraMudou() {
        return this.tipoHoraMudou;
    }

    public boolean isInsumoMudou() {
        return this.insumoMudou;
    }

    public boolean isTipoInsumoMudou() {
        return this.tipoInsumoMudou;
    }

    public boolean isDescricaoMudou() {
        return this.descricaoMudou;
    }

    public String getTarefaAlm() {
        final ProjetoAlm projetoAlm = (ProjetoAlm)this.projeto;
        return projetoAlm.getNome();
    }

    public String getIdItemTrabalho() {
        final ProjetoAlm projetoAlm = (ProjetoAlm)this.projeto;
        return projetoAlm.getIdItemTrabalho();
    }

    public boolean isLotacaoSuperior() {
        return true;
    }

    public boolean isSistemaAlm() {
        return Util.isSistemaAlm(sistema);
    }


}
