package br.com.marcosoft.sgi.po.alm;

import java.util.Date;

import br.com.marcosoft.sgi.model.Task;
import br.com.marcosoft.sgi.model.TaskDailySummary;
import br.com.marcosoft.sgi.po.PageObject;

public class ApropriationPageAlm extends PageObject {

    public static final String SELECIONE_UMA_OPCAO = "--- Selecione uma opção ---";
    //IDs dos elementos na pagina.
    private static final String AP_HORAS = "AP_horas";
    private static final String AP_DT_AP = "AP_dt_ap";
    private static final String AP_OBS = "AP_obs";
    @SuppressWarnings("unused")
    private static final String AP_TIPO_INSUMO = "AP_Tipo_Insumo";
    private static final String AP_INSUMO = "AP_Insumo";
    @SuppressWarnings("unused")
    private static final String AP_TIPO_HORA = "AP_Tipo_hora";
    private static final String AP_MACRO_ATIVIDADE = "AP_MacroAtividade";
    private static final String AP_SERVICO = "AP_Servico";
    private static final String AP_UG_CLIENTE = "AP_ug_cliente";
    private static final String CK_LOT_SUPERIOR = "ck_Lot_Superior";

    private static final String BTN_LIMPAR = "btnLimpar";
    private static final String BTN_INCLUIR = "btnIncluir";

    public ApropriationPageAlm() {
        if (!getSelenium().isElementPresent(BTN_INCLUIR)) {
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



        click(BTN_INCLUIR);


    }

}
