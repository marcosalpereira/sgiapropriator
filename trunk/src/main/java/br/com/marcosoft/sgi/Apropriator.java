package br.com.marcosoft.sgi;


import static br.com.marcosoft.sgi.ColunasPlanilha.COL_REG_DESCRICAO;
import static br.com.marcosoft.sgi.ColunasPlanilha.COL_REG_INSUMO;
import static br.com.marcosoft.sgi.ColunasPlanilha.COL_REG_LOTACAO_SUPERIOR;
import static br.com.marcosoft.sgi.ColunasPlanilha.COL_REG_MACRO;
import static br.com.marcosoft.sgi.ColunasPlanilha.COL_REG_NOME_PROJETO;
import static br.com.marcosoft.sgi.ColunasPlanilha.COL_REG_TIPO_HORA;
import static br.com.marcosoft.sgi.ColunasPlanilha.COL_REG_TIPO_INSUMO;
import static br.com.marcosoft.sgi.ColunasPlanilha.COL_REG_UG_CLIENTE;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;

import br.com.marcosoft.sgi.model.ApropriationFile;
import br.com.marcosoft.sgi.model.Task;
import br.com.marcosoft.sgi.model.TaskDailySummary;
import br.com.marcosoft.sgi.model.TaskRecord;
import br.com.marcosoft.sgi.po.ApropriationPage;
import br.com.marcosoft.sgi.po.HomePage;
import br.com.marcosoft.sgi.po.LoginPage;
import br.com.marcosoft.sgi.po.Sgi;
import br.com.marcosoft.sgi.selenium.SeleniumSupport;
import br.com.marcosoft.sgi.util.ApplicationProperties;
import br.com.marcosoft.sgi.util.URLUtils;

/**
 * Apropriar SGI.
 */
public class Apropriator {

    public static void main(final String[] args) throws Exception {
        final File inputFile = parseArgs(args);
        if (inputFile == null) {
            JOptionPane.showMessageDialog(null, "Erro acessando arquivo importação!");
        } else {
            final Apropriator apropriator = new Apropriator();
            apropriator.checkForNewVersion();
            apropriator.doItForMePlease(inputFile);
        }
    }

    private void checkForNewVersion() {
        final String appVersion = getAppVersion();
        final String latestVersion =
            URLUtils.downloadFile("http://sgiapropriator.googlecode.com/files/latestVersion.txt");
        if (latestVersion != null && !latestVersion.equals(appVersion)) {
            JOptionPane.showMessageDialog(null, "Versão " + latestVersion
                + " está diponível para download");
        }
    }

    private static File parseArgs(final String[] args) {
        if (args.length > 0) {
            final File ret = new File(args[0]);
            if (ret.exists()) return ret;
        }
        return null;
    }

    private ApropriationFile apropriationFile;

    private final ApplicationProperties applicantionProperties = new ApplicationProperties(
        "sgiApropriator");

    public void doItForMePlease(final File inputFile) throws IOException {
        final ApropriationFileParser apropriationFileParser = new ApropriationFileParser(inputFile);
        this.apropriationFile = apropriationFileParser.parse();

        final Map<String, String> config = this.apropriationFile.getConfig();

        if (!verificarCompatibilidade(config.get("version"))) {
            JOptionPane.showMessageDialog(null, "Não sei tratar arquivos na versão:" + config.get("version"));
            return;
        }

        final String firefoxProfile = getFirefoxProfile(config);

        SeleniumSupport.initSelenium(firefoxProfile);

        try {
            final List<TaskRecord> tasks = this.apropriationFile.getTasksRecords();
            verifyDefaults(tasks);
            final List<TaskDailySummary> tasksSum = apropriate(tasks);
            gravarArquivoRetorno(inputFile.getParent(), tasksSum);
        } catch (final RuntimeException e) {
            JOptionPane.showMessageDialog(null, "Um erro inesperado ocorreu!\n" + e.getMessage());
            e.printStackTrace();
        }

        SeleniumSupport.stopSelenium();

    }

    private String getFirefoxProfile(final Map<String, String> config) {
        String firefoxProfile = config.get("firefoxProfile");
        firefoxProfile = substringAfterLast(firefoxProfile, File.separatorChar);
        return substringAfterLast(firefoxProfile, '.');
    }

