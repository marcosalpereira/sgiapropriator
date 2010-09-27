package br.com.marcosoft.sgi.po;

import java.util.List;

import br.com.marcosoft.sgi.InputProjectDialog;
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
        final Task task = taskDailySummary.getTask();

        select("AP_dt_ap", Util.formatDate(taskDailySummary.getData()));
        if (taskDailySummary.getTask().isLotacaoSuperior()) {
            if (!getSelenium().isChecked("ck_Lot_Superior")) {
                clickAndWait("ck_Lot_Superior");
            }
        } else {
            if (getSelenium().isChecked("ck_Lot_Superior")) {
                clickAndWait("ck_Lot_Superior");
            }
        }

        select("AP_ug_cliente", task.getUgCliente());

        select("AP_Servico", task.getProjeto());
        sleep(3000);

        type("AP_horas", Util.formatMinutes(taskDailySummary.getSum()));
        if (task.getMacro().length() != 0) {
            select("AP_MacroAtividade", task.getMacro());
        }
        select("AP_Tipo_hora", task.getTipoHora());

        select("AP_Insumo", task.getInsumo());
        if (getSelenium().isEditable("AP_Tipo_Insumo")) {
            select("AP_Tipo_Insumo", task.getTipoInsumo());
        }
        type("AP_obs", task.getDescricao());

        getSelenium().click("btnIncluir");
        waitForPageToLoad();

    }

    /**
     * Apropriate task.
     * @param tasks task daily summary
     */
    public void escolherProjetos(final List<TaskRecord> tasks) {

        for (final TaskRecord taskRecord : tasks) {
            final Task task = taskRecord.getTask();
            if (!task.isProjetoVazio()) {
                continue;
            }

            if (task.isLotacaoSuperior()) {
                if (!getSelenium().isChecked("ck_Lot_Superior")) {
                    clickAndWait("ck_Lot_Superior");
                }
            } else {
                if (getSelenium().isChecked("ck_Lot_Superior")) {
                    clickAndWait("ck_Lot_Superior");
                }
            }

            select("AP_ug_cliente", task.getUgCliente());
            sleep(3000);

            task.setProjeto(escolherProjeto(taskRecord));
            task.setProjetoEscolhidoUsuario(true);

            select("AP_Servico", task.getProjeto());
            sleep(3000);
        }

    }

    private String escolherProjeto(final TaskRecord taskRecord) throws CanceladoPeloUsuarioException {
        final String[] projetosPagina = getSelenium().getSelectOptions("AP_Servico");
        final String[] projetos = new String[projetosPagina.length - 1];
        System.arraycopy(projetosPagina, 1, projetos, 0, projetos.length);
        final boolean selecionarProjeto = InputProjectDialog.selecionarProjeto(taskRecord);
        if (!selecionarProjeto) {
            throw new CanceladoPeloUsuarioException();
        }
        return null;
    }

    public void mostrarApropriacoesPeriodo() {
        getSelenium().click("Ck_Aprop_Periodo");
        waitForPageToLoad();
    }

}
