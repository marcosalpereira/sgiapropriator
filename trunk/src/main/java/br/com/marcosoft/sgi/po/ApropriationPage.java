package br.com.marcosoft.sgi.po;

import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;

import br.com.marcosoft.sgi.EsperarAjustesUsuario;
import br.com.marcosoft.sgi.SelectOptionWindow;
import br.com.marcosoft.sgi.model.ApropriationFile.Config;
import br.com.marcosoft.sgi.model.Task;
import br.com.marcosoft.sgi.model.TaskDailySummary;
import br.com.marcosoft.sgi.model.TaskRecord;
import br.com.marcosoft.sgi.util.Util;

public class ApropriationPage extends PageObject {

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

    public ApropriationPage() {
        if (!getSelenium().isElementPresent(BTN_INCLUIR)) {
            throw new RuntimeException("Ops! Não estou na página de apropriação!");
        }
    }

    public Collection<String> capturarProjetos(String ugCliente, boolean lotacaoSuperior) {
        try {
            select(AP_UG_CLIENTE, ugCliente);
        } catch (final NotSelectedException e) {
            JOptionPane.showMessageDialog(null, "UG Cliente inválida");
            return Collections.emptyList();
        }
        fillLotacaoSuperior(lotacaoSuperior);
        sleep(1000);
        return selecionarProjetos();
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
            recoverableSelect(taskDailySummary, "Empregado", "AP_id_pessoa", nomeSubordinado);
        }

        recoverableSelect(taskDailySummary, "Data", AP_DT_AP, Util.formatDate(data));

        fillLotacaoSuperior(task.isLotacaoSuperior());

        recoverableSelect(taskDailySummary, "UG Cliente", AP_UG_CLIENTE, task.getUgCliente());

        selectProject(taskDailySummary);

        final String minutes = Util.formatMinutes(qtdMinutos);
        type(AP_HORAS, minutes);

        recoverableSelect(taskDailySummary, "Macroatividade", AP_MACRO_ATIVIDADE, task.getMacro());

        recoverableSelect(taskDailySummary, "Insumo", AP_INSUMO, task.getInsumo());

        if (!getSelenium().getValue(AP_OBS).equals(task.getDescricao())) {
            type(AP_OBS, task.getDescricao());
        }

        getSelenium().click(BTN_INCLUIR);

        waitWindow(AP_DT_AP, "Esperando usuário clicar no ok");

