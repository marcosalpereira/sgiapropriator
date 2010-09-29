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
            throw new RuntimeException("Ops! Não estou na página de apropriação!");
        }
    }

    /**
     * Apropriate task.
     * @param taskDailySummary task daily summary
     */
    public void apropriate(final TaskDailySummary taskDailySummary) {
        taskToPage(taskDailySummary.getData(), taskDailySummary.getFirstTask(), taskDailySummary.getSum());
        getSelenium().click("btnIncluir");
        waitForPageToLoad();
    }

    private void taskToPage(final Date data, final Task task, final int qtdMinutos) {

        select("AP_dt_ap", Util.formatDate(data));

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
            select("AP_ug_cliente", task.getUgCliente());
            sleep(3000);
        }

        if (task.getProjeto().length() != 0) {
            select("AP_Servico", task.getProjeto());
            sleep(3000);
        }

        type("AP_horas", Util.formatMinutes(qtdMinutos));

        if (task.getMacro().length() != 0) {
            select("AP_MacroAtividade", task.getMacro());
        }

        if (task.getTipoHora().length() != 0) {
            select("AP_Tipo_hora", task.getTipoHora());
        }

        if (task.getInsumo().length() != 0) {
            select("AP_Insumo", task.getInsumo());
        }

        if (task.getTipoInsumo().length() != 0 && getSelenium().isEditable("AP_Tipo_Insumo")) {
            select("AP_Tipo_Insumo", task.getTipoInsumo());
        }

        type("AP_obs", task.getDescricao());
    }

    /**
     * Ajustar as informacoes da apropriacao task.t
     * @param tasks tasks
     */
    public void ajustarApropriacoes(final List<TaskRecord> tasks) {
        for (final TaskRecord taskRecord : tasks) {
            final Task task = taskRecord.getTask();
            if (task.isAjustarInformacoes()) {
                taskToPage(taskRecord.getData(), task, taskRecord.getDuracao());
                esperarAjustesUsuario();
                atualizarAtividadeComInformacoesUsuario(task);
            }
        }

    }

    private void atualizarAtividadeComInformacoesUsuario(final Task task) {
        task.setControlarMudancas(true);
        task.setLotacaoSuperior(getSelenium().isChecked("ck_Lot_Superior"));
        task.setUgCliente(getSelenium().getText("AP_ug_cliente"));
        task.setProjeto(getSelenium().getText("AP_Servico"));
        task.setMacro(getSelenium().getText("AP_MacroAtividade"));
        task.setTipoHora(getSelenium().getText("AP_Tipo_hora"));
        task.setInsumo(getSelenium().getText("AP_Insumo"));
        task.setTipoInsumo(getSelenium().getText("AP_Tipo_Insumo"));
        task.setDescricao(getSelenium().getText("AP_obs"));
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