    /**
     * Retorna a substring da posicao do ultimo separador encontrado ate o final.
     * Ex: substringAfterLast("/a/b/c", "/") --> "c"
     * Ex: substringAfterLast("a.b", ".") --> "b"
     * Ex: substringAfterLast("a", ".") --> "a"
     * @param string string
     * @param separator separator
     * @return a substring
     */
    private String substringAfterLast(String string, char separator) {
        if (string == null) {
            return null;
        }
        final int lastPos = string.lastIndexOf(separator);
        if (lastPos == -1) {
            return string;
        }
        return string.substring(lastPos + 1);
    }

    /**
     * Verificar a compatibilidade desta implementacao com a versao do arquivo de integracao.
     * @param strVersion versao que esta no arquivo de integracao
     * @return
     */
    private boolean verificarCompatibilidade(final String strVersion) {
        if (strVersion == null) {
            return true;
        }
        final double version = Double.parseDouble(strVersion);
        return (version >= 0.4);
    }

    private void gravarArquivoRetorno(final String exportFolder, final List<TaskDailySummary> tasksSum) {
        final String fileName = exportFolder + File.separator + "sgi.ret";
        PrintWriter out;
        try {
            out = new PrintWriter(new FileWriter(fileName));
        } catch (final IOException e) {
            JOptionPane.showMessageDialog(null, "Nao consegui gravar arquivo retorno!\n"
                + e.getMessage());
            return;
        }

        //Marcar como registrado
        for (final TaskDailySummary tds : tasksSum) {
            if (tds.isApropriado()) {
                for (final Task task : tds.getTasks()) {
                    out.println(String.format("mcr|%s", task.getNumeroLinha()));
                }
            }
        }

        //Atualizar lista de validacoes/set text
        for (final TaskDailySummary tds : tasksSum) {
            for (final Task task : tds.getTasks()) {
                if (task.isDescricaoMudou()) {
                    out.println(String.format("set|%s|%s|%s", COL_REG_DESCRICAO, task.getNumeroLinha(), task.getDescricao()));
                }
                if (task.isLotacaoSuperior()) {
                    out.println(String.format("set|%s|%s|%s", COL_REG_LOTACAO_SUPERIOR, task.getNumeroLinha(), task.isLotacaoSuperior() ? "Sim" : "Não"));
                }
                if (task.isTipoHoraMudou()) {
                    out.println(String.format("set|%s|%s|%s", COL_REG_TIPO_HORA, task.getNumeroLinha(), task.getTipoHora()));
                }
                if (task.isInsumoMudou()) {
                    out.println(String.format("alv|%s|%s", COL_REG_INSUMO ,task.getInsumo()));
                    out.println(String.format("set|%s|%s|%s", COL_REG_INSUMO, task.getNumeroLinha(), task.getInsumo()));
                }
                if (task.isMacroMudou()) {
                    //out.println(String.format("alv|%s|%s", COL_REG_MACRO ,task.getMacro()));
                    out.println(String.format("set|%s|%s|%s", COL_REG_MACRO, task.getNumeroLinha(), task.getMacro()));
                }
                if (task.isProjetoMudou()) {
                    out.println(String.format("alv|%s|%s", COL_REG_NOME_PROJETO ,task.getProjeto()));
                    out.println(String.format("set|%s|%s|%s", COL_REG_NOME_PROJETO, task.getNumeroLinha(), task.getProjeto()));
                }
                if (task.isTipoInsumoMudou()) {
                    out.println(String.format("alv|%s|%s", COL_REG_TIPO_INSUMO ,task.getTipoInsumo()));
                    out.println(String.format("set|%s|%s|%s", COL_REG_TIPO_INSUMO, task.getNumeroLinha(), task.getTipoInsumo()));
                }
                if (task.isUgClienteMudou()) {
                    out.println(String.format("alv|%s|%s", COL_REG_UG_CLIENTE ,task.getUgCliente()));
                    out.println(String.format("set|%s|%s|%s", COL_REG_UG_CLIENTE, task.getNumeroLinha(), task.getUgCliente()));
                }
            }
        }

        out.close();

    }

