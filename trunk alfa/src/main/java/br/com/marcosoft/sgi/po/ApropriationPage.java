package br.com.marcosoft.sgi.po;

import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import br.com.marcosoft.sgi.EsperarAjustesUsuario;
import br.com.marcosoft.sgi.model.Task;
import br.com.marcosoft.sgi.model.TaskDailySummary;
import br.com.marcosoft.sgi.model.TaskRecord;
import br.com.marcosoft.sgi.util.Util;

public class ApropriationPage extends PageObject {

    public ApropriationPage() {
        if (!getSelenium().isElementPresent("btnIncluir")) {
            throw new RuntimeException("Ops! Não estou na página de apropriação!");
        }
    }

    /**
     * Apropriate task.
     * @param taskDailySummary task daily summary
     */
    public void apropriate(final TaskDailySummary taskDailySummary) {
        final Date data = taskDailySummary.getData();
        final Task task = taskDailySummary.getFirstTask();
        final int qtdMinutos = taskDailySummary.getSum();

        fillDate(data);
        fillLotacaoSuperior(task);
        fillUgCliente(task);
        safeFillProject(task);
        fillHoras(qtdMinutos);
        fillMacro(task);
        fillTipoHora(task);
        fillInsumo(task);
        fillTipoInsumo(task);
        fillObservacao(task);

        getSelenium().click("btnIncluir");

        waitForPageToLoad();
    }

    private void safeFillProject(final Task task) {
        try {
            fillProjeto(task);
        } catch (final NotSelectedException e) {
            final String message = "Não consegui selecionar o projeto ["
                    + task.getProjeto() +  "]\n\nDeseja selecionar o projeto no SGI?";
            final int opt = JOptionPane.showConfirmDialog(null, message,
                "Erro selecionar projeto", JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.OK_OPTION) {
                throw e;
            }
            esperarAjustesUsuario("nome projeto");
            atualizarAtividadeComInformacoesUsuario(task);
        }
    }

    /**
     * Ajustar as informacoes da apropriacao task.t
     * @param tasks tasks
     */
    public void ajustarApropriacoes(final List<TaskRecord> tasks) {
        int qtdAjustes = getQuantidadesAjustes(tasks);
        int progresso = 0;
        for (final TaskRecord taskRecord : tasks) {
            final Task task = taskRecord.getTask();
            if (task.isAjustarInformacoes()) {
                ajustarApropriacoes(taskRecord.getData(), task, taskRecord.getDuracao());
                esperarAjustesUsuario(++progresso + "/" + qtdAjustes);
                atualizarAtividadeComInformacoesUsuario(task);
            }
        }
    }

    private int getQuantidadesAjustes(final List<TaskRecord> tasks) {
        int qtd = 0;
        for (final TaskRecord taskRecord : tasks) {
            final Task task = taskRecord.getTask();
            if (task.isAjustarInformacoes()) {
                qtd++;
            }
        }
        return qtd;
    }

    private void ajustarApropriacoes(final Date data, final Task task, final int qtdMinutos) {
        setIgnoreSelectionErrors(true);
        try {
            fillDate(data);
            fillLotacaoSuperior(task);
            fillUgCliente(task);
            safeFillProject(task);
            fillHoras(qtdMinutos);
            fillMacro(task);
            fillTipoHora(task);
            fillInsumo(task);
            fillTipoInsumo(task);
            fillObservacao(task);
        } finally {
            setIgnoreSelectionErrors(false);
        }

    }

    private void fillObservacao(final Task task) {
        type("AP_obs", task.getDescricao());
    }

    private void fillTipoInsumo(final Task task) {
        if (task.getTipoInsumo().length() != 0 && getSelenium().isEditable("AP_Tipo_Insumo")) {
            select("AP_Tipo_Insumo", task.getTipoInsumo());
        }
    }

    private void fillInsumo(final Task task) {
        if (task.getInsumo().length() != 0) {
            select("AP_Insumo", task.getInsumo());
        }
    }

    private void fillTipoHora(final Task task) {
        if (task.getTipoHora().length() != 0) {
            select("AP_Tipo_hora", task.getTipoHora());
        }
    }

    private void fillMacro(final Task task) {
        if (task.getMacro().length() != 0) {
            select("AP_MacroAtividade", task.getMacro());
        }
    }

    private void fillHoras(final int qtdMinutos) {
        type("AP_horas", Util.formatMinutes(qtdMinutos));
    }

    private void fillProjeto(final Task task) {
        if (task.getProjeto().length() != 0) {
            select("AP_Servico", task.getProjeto());
        }
    }

    private void fillUgCliente(final Task task) {
        if (task.getUgCliente().length() != 0) {
            select("AP_ug_cliente", task.getUgCliente());
            //sleep(3000);
        }
    }

    private void fillLotacaoSuperior(final Task task) {
        if (task.isLotacaoSuperior()) {
            if (!getSelenium().isChecked("ck_Lot_Superior")) {
                clickAndWait("ck_Lot_Superior");
            }
        } else {
            if (getSelenium().isChecked("ck_Lot_Superior")) {
                clickAndWait("ck_Lot_Superior");
            }
        }
    }

    private void fillDate(final Date data) {
        select("AP_dt_ap", Util.formatDate(data));
    }

    private void desabilitarBotoesIncluirCancelar() {
        desabilitarBotao("btnIncluir");
        desabilitarBotao("btnLimpar");
    }

    private void desabilitarBotao(String id) {
        final String script = new StringBuilder()
            .append(String.format("var element = selenium.browserbot.findElement(\"id=%s\");", id))
            .append("element.disabled = true")
            .toString();
        getSelenium().getEval(script);
    }

    private void atualizarAtividadeComInformacoesUsuario(final Task task) {
        task.setControlarMudancas(true);
        task.setLotacaoSuperior(getSelenium().isChecked("ck_Lot_Superior"));
        task.setUgCliente(getSelenium().getSelectedLabel("AP_ug_cliente"));
        task.setProjeto(getSelenium().getSelectedLabel("AP_Servico"));
        task.setMacro(getSelenium().getSelectedLabel("AP_MacroAtividade"));
        task.setTipoHora(getSelenium().getSelectedLabel("AP_Tipo_hora"));
        task.setInsumo(getSelenium().getSelectedLabel("AP_Insumo"));
        task.setTipoInsumo(getSelenium().getSelectedLabel("AP_Tipo_Insumo"));
        task.setDescricao(getSelenium().getValue("AP_obs"));
        task.setControlarMudancas(false);
    }

    private void esperarAjustesUsuario(String progressoAjuste) throws CanceladoPeloUsuarioException {
        desabilitarBotoesIncluirCancelar();
        if (!EsperarAjustesUsuario.esperarAjustes(progressoAjuste)) {
            throw new CanceladoPeloUsuarioException();
        }
    }

    public void mostrarApropriacoesPeriodo() {
        getSelenium().click("Ck_Aprop_Periodo");
        waitForPageToLoad();
    }

}
