package br.com.marcosoft.sgi;


import static br.com.marcosoft.sgi.ColunasPlanilha.COL_REG_DESCRICAO;
import static br.com.marcosoft.sgi.ColunasPlanilha.COL_REG_INSUMO;
import static br.com.marcosoft.sgi.ColunasPlanilha.COL_REG_MACRO;
import static br.com.marcosoft.sgi.ColunasPlanilha.COL_REG_NOME_PROJETO;
import static br.com.marcosoft.sgi.ColunasPlanilha.COL_REG_TIPO_HORA;
import static br.com.marcosoft.sgi.ColunasPlanilha.COL_REG_TIPO_INSUMO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

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
import br.com.marcosoft.sgi.po.alm.Alm;
import br.com.marcosoft.sgi.po.alm.ApropriationPageAlm;
import br.com.marcosoft.sgi.po.alm.HomePageAlm;
import br.com.marcosoft.sgi.po.alm.LoginPageAlm;
import br.com.marcosoft.sgi.selenium.SeleniumSupport;
import br.com.marcosoft.sgi.util.ApplicationProperties;
import br.com.marcosoft.sgi.util.Cipher;
import br.com.marcosoft.sgi.util.URLUtils;
import br.com.marcosoft.sgi.util.Util;

/**
 * Apropriar SGI.
 */
public class Apropriator {

    private static final String CHAVE_SENHA_APP_PROPERTIES = "password";
    private static final String CHAVE_SENHA_ALM_APP_PROPERTIES = "alm.password";