        if (!apropriou(data, minutes, task)) {
            final String message = "Não detectei que a apropriação foi realizada. Deseja ajustar e tentar novamente?" ;
            final int reposta = showConfirmDialog(
                null, message, "Erro na apropriação", YES_NO_OPTION, QUESTION_MESSAGE);
            if (reposta == OK_OPTION) {
                esperarAjustesUsuario("Ajustar apropriação");
                atualizarAtividadeComInformacoesUsuario(taskDailySummary);
                apropriate(taskDailySummary, nomeSubordinado);
            } else {
                throw new RuntimeException("Erro na apropriação da atividade");
            }
        }
    }

    private boolean apropriou(Date data, String minutes, Task task) {
        if ("sim".equals(System.getProperty(Config.SGI_NAO_VERIFICAR_APROPRIACAO))) {
            return true;
        }
        final List<List<String>> table = getTable("AP_listagemPessoaTarefa");
        for (final List<String> linha : table) {
            if (linhaContemApropriacaoTarefa(linha, data, minutes, task)) {
                return true;
            }
        }
        return false;
    }

    private boolean linhaContemApropriacaoTarefa(List<String> linha, Date data, String minutes, Task task) {
        final String dataEsperada = Util.DD_MM_YYYY_FORMAT.format(data);
        final String projeto = task.getProjeto()
            .replaceAll("\\W", "").toLowerCase();

        final String dataPagina = linha.get(0);
        final String minutosPagina = linha.get(1);
        final String ugPagina = linha.get(2);
        final String projetoMacroPagina = linha.get(3);
        final String projetoPagina = separarProjetoDaMacroAtividade(projetoMacroPagina)
            .replaceAll("\\W", "").toLowerCase();

        return dataEsperada.equals(dataPagina)
            && minutes.equals(minutosPagina)
            && task.getUgCliente().equals(ugPagina)
            && projeto.startsWith(projetoPagina);
    }

    private String separarProjetoDaMacroAtividade(String projetoMacro) {
        final int posBarra = projetoMacro.lastIndexOf('/');
        if (posBarra == -1) return projetoMacro;
        return projetoMacro.substring(0, posBarra - 1);
    }

    private void selectProject(TaskDailySummary taskDailySummary) {
        final Task task = taskDailySummary.getFirstTask();
        try {
            select(AP_SERVICO, task.getProjeto());
        } catch (final NotSelectedException e) {
            final String projetoSimilar = selecionarProjetoSimilar(task.getProjeto());
            if (projetoSimilar != null) {
                atualizarAtividadeComOpcaoSelecionada(taskDailySummary, AP_SERVICO, projetoSimilar);
                recoverableSelect(taskDailySummary, "Projeto/Serviço", AP_SERVICO, projetoSimilar);
            }
        }

    }

    private String selecionarProjetoSimilar(String projetoOriginal) {
        final Collection<String> selectOptions = selecionarProjetos();
        for (final String option : selectOptions) {
            if (Util.isSimilar(projetoOriginal, option)) {
                final String message = String.format(
                      "Nome projeto/serviço parece ter mudado de \n'%s'\npara\n'%s'\nAproprio com esse projeto/serviço novo?",
                        projetoOriginal, option);
                final int resposta = JOptionPane.showConfirmDialog(
                    null, message, "Ajuste nome projeto similar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (resposta == JOptionPane.OK_OPTION) {
                    return option;
                }
            }
        }
        return null;
    }

    private Collection<String> selecionarProjetos() {
        final String[] selectOptions = getSelenium().getSelectOptions(AP_SERVICO);

        final List<String> projetos = new ArrayList<String>();
        for (final String option : selectOptions) {
            if (SELECIONE_UMA_OPCAO.equals(option)) continue;
            projetos.add(
                option.replaceAll("\\(S\\)  \\- SUPDE", "(S) - SUPDE")
            );
        }
        return projetos;
    }

    private void recoverableSelect(TaskDailySummary tds, String fieldName, String locator, String value) {
        if (value.length() == 0) {
            return;
        }
        try {
            select(locator, value);
        } catch (final NotSelectedException e) {
            final Collection<String> opcoes = new ArrayList<String>();
            final String ajustarNoSgi = "[Ajustar atividade no SGI]";
            opcoes.add(ajustarNoSgi);
            final String[] selectOptions = getSelenium().getSelectOptions(locator);
            opcoes.addAll(Arrays.asList(selectOptions));
            opcoes.remove(SELECIONE_UMA_OPCAO);

            final String message = String.format(
                "Não consegui selecionar '%s'. Selecione uma das opções possíveis?", fieldName);
            final SelectOptionWindow selectOptionWindow = new SelectOptionWindow(message, opcoes);
            final String selectedOption = selectOptionWindow.getSelectedOption();
            if (selectedOption == null) {
                throw new CanceladoPeloUsuarioException();
            }
            if (ajustarNoSgi.equals(selectedOption)) {
                esperarAjustesUsuario("Seleção manual" + fieldName);
                atualizarAtividadeComInformacoesUsuario(tds);
            } else {
                atualizarAtividadeComOpcaoSelecionada(tds, locator, selectedOption);
                recoverableSelect(tds, fieldName, locator, selectedOption);
            }
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
        fillLotacaoSuperior(task.isLotacaoSuperior());
        select(AP_UG_CLIENTE, task.getUgCliente(), true, 2);
        select(AP_SERVICO, task.getProjeto(), true, 2);
        type(AP_HORAS, Util.formatMinutes(qtdMinutos));
        select(AP_MACRO_ATIVIDADE, task.getMacro(), true, 2);
        select(AP_INSUMO, task.getInsumo(), true, 2);
        type(AP_OBS, task.getDescricao());
    }

    private void fillLotacaoSuperior(boolean lotacaoSuperior) {
        if (lotacaoSuperior) {
            if (!getSelenium().isChecked(CK_LOT_SUPERIOR)) {
                clickAndWait(CK_LOT_SUPERIOR);
            }
        } else {
            if (getSelenium().isChecked(CK_LOT_SUPERIOR)) {
                clickAndWait(CK_LOT_SUPERIOR);
            }
        }
    }

    private void atualizarAtividadeComOpcaoSelecionada(TaskDailySummary tds, String locator, String novoValor) {
        for (final Task task : tds.getTasks()) {
            task.setControlarMudancas(true);

            if (AP_UG_CLIENTE.equals(locator)) {
                task.setUgCliente(novoValor);

            } else if (AP_MACRO_ATIVIDADE.equals(locator)) {
                task.setMacro(novoValor);

            } else if (AP_SERVICO.equals(locator)) {
                task.setProjeto(novoValor);

            } else if (AP_INSUMO.equals(locator)) {
                task.setInsumo(novoValor);

            }
            task.setControlarMudancas(false);
        }
    }

    private void atualizarAtividadeComInformacoesUsuario(final TaskDailySummary tds) {
        for (final Task task : tds.getTasks()) {
            atualizarAtividadeComInformacoesUsuario(task);
        }
    }

    private void atualizarAtividadeComInformacoesUsuario(final Task task) {
        task.setControlarMudancas(true);
        task.setLotacaoSuperior(getSelenium().isChecked(CK_LOT_SUPERIOR));
        task.setUgCliente(getSelenium().getSelectedLabel(AP_UG_CLIENTE));
        task.setProjeto(getSelenium().getSelectedLabel(AP_SERVICO));
        task.setMacro(getSelenium().getSelectedLabel(AP_MACRO_ATIVIDADE));
        task.setInsumo(getSelenium().getSelectedLabel(AP_INSUMO));
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
