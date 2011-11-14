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

        recoverableSelect(task, "Data", "AP_dt_ap", Util.formatDate(data));

        fillLotacaoSuperior(task);

        recoverableSelect(task, "UG Cliente", "AP_ug_cliente", task.getUgCliente());

        recoverableSelect(task, "Projeto/Serviço", "AP_Servico", task.getProjeto());

        type("AP_horas", Util.formatMinutes(qtdMinutos));

        recoverableSelect(task, "Macroatividade", "AP_MacroAtividade", task.getMacro());

        recoverableSelect(task, "Tipo de Hora", "AP_Tipo_hora", task.getTipoHora());

        recoverableSelect(task, "Insumo", "AP_Insumo", task.getInsumo());

        if (getSelenium().isEditable("AP_Tipo_Insumo")) {
            recoverableSelect(task, "Tipo de Insumo", "AP_Tipo_Insumo", task.getTipoInsumo());
        }

        type("AP_obs", task.getDescricao());

        getSelenium().click("btnIncluir");

        waitForPageToLoad();
    }

    private void recoverableSelect(Task task, String fieldName, String locator, String value) {
        if (value.length() == 0) {
            return;
        }
        try {
            select(locator, value);
        } catch (final NotSelectedException e) {
            final String message = String.format(
                "Não consegui selecionar %s [%s]]\n\nDeseja selecionar o projeto no SGI?",
                    fieldName, value);
            final int opt = JOptionPane.showConfirmDialog(null, message,
                "Erro selecionando campo", JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.OK_OPTION) {
                throw e;
            }
            esperarAjustesUsuario("Seleção manual" + fieldName);
            atualizarAtividadeComInformacoesUsuario(task, fieldName);
        }
    }

    /**
     * Ajustar as informacoes da apropriacao task.t
     * @param tasks tasks
     */
    public void ajustarApropriacoes(final List<TaskRecord> tasks) {
        final int qtdAjustes = getQuantidadesAjustes(tasks);
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

    private void ajustarApropriacoes(final Date data, final Task task,
        final int qtdMinutos) {
        setIgnoreSelectionErrors(true);
        try {
            select("AP_dt_ap", Util.formatDate(data));
            fillLotacaoSuperior(task);
            select("AP_ug_cliente", task.getUgCliente());
            select("AP_Servico", task.getProjeto());
            type("AP_horas", Util.formatMinutes(qtdMinutos));
            select("AP_MacroAtividade", task.getMacro());
            select("AP_Tipo_hora", task.getTipoHora());
            select("AP_Insumo", task.getInsumo());
            if (getSelenium().isEditable("AP_Tipo_Insumo")) {
                select("AP_Tipo_Insumo", task.getTipoInsumo());
            }
            type("AP_obs", task.getDescricao());
        } finally {
            setIgnoreSelectionErrors(false);
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

    private void atualizarAtividadeComInformacoesUsuario(final Task task, String locator) {
        task.setControlarMudancas(true);

        if (locator.equals("ck_Lot_Superior")) {
            task.setLotacaoSuperior(getSelenium().isChecked("ck_Lot_Superior"));
        }
        if (locator.equals("AP_ug_cliente")) {
            task.setUgCliente(getSelenium().getSelectedLabel("AP_ug_cliente"));
        }
        if (locator.equals("AP_Servico")) {
            task.setProjeto(getSelenium().getSelectedLabel("AP_Servico"));
        }
        if (locator.equals("AP_MacroAtividade")) {
            task.setMacro(getSelenium().getSelectedLabel("AP_MacroAtividade"));
        }
        if (locator.equals("AP_Tipo_hora")) {
            task.setTipoHora(getSelenium().getSelectedLabel("AP_Tipo_hora"));
        }
        if (locator.equals("AP_Insumo")) {
            task.setInsumo(getSelenium().getSelectedLabel("AP_Insumo"));
        }
        if (locator.equals("AP_Tipo_Insumo")) {
            task.setTipoInsumo(getSelenium().getSelectedLabel("AP_Tipo_Insumo"));
        }
        if (locator.equals("AP_obs")) {
            task.setDescricao(getSelenium().getValue("AP_obs"));
        }

        task.setControlarMudancas(false);
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

    private void esperarAjustesUsuario(String contexto) throws CanceladoPeloUsuarioException {
        desabilitarBotoesIncluirCancelar();
        if (!EsperarAjustesUsuario.esperarAjustes(contexto)) {
            throw new CanceladoPeloUsuarioException();
        }
    }

    public void mostrarApropriacoesPeriodo() {
        getSelenium().click("Ck_Aprop_Periodo");
        waitForPageToLoad();
    }

}