    public static void main(final String[] args) {
        setLookAndFeel();
        final File inputFile = parseArgs(args);
        if (inputFile == null) {
            JOptionPane.showMessageDialog(null, "Erro acessando arquivo importação!");
        } else {
            final Apropriator apropriator = new Apropriator();
            try {
                apropriator.doItForMePlease(inputFile);
            } catch (final Throwable e) {
                JOptionPane.showMessageDialog(null, "Um erro inesperado ocorreu!\n" + e.getMessage());
                apropriator.gravarArquivoRetornoErro(e, inputFile);
                e.printStackTrace();
            }
        }
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private String getNewVersionMessage() {
        return "Uma nova versão está diponível em "
            + "http://code.google.com/p/sgiapropriator/downloads/list";
    }

    private boolean isNewVersion() {
        final String appVersion = getAppVersion();
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

    public void doItForMePlease(final File inputFile) throws ApropriationException {

        parseFile(inputFile);
        verificarCompatibilidade();

        if (apropriationFile.isCaptureProjects()) {
            captureProjects();
        } else {
            apropriate();
        }

    }

    private void parseFile(final File inputFile) throws ApropriationException {
        final ApropriationFileParser apropriationFileParser = new ApropriationFileParser(inputFile);
        try {
            apropriationFile = apropriationFileParser.parse();
        } catch (final IOException e) {
            throw new ApropriationException(e);
        }
    }

    private void iniciarSelenium(Config config, String browserUrl) {
        final WaitWindow waitWindow = new WaitWindow("Iniciando Selenium " + browserUrl);
        try {
            SeleniumSupport.initSelenium(config, browserUrl);
        } finally {
            waitWindow.dispose();
        }
    }

    /**
     * Verificar a compatibilidade desta implementacao com a versao do arquivo de integracao.
     * @param strVersion versao que esta no arquivo de integracao
     * @throws ApropriationException
     */
    private void verificarCompatibilidade() throws ApropriationException {
        final String strVersion = getMacrosVersion();
        if (strVersion == null) {
            return;
        }
        final double version = Double.parseDouble(strVersion);
        if (version < 0.4) {
            throw new ApropriationException("Não sei tratar arquivos na versão:" + strVersion);
        }
    }

    private void gravarArquivoRetornoErro(Throwable erro, File inputFile) {
        String exportFolder = inputFile.getParent();
        if (exportFolder == null) exportFolder = ".";

        final String fileName = exportFolder + File.separator + "sgi.ret";
        final PrintWriter out;
        try {
            out = new PrintWriter(fileName, "UTF-8");
        } catch (final IOException e) {
            JOptionPane.showMessageDialog(null, "Nao consegui gravar arquivo retorno!\n"
                + e.getMessage());
            return;
        }
        out.println("err|" + erro.getMessage());
        out.close();
    }

    private void gravarArquivoRetornoProjetos(Collection<Projeto> projetos) {
        final String exportFolder = this.apropriationFile.getConfig().getPlanilhaDir();

        final String fileName = exportFolder + File.separator + "sgi.ret";
        final PrintWriter out;
        try {
            out = new PrintWriter(fileName, "UTF-8");
        } catch (final IOException e) {
            JOptionPane.showMessageDialog(null, "Nao consegui gravar arquivo retorno!\n"
                + e.getMessage());
            return;
        }

        if (projetos != null && !projetos.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (final Projeto projeto : projetos) {
                if (sb.length() > 0) {
                    sb.append(";");
                }
                sb.append(
                    String.format("%s",
                        aspas(
                            projeto.getDados()
                        )
                    )
                );
            }
            out.print("prj|");
            out.println(sb.toString());
        }
        out.close();

    }

    private String aspas(String string) {
        return "\"" + string + "\"";
    }

    private void gravarArquivoRetornoApropriacao(final List<TaskDailySummary> tasksSum) {
        final String exportFolder = this.apropriationFile.getConfig().getPlanilhaDir();

        final String fileName = exportFolder + File.separator + "sgi.ret";
        PrintWriter out;
        try {
            out = new PrintWriter(fileName, "UTF-8");
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
                if (task.isTipoHoraMudou()) {
                    out.println(String.format("set|%s|%s|%s", COL_REG_TIPO_HORA, task.getNumeroLinha(), task.getTipoHora()));
                }
                if (task.isInsumoMudou()) {
                    out.println(String.format("alv|%s|%s", COL_REG_INSUMO ,task.getInsumo()));
                    out.println(String.format("set|%s|%s|%s", COL_REG_INSUMO, task.getNumeroLinha(), task.getInsumo()));
                }
                if (task.isMacroMudou()) {
                    out.println(String.format("set|%s|%s|%s", COL_REG_MACRO, task.getNumeroLinha(), task.getMacro()));
                }
                if (task.isProjetoMudou() || task.isUgClienteMudou()) {
                    out.println(String.format("alv|%s|%s", COL_REG_NOME_PROJETO ,task.getNomeProjeto()));
                    out.println(String.format("set|%s|%s|%s", COL_REG_NOME_PROJETO, task.getNumeroLinha(),
                        task.getUgCliente() + ";" + task.getNomeProjeto()));
                }
                if (task.isTipoInsumoMudou()) {
                    out.println(String.format("alv|%s|%s", COL_REG_TIPO_INSUMO ,task.getTipoInsumo()));
                    out.println(String.format("set|%s|%s|%s", COL_REG_TIPO_INSUMO, task.getNumeroLinha(), task.getTipoInsumo()));
                }
            }
        }

        out.println();

        out.close();

    }

    private void captureProjects() {
        final Config config = apropriationFile.getConfig();

        iniciarSelenium(config, config.getUrlSgi());

        final HomePage homePage = doLogin();
        final ApropriationPage apropriationPage =
            homePage.gotoApropriationPage(isApropriacaoSubordinado());

        final String title = "Apropriator v" + getAppVersion() + " - Capturar projetos";

        final CaptureProjectsWindow captureProjects = new CaptureProjectsWindow(title,
            this.apropriationFile.getProjects(), apropriationPage);

        final Collection<Projeto> projetos = captureProjects.getSelectedProjects();

        homePage.logout();

        gravarArquivoRetornoProjetos(projetos);

        SeleniumSupport.stopSelenium();
    }

    private void apropriate() {
        final List<TaskDailySummary> tasksSum = new ArrayList<TaskDailySummary>();
        if (registrosApropriacoesIntegros()) {
            tasksSum.addAll(apropriateSgi());
            tasksSum.addAll(apropriateAlm());
        }
        gravarArquivoRetornoApropriacao(tasksSum);
    }

    private boolean registrosApropriacoesIntegros() {
        final Config config = this.apropriationFile.getConfig();
        final int minimoMinutosApropriacaoDia = config.getMinimoMinutosApropriacaoDia();
        final int maximoMinutosApropriacaoDia = config.getMaximoMinutosApropriacaoDia();

        final List<TaskRecord> tasks = this.apropriationFile.getTasksRecords();
        final List<TaskDailySummary> tasksSum = sumAllTasksByDay(tasks);

        final StringBuilder sb = new StringBuilder();

        for (final TaskDailySummary taskDailySummary : tasksSum) {
            final int sum = taskDailySummary.getSum();
            if (sum < minimoMinutosApropriacaoDia
                || sum > maximoMinutosApropriacaoDia) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(
                    String.format(
                        "Na data %s o total apropriado é %d",
                            Util.DD_MM_YY_FORMAT.format(taskDailySummary.getData()),
                            sum));
            }
        }

        if (sb.length() > 0) {

            final String message = String.format(
                "As apropriações estão fora dos limites aceitáveis.\nVariáveis na aba de configuração da planilha\n\t%s=%d\n\t%s=%d\n%s",
                config.getChaveMinimoMinutosApropriacaoDia(),
                minimoMinutosApropriacaoDia,
                config.getChaveMaximoMinutosApropriacaoDia(),
                maximoMinutosApropriacaoDia, sb.toString());
            final JTextArea textArea = new JTextArea(10, 50);
            textArea.setText(message);
            textArea.setEditable(false);
            final JScrollPane scrollPane = new JScrollPane(textArea);

            final Object[] options = {"Confirma", "Cancela"};
            final int n = JOptionPane.showOptionDialog(null,
                scrollPane,
                "Confirme para continuar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
            return n == 0;

        }
        return true;
    }

    private List<TaskDailySummary> apropriateAlm() {
        final List<TaskRecord> tasks = this.apropriationFile.getAlmTasksRecords();
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        final Config config = apropriationFile.getConfig();
        iniciarSelenium(config, config.getUrlAlm());

        final HomePageAlm homePage = doAlmLogin();

        final List<TaskDailySummary> tasksSum = sumTasks(tasks);

        final String title = "Apropriator v" + getAppVersion() + " - Macros" + getMacrosVersion();
        final ProgressInfo progressInfo = new ProgressInfo(title);
        if (isNewVersion()) {
            progressInfo.setInfoMessage(getNewVersionMessage());
        }

        for (int i=1; i<=tasksSum.size();) {
            final TaskDailySummary tds = tasksSum.get(i-1);
            final String progresso = i + "/" + tasksSum.size();
            progressInfo.setInfo(progresso, tds);
            final ApropriationPageAlm apropriationPage = homePage.gotoApropriationPage(tds);
            try {
                apropriationPage.apropriate(tds);
                tds.setApropriado(true);
                progressInfo.setInfoMessage(null);
                i++;

            } catch (final RuntimeException e) {
                final OpcoesRecuperacaoAposErro opcao = stopAfterException(e);
                if (opcao == OpcoesRecuperacaoAposErro.TENTAR_NOVAMENTE) {
                    progressInfo.setInfoMessage("Tentando apropriar novamente mesma atividade!!!");
                    homePage.gotoApropriationPage(tds);

                } else if (opcao == OpcoesRecuperacaoAposErro.PROXIMA) {
                    homePage.gotoApropriationPage(tds);
                    i++;

                } else {
                    break;
                }
            }

        }

        homePage.logout();

        progressInfo.dispose();

        SeleniumSupport.stopSelenium();

        return tasksSum;
    }

    private List<TaskDailySummary> apropriateSgi() {
        final List<TaskRecord> tasks = this.apropriationFile.getSgiTasksRecords();
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        verifyDefaultsSgi(tasks);

        final Config config = apropriationFile.getConfig();
        iniciarSelenium(config, config.getUrlSgi());

        final HomePage homePage = doLogin();
        final ApropriationPage apropriationPage = irParaPaginaApropriacao(homePage);

        if (precisaAjustarInformacoesApropriacao(tasks)) {
            apropriationPage.ajustarApropriacoes(tasks);
        }

        final List<TaskDailySummary> tasksSum = sumTasks(tasks);

        final String title = "Apropriator v" + getAppVersion() + " - Macros" + getMacrosVersion();
        final ProgressInfo progressInfo = new ProgressInfo(title);
        if (isNewVersion()) {
            progressInfo.setInfoMessage(getNewVersionMessage());
        }

        for (int i=1; i<=tasksSum.size();) {
            try {
                final TaskDailySummary tds = tasksSum.get(i-1);
                final String progresso = i + "/" + tasksSum.size();
                progressInfo.setInfo(progresso, tds);
                apropriationPage.apropriate(tds, getNomeSubordinado());
                tds.setApropriado(true);
                progressInfo.setInfoMessage(null);
                i++;

            } catch (final ErroInesperado e) {
                progressInfo.setInfoMessage(
                    "Erro no SGI Detectado. Tentando apropriar novamente mesma atividade!!!");
                irParaPaginaApropriacao(homePage);

            } catch (final RuntimeException e) {
                final OpcoesRecuperacaoAposErro opcao = stopAfterException(e);
                if (opcao == OpcoesRecuperacaoAposErro.TENTAR_NOVAMENTE) {
                    progressInfo.setInfoMessage("Tentando apropriar novamente mesma atividade!!!");
                    irParaPaginaApropriacao(homePage);

                } else if (opcao == OpcoesRecuperacaoAposErro.PROXIMA) {
                    irParaPaginaApropriacao(homePage);
                    i++;

                } else {
                    break;
                }
            }

        }

        homePage.logout();

        progressInfo.dispose();

        SeleniumSupport.stopSelenium();

        return tasksSum;
    }

    private ApropriationPage irParaPaginaApropriacao(final HomePage homePage) {
        final ApropriationPage apropriationPage = homePage.gotoApropriationPage(isApropriacaoSubordinado());
        apropriationPage.mostrarApropriacoesPeriodo();
        return apropriationPage;
    }

    private enum OpcoesRecuperacaoAposErro {
        TENTAR_NOVAMENTE, PROXIMA, TERMINAR;

        @Override
        public String toString() {
            switch (this) {
            case TENTAR_NOVAMENTE:
                return "Tentar Novamente";
            case PROXIMA:
                return "Ir para a próxima";
            default:
                return "Terminar";
            }
        }
    }

    private OpcoesRecuperacaoAposErro stopAfterException(final RuntimeException e) {
        final String message = "O seguinte erro ocorreu:\n" + e.getMessage() + "!";
        final JTextArea textArea = new JTextArea(10, 60);
        textArea.setText(message);
        textArea.setEditable(false);
        final JScrollPane scrollPane = new JScrollPane(textArea);

        final Object[] options = {"Tentar Novamente",
                            "Ir para próxima",
                            "Terminar"};
        final int n = JOptionPane.showOptionDialog(null,
            scrollPane,
            "Erro na apropriação",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);

        return OpcoesRecuperacaoAposErro.values()[n];

    }

    private String getMacrosVersion() {
        return this.apropriationFile.getConfig().getMacrosVersion();
    }

    private String getNomeSubordinado() {
        return this.apropriationFile.getConfig().getNomeSubordinado();
    }

    private boolean isApropriacaoSubordinado() {
        return StringUtils.isNotEmpty(getNomeSubordinado());
    }

    private HomePageAlm doAlmLogin() {
        final Alm alm = new Alm();
        final LoginPageAlm loginPage = alm.gotoLoginPage();
        final String cpf = this.apropriationFile.getConfig().getCpf();
        final String pwd = readAlmSavedPassword();
        final HomePageAlm homePage = loginPage.login(cpf, pwd);
        writeAlmSavedPassword(pwd, homePage.getSenha());
        return homePage;
    }

    private HomePage doLogin() {
        final Sgi sgi = new Sgi();
        final LoginPage loginPage = sgi.gotoLoginPage();
        final String cpf = this.apropriationFile.getConfig().getCpf();
        final String pwd = readSavedPassword();
        final HomePage homePage = loginPage.login(cpf, pwd);
        writeSavedPassword(pwd, homePage.getSenha());
        return homePage;
    }

    private void writeSavedPassword(String pwd, String senhaLida) {
        if (senhaLida != null && !senhaLida.equals(pwd)) {
            this.applicantionProperties.setProperty(CHAVE_SENHA_APP_PROPERTIES, Cipher.cript(senhaLida));
        }
    }

    private void writeAlmSavedPassword(String pwd, String senhaLida) {
        if (senhaLida != null && !senhaLida.equals(pwd)) {
            this.applicantionProperties.setProperty(CHAVE_SENHA_ALM_APP_PROPERTIES,
                Cipher.cript(senhaLida));
        }
    }

    private String readAlmSavedPassword() {
        final String pwdCripto = this.applicantionProperties.getProperty(
            CHAVE_SENHA_ALM_APP_PROPERTIES);
        if (pwdCripto != null) {
            return Cipher.uncript(pwdCripto);
        }
        return null;
    }

    private String readSavedPassword() {
        final String pwdCripto = this.applicantionProperties.getProperty(CHAVE_SENHA_APP_PROPERTIES);
        if (pwdCripto != null) {
            return Cipher.uncript(pwdCripto);
        }

        final String pwd = this.applicantionProperties.getProperty("pwd");
        return pwd;
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

    private List<TaskDailySummary> sumAllTasksByDay(final List<TaskRecord> activities) {

        final List<TaskDailySummary> ret = new ArrayList<TaskDailySummary>();

        for (final TaskRecord activity : activities) {
            TaskDailySummary tds = searchData(ret, activity.getData());
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
    private TaskDailySummary searchData(final List<TaskDailySummary> tasksSummary, final Date data) {
        for (final TaskDailySummary tds : tasksSummary) {
            if (tds.getData().equals(data)) {
                return tds;
            }
        }
        return null;
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

    private void verifyDefaultsSgi(final List<TaskRecord> tasks) {
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