    /**
     * @param tasksSum
     * @return
     */
    private List<TaskDailySummary> apropriate(final List<TaskRecord> tasks) {
        final ApropriationPage apropriationPage = irParaPaginaApropriacao();

        if (precisaAjustarInformacoesApropriacao(tasks)) {
            apropriationPage.ajustarApropriacoes(tasks);
        }

        final List<TaskDailySummary> tasksSum = sumTasks(tasks);

        final String title = "Apropriator v" + getAppVersion();
        final ProgressInfo progressInfo = new ProgressInfo(title);

        int i = 0;

        for (final TaskDailySummary tds : tasksSum) {
            try {
                final String progresso = (++i) + "/" + tasksSum.size();
                progressInfo.setInfo(progresso, tds);
                apropriationPage.apropriate(tds);
                tds.setApropriado(true);

            } catch (final RuntimeException e) {
                final String message = "O seguinte erro ocorreu:" + e.getMessage() + "\n\nContinua?";
                final int opt = JOptionPane.showConfirmDialog(null, message,
                    "Erro na apropriação", JOptionPane.YES_NO_OPTION);
                if (opt != JOptionPane.OK_OPTION) {
                    break;
                }
            }
        }

        progressInfo.dispose();

        return tasksSum;

    }

    private ApropriationPage irParaPaginaApropriacao() {
        final Sgi sgi = new Sgi();

        final LoginPage loginPage = sgi.gotoLoginPage();
        final String cpf = this.apropriationFile.getConfig().get("cpf");
        final String pwd = this.applicantionProperties.getProperty("pwd");

        final HomePage homePage = loginPage.login(cpf, pwd);

        final ApropriationPage apropriationPage = homePage.gotoApropriationPage();
        apropriationPage.mostrarApropriacoesPeriodo();
        return apropriationPage;
    }

    private boolean precisaAjustarInformacoesApropriacao(final List<TaskRecord> tasks) {
        for (final TaskRecord taskRecord : tasks) {
            if (taskRecord.getTask().isAjustarInformacoes()) {
                return true;
            }
        }
        return false;
    }

    private String getAppVersion() {
        final String ret = "?";
        final InputStream stream = this.getClass().getClassLoader().getResourceAsStream(
            "META-INF/MANIFEST.MF");
        if (stream != null) {
            final Properties prop = new Properties();
            try {
                prop.load(stream);
                return prop.getProperty("version");
            } catch (final IOException e) {
            }
        }
        return ret;
    }

    private List<TaskDailySummary> sumTasks(final List<TaskRecord> activities) {

        final List<TaskDailySummary> ret = new ArrayList<TaskDailySummary>();

        for (final TaskRecord activity : activities) {
            TaskDailySummary tds = searchTaskAndData(ret, activity.getTask(), activity.getData());
            if (tds == null) {
                tds = new TaskDailySummary();
                tds.setData(activity.getData());
                tds.setSum(activity.getDuracao());
                ret.add(tds);
            } else {
                tds.setSum(tds.getSum() + activity.getDuracao());
            }
            tds.getTasks().add(activity.getTask());
        }

        return ret;

    }

    /**
     * Search tasks summary for the task and data.
     * @param tasksSummary tasks summary
     * @param task task
     * @param data data
     * @return
     */
    private TaskDailySummary searchTaskAndData(final List<TaskDailySummary> tasksSummary, final Task task,
        final Date data) {
        for (final TaskDailySummary tds : tasksSummary) {
            if (task.equals(tds.getFirstTask()) && tds.getData().equals(data)) {
                return tds;
            }
        }
        return null;
    }

    private void verifyDefaults(final List<TaskRecord> tasks) {
        String defaultTipoHora = this.apropriationFile.getConfig().get("defaultTipoHora");
        if (defaultTipoHora == null)
            defaultTipoHora = "Normal";

        String defaultInsumo = this.apropriationFile.getConfig().get("defaultInsumo");
        if (defaultInsumo == null)
            defaultInsumo = "Análise de Sistemas";

        String defaultTipoInsumo = this.apropriationFile.getConfig().get("defaultTipoInsumo");
        if (defaultTipoInsumo == null)
            defaultTipoInsumo = "Novo Sistema";

        for (final TaskRecord tr : tasks) {
            final Task task = tr.getTask();

            if (task.getTipoHora().length() == 0) {
                task.setTipoHora(defaultTipoHora);
            }

            if (!task.isAjustarInformacoes()) {
                if (task.getInsumo().length() == 0) {
                    task.setInsumo(defaultInsumo);
                }

                if (task.getTipoInsumo().length() == 0) {
                    task.setTipoInsumo(defaultTipoInsumo);
                }
            }
        }
    }

}
