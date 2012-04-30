package br.com.marcosoft.sgi.po;

import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;

import br.com.marcosoft.sgi.EsperarAjustesUsuario;
import br.com.marcosoft.sgi.model.Task;
import br.com.marcosoft.sgi.model.TaskDailySummary;
import br.com.marcosoft.sgi.model.TaskRecord;
import br.com.marcosoft.sgi.util.Util;

public class ApropriationPage extends PageObject {

    //IDs dos elementos na pagina.
    private static final String AP_HORAS = "AP_horas";
    private static final String AP_DT_AP = "AP_dt_ap";
    private static final String AP_OBS = "AP_obs";
    private static final String AP_TIPO_INSUMO = "AP_Tipo_Insumo";
    private static final String AP_INSUMO = "AP_Insumo";
    private static final String AP_TIPO_HORA = "AP_Tipo_hora";
    private static final String AP_MACRO_ATIVIDADE = "AP_MacroAtividade";
    private static final String AP_SERVICO = "AP_Servico";
    private static final String AP_UG_CLIENTE = "AP_ug_cliente";
    private static final String CK_LOT_SUPERIOR = "ck_Lot_Superior";

    private static final String BTN_LIMPAR = "btnLimpar";
    private static final String BTN_INCLUIR = "btnIncluir";

    public ApropriationPage() {
        if (!getSelenium().isElementPresent(BTN_INCLUIR)) {
            throw new RuntimeException("Ops! Não estou na página de apropriação!");
        }
    }

    /**
     * Apropriate task.
     * @param taskDailySummary task daily summary
     * @param nomeSubordinado nome do subordinado
     */
    public void apropriate(final TaskDailySummary taskDailySummary, String nomeSubordinado) {
        final Date data = taskDailySummary.getData();
        final Task task = taskDailySummary.getFirstTask();
        final int qtdMinutos = taskDailySummary.getSum();

        if (StringUtils.isNotEmpty(nomeSubordinado)) {
            recoverableSelect(task, "Empregado", "AP_id_pessoa", nomeSubordinado);
        }

        recoverableSelect(task, "Data", AP_DT_AP, Util.formatDate(data));

        fillLotacaoSuperior(task);

        recoverableSelect(task, "UG Cliente", AP_UG_CLIENTE, task.getUgCliente());

        recoverableSelect(task, "Projeto/Serviço", AP_SERVICO, task.getProjeto());

        type(AP_HORAS, Util.formatMinutes(qtdMinutos));

        recoverableSelect(task, "Macroatividade", AP_MACRO_ATIVIDADE, task.getMacro());

        //recoverableSelect(task, "Tipo de Hora", AP_TIPO_HORA, task.getTipoHora());

        recoverableSelect(task, "Insumo", AP_INSUMO, task.getInsumo());

//        if (getSelenium().isEditable(AP_TIPO_INSUMO)) {
//            recoverableSelect(task, "Tipo de Insumo", AP_TIPO_INSUMO, task.getTipoInsumo());
//        }

        if (!getSelenium().getValue(AP_OBS).equals(task.getDescricao())) {
            type(AP_OBS, task.getDescricao());
        }

        getSelenium().click(BTN_INCLUIR);

        waitWindow(AP_DT_AP, "Esperando usuário clicar no ok");
    }

    private void recoverableSelect(Task task, String fieldName, String locator, String value) {
        if (value.length() == 0) {
            return;
        }
        try {
            select(locator, value);
        } catch (final NotSelectedException e) {
            final String message = String.format(
                "Não consegui selecionar %s [%s]]\n\nDeseja selecionar no SGI?",
                    fieldName, value);
            final int opt = JOptionPane.showConfirmDialog(null, message,
                "Erro selecionando campo", JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.OK_OPTION) {
                throw e;
            }
            esperarAjustesUsuario("Seleção manual" + fieldName);
            atualizarAtividadeComInformacoesUsuario(task);
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
        select(AP_DT_AP, Util.formatDate(data), true, 2);
        fillLotacaoSuperior(task);
        select(AP_UG_CLIENTE, task.getUgCliente(), true, 2);
        select(AP_SERVICO, task.getProjeto(), true, 2);
        type(AP_HORAS, Util.formatMinutes(qtdMinutos));
        select(AP_MACRO_ATIVIDADE, task.getMacro(), true, 2);
        //select(AP_TIPO_HORA, task.getTipoHora(), true, 2);
        select(AP_INSUMO, task.getInsumo(), true, 2);
//        if (getSelenium().isEditable(AP_TIPO_INSUMO)) {
//            select(AP_TIPO_INSUMO, task.getTipoInsumo(), true, 2);
//        }
        type(AP_OBS, task.getDescricao());
    }

    private void fillLotacaoSuperior(final Task task) {
        if (task.isLotacaoSuperior()) {
            if (!getSelenium().isChecked(CK_LOT_SUPERIOR)) {
                clickAndWait(CK_LOT_SUPERIOR);
            }
        } else {
            if (getSelenium().isChecked(CK_LOT_SUPERIOR)) {
                clickAndWait(CK_LOT_SUPERIOR);
            }
        }
    }

    private void atualizarAtividadeComInformacoesUsuario(final Task task) {
        task.setControlarMudancas(true);
        task.setLotacaoSuperior(getSelenium().isChecked(CK_LOT_SUPERIOR));
        task.setUgCliente(getSelenium().getSelectedLabel(AP_UG_CLIENTE));
        task.setProjeto(getSelenium().getSelectedLabel(AP_SERVICO));
        task.setMacro(getSelenium().getSelectedLabel(AP_MACRO_ATIVIDADE));
        //task.setTipoHora(getSelenium().getSelectedLabel(AP_TIPO_HORA));
        task.setInsumo(getSelenium().getSelectedLabel(AP_INSUMO));
//        task.setTipoInsumo(getSelenium().getSelectedLabel(AP_TIPO_INSUMO));
        task.setDescricao(getSelenium().getValue(AP_OBS));
        task.setControlarMudancas(false);
    }

    private void esperarAjustesUsuario(String contexto) throws CanceladoPeloUsuarioException {
        final DesabilitarBotoesIncluirCancelar desabilitarBotoesIncluirCancelar =
            new DesabilitarBotoesIncluirCancelar();

        desabilitarBotoesIncluirCancelar.start();
        if (!EsperarAjustesUsuario.esperarAjustes(contexto)) {
            desabilitarBotoesIncluirCancelar.finalizar();
            throw new CanceladoPeloUsuarioException();
        }
        desabilitarBotoesIncluirCancelar.finalizar();
    }

    public void mostrarApropriacoesPeriodo() {
        getSelenium().click("Ck_Aprop_Periodo");
        waitForPageToLoad();
    }


    private class DesabilitarBotoesIncluirCancelar extends Thread {
        private boolean stop = false;

        @Override
        public void run() {
            while (!stop) {
                desabilitarBotoesIncluirCancelar();
                PageObject.sleep(1000);
            }
        }

        public void finalizar() {
            stop = true;
            habilitarBotoesIncluirCancelar();
        }

    }

    private void desabilitarBotoesIncluirCancelar() {
        setEnabled(BTN_INCLUIR, false);
        setEnabled(BTN_LIMPAR, false);
    }

    private void habilitarBotoesIncluirCancelar() {
        setEnabled(BTN_INCLUIR, true);
        setEnabled(BTN_LIMPAR, true);
    }

}
