package br.com.marcosoft.sgi.po;

import java.util.Date;
import java.util.List;

import br.com.marcosoft.sgi.EsperarAjustesUsuario;
import br.com.marcosoft.sgi.model.Task;
import br.com.marcosoft.sgi.model.TaskDailySummary;
import br.com.marcosoft.sgi.model.TaskRecord;
import br.com.marcosoft.sgi.util.Util;

public class ApropriationPage extends PageObject {

    public ApropriationPage() {
        if (!getSelenium().isElementPresent("btnIncluir")) {
            throw new RuntimeException("Ops! N�o estou na p�gina de apropria��o!");
        }
    }

    /**
     * Apropriate task.
     * @param taskDailySummary task daily summary
     */
    public void apropriate(final TaskDailySummary taskDailySummary) {
        taskToPage(taskDailySummary.getData(), taskDailySummary.getFirstTask(), taskDailySummary.getSum(), false);
        getSelenium().click("btnIncluir");
        waitForPageToLoad();
    }

    private void taskToPage(final Date data, final Task task, final int qtdMinutos, boolean checkEditableFirst) {

        select(checkEditableFirst, "AP_dt_ap", Util.formatDate(data));

        if (task.isLotacaoSuperior()) {
            if (!getSelenium().isChecked("ck_Lot_Superior")) {
                clickAndWait("ck_Lot_Superior");
            }
        } else {
            if (getSelenium().isChecked("ck_Lot_Superior")) {
                clickAndWait("ck_Lot_Superior");
            }
        }

        if (task.getUgCliente().length() != 0) {
            select(checkEditableFirst, "AP_ug_cliente", task.getUgCliente());
            sleep(3000);
        }

        if (task.getProjeto().length() != 0) {
            select(checkEditableFirst, "AP_Servico", task.getProjeto());
            sleep(3000);
        }

        type("AP_horas", Util.formatMinutes(qtdMinutos));

        if (task.getMacro().length() != 0) {
            select(checkEditableFirst, "AP_MacroAtividade", task.getMacro());
        }

        if (task.getTipoHora().length() != 0) {
            select(checkEditableFirst, "AP_Tipo_hora", task.getTipoHora());
        }

        if (task.getInsumo().length() != 0) {
            select(checkEditableFirst, "AP_Insumo", task.getInsumo());
        }

        if (task.getTipoInsumo().length() != 0 && getSelenium().isEditable("AP_Tipo_Insumo")) {
            select(checkEditableFirst, "AP_Tipo_Insumo", task.getTipoInsumo());
        }

        type("AP_obs", task.getDescricao());
    }

    /**
     * Ajustar as informacoes da apropriacao task.t
     * @param tasks tasks
     */
    public void ajustarApropriacoes(final List<TaskRecord> tasks) {
        desabilitarBotoes();

        for (final TaskRecord taskRecord : tasks) {
            final Task task = taskRecord.getTask();
            if (task.isAjustarInformacoes()) {
                taskToPage(taskRecord.getData(), task, taskRecord.getDuracao(), true);
                esperarAjustesUsuario();
                atualizarAtividadeComInformacoesUsuario(task);
            }
        }

    }

    private void desabilitarBotoes() {
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

    private void esperarAjustesUsuario() throws CanceladoPeloUsuarioException {
        if (!EsperarAjustesUsuario.esperarAjustes()) {
            throw new CanceladoPeloUsuarioException();
        }
    }

    public void mostrarApropriacoesPeriodo() {
        getSelenium().click("Ck_Aprop_Periodo");
        waitForPageToLoad();
    }

}
