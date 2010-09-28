package br.com.marcosoft.sgi;

import static br.com.marcosoft.sgi.ColunasPlanilha.COLUNA_NOME_PROJETO;

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

/**
 * Apropriar SGI.
 */
public class Apropriator {

    public static void main(final String[] args) throws Exception {
        final File inputFile = parseArgs(args);
        if (inputFile == null) {
            JOptionPane.showMessageDialog(null, "Erro acessando arquivo importação!");
        } else {
            new Apropriator().doItForMePlease(inputFile);
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
        verificarCompatibilidade(config.get("version"));

        final String firefoxProfile = config.get("firefoxProfile");

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

    private void verificarCompatibilidade(final String version) {
        Double.parseDouble(version);
        //getAppVersion()
        // TODO Auto-generated method stub
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
                for (final String numeroLinha : tds.getNumerosLinhas()) {
                    out.println(String.format("mcr|%s", numeroLinha));
                }
            }
        }

        //Alterar lista de projetos
        final String novosProjetos = selecionarProjetosEscolhidosUsuario(tasksSum);
        if (novosProjetos.length() != 0) {
            out.println(String.format("alp|%s", novosProjetos));
        }

        //Setar projetos nas atividades
        for (final TaskDailySummary tds : tasksSum) {
            if (tds.getTask().isInformacoesAjustadasUsuario()) {
                out.println(String.format("set|%s|%s|%s", COLUNA_NOME_PROJETO, tds.getTask().getNumeroLinha(), tds.getTask().getProjeto()));
            }
        }
        out.close();

    }

    private String selecionarProjetosEscolhidosUsuario(final List<TaskDailySummary> tasksSum) {
        String novosProjetos = "";
        for (final TaskDailySummary tds : tasksSum) {
            if (tds.getTask().isInformacoesAjustadasUsuario()) {
                if (novosProjetos.length() == 0) {
                    novosProjetos += aspas(tds.getTask().getProjeto());
                } else {
                    novosProjetos += ";" + aspas(tds.getTask().getProjeto());
                }
            }
        }
        return novosProjetos;
    }

    private String aspas(final String projeto) {
        return "\"" + projeto + "\"";
    }

    /**
     * @param tasksSum
     * @return
     */
    private List<TaskDailySummary> apropriate(final List<TaskRecord> tasks) {
        final ApropriationPage apropriationPage = irParaPaginaApropriacao();

        if (precisaEscolherProjeto(tasks)) {
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

    private boolean precisaEscolherProjeto(final List<TaskRecord> tasks) {
        for (final TaskRecord taskRecord : tasks) {
            if (taskRecord.getTask().isAjustarInformacoesApropriacao()) {
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
                tds.setTask(activity.getTask());
                tds.setSum(activity.getDuracao());
                ret.add(tds);
            } else {
                tds.setSum(tds.getSum() + activity.getDuracao());
            }
            tds.getNumerosLinhas().add(activity.getTask().getNumeroLinha());
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
            if (tds.getTask().equals(task) && tds.getData().equals(data)) {
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

            if (task.getInsumo().length() == 0) {
                task.setInsumo(defaultInsumo);
            }

            if (task.getTipoInsumo().length() == 0) {
                task.setTipoInsumo(defaultTipoInsumo);
            }
        }
    }

}
