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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;

import br.com.marcosoft.sgi.model.ApropriationFile;
import br.com.marcosoft.sgi.model.ApropriationFile.Config;
import br.com.marcosoft.sgi.model.Projeto;
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
        if (isNewVersion(appVersion)) {
            final String text = "Uma nova versão está diponível em "
                + "http://code.google.com/p/sgiapropriator/downloads/list";
            TopMostMessage.message(text);
        }
    }

    private boolean isNewVersion(final String appVersion) {
        final String downloadsListPage =
            URLUtils.downloadFile("http://code.google.com/p/sgiapropriator/downloads/list");
        final String latestVersion = getLatestVersion(downloadsListPage);
        return latestVersion != null && latestVersion.compareTo(appVersion) > 0;
    }

    private String getLatestVersion(String downloadsListPage) {
        if (downloadsListPage == null) {
            return null;
        }
        final String TARGET_HREF_PREFIX = "href=\"//sgiapropriator.googlecode.com/files/";
        final int TARGET_HREF_PREFIX_LENGTH = TARGET_HREF_PREFIX.length();
        final Pattern versionFilePattern = Pattern.compile("^version-(\\d+\\.\\d+)\\.zip$");

        int fromIndex = 0;
        String latest = "";
        for(;;) {
            final int idx = downloadsListPage.indexOf(TARGET_HREF_PREFIX, fromIndex);
            if (idx == -1) {
                break;
            }
            final int beginIndex = idx + TARGET_HREF_PREFIX_LENGTH;
            final int endIndex = downloadsListPage.indexOf("\"", beginIndex);
            final String file = downloadsListPage.substring(beginIndex, endIndex);
            final Matcher matcher = versionFilePattern.matcher(file);
            if (matcher.matches()) {
                final String version = matcher.group(1);
                if (version.compareTo(latest) > 0) {
                    latest = version;
                }
            }
            fromIndex = idx + 1;
        }
        if (latest.isEmpty()) {
            return null;
        }
        return latest;
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
        apropriationFile = apropriationFileParser.parse();

        if (!verificarCompatibilidade()) {
            return;
        }

        iniciarSelenium(apropriationFile.getConfig());

        try {
            if (isToCaptureProjects()) {
                captureProjects();
            } else {
                apropriate();
            }
        } catch (final RuntimeException e) {
            JOptionPane.showMessageDialog(null, "Um erro inesperado ocorreu!\n" + e.getMessage());
            e.printStackTrace();
        }

        SeleniumSupport.stopSelenium();

    }

    private void iniciarSelenium(Config config) {
        final WaitWindow waitWindow = new WaitWindow("Iniciando Selenium");
        SeleniumSupport.initSelenium(config);
        waitWindow.dispose();
    }

    /**
     * Verificar a compatibilidade desta implementacao com a versao do arquivo de integracao.
     * @param strVersion versao que esta no arquivo de integracao
     * @return
     */
    private boolean verificarCompatibilidade() {
        final String strVersion = System.getProperty("version");
        if (strVersion == null) {
            return true;
        }
        final double version = Double.parseDouble(strVersion);
        if (version < 0.4) {
            JOptionPane.showMessageDialog(null, "Não sei tratar arquivos na versão:" + strVersion);
            return false;
        }
        return true;
    }

    private void gravarArquivoRetornoApropriacao(final List<TaskDailySummary> tasksSum) {
        final String exportFolder = this.apropriationFile.getInputFile().getParent();

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

    private boolean isToCaptureProjects() {
        // TODO Auto-generated method stub
        return false;
    }

    private void captureProjects() {
        final HomePage homePage = doLogin();
        final ApropriationPage apropriationPage =
            homePage.gotoApropriationPage(isApropriacaoSubordinado());

        final String title = "Apropriator v" + getAppVersion();

        final CaptureProjectsWindow captureProjects = new CaptureProjectsWindow(title,
            this.apropriationFile.getProjects().values(), apropriationPage);

        final Collection<Projeto> projetos = captureProjects.getSelectedProjects();

        homePage.logout();

        gravarArquivoRetornoProjetos(projetos);

    }


    private void gravarArquivoRetornoProjetos(Collection<Projeto> projetos) {
        // TODO Auto-generated method stub
    }

    private void apropriate() {
        final List<TaskRecord> tasks = this.apropriationFile.getTasksRecords();
        verifyDefaults(tasks);

        final HomePage homePage = doLogin();
        final ApropriationPage apropriationPage = homePage.gotoApropriationPage(isApropriacaoSubordinado());
        apropriationPage.mostrarApropriacoesPeriodo();

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
                apropriationPage.apropriate(tds, getNomeSubordinado());
                tds.setApropriado(true);

            } catch (final RuntimeException e) {
                if (stopAfterException(e)) {
                    break;
                }
            }
        }
        homePage.logout();

        progressInfo.dispose();

        gravarArquivoRetornoApropriacao(tasksSum);
    }

    private String getNomeSubordinado() {
        return this.apropriationFile.getConfig().getNomeSubordinado();
    }

    private boolean isApropriacaoSubordinado() {
        return StringUtils.isNotEmpty(getNomeSubordinado());
    }

    private boolean stopAfterException(final RuntimeException e) {
        final String message = "O seguinte erro ocorreu:" + e.getMessage() + "\n\nContinua?";
        final int opt = JOptionPane.showConfirmDialog(null, message,
            "Erro na apropriação", JOptionPane.YES_NO_OPTION);
        return opt != JOptionPane.OK_OPTION;
    }

    private HomePage doLogin() {
        final Sgi sgi = new Sgi();
        final LoginPage loginPage = sgi.gotoLoginPage();
        final String cpf = this.apropriationFile.getConfig().getCpf();
        final String pwd = this.applicantionProperties.getProperty("pwd");
        final HomePage homePage = loginPage.login(cpf, pwd);
        return homePage;
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
        final Config config = this.apropriationFile.getConfig();
        final String defaultTipoHora = config.getDefaultTipoHora();
        final String defaultInsumo = config.getDefaultInsumo();
        final String defaultTipoInsumo = config.getDefaultTipoInsumo();

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
