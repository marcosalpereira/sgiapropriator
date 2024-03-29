package br.com.marcosoft.sgi.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.marcosoft.sgi.util.Util;

public class ApropriationFile {
    private final File inputFile;

    private List<TaskRecord> tasksRecords = new ArrayList<TaskRecord>();

    private final Config config = new Config();

    private final Collection<Projeto> projects = new ArrayList<Projeto>();

    private boolean captureProjects;

    public ApropriationFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public List<TaskRecord> getSgiTasksRecords() {
        final List<TaskRecord> tasksRecordsSistema = new ArrayList<TaskRecord>();
        for (final TaskRecord taskRecord : tasksRecords) {
            if (taskRecord.isSistemaSgi()) {
                tasksRecordsSistema.add(taskRecord);
            }
        }
        return tasksRecordsSistema;
    }

    public List<TaskRecord> getTasksRecords() {
        return tasksRecords;
    }

    public List<TaskRecord> getAlmTasksRecords() {
        final List<TaskRecord> tasksRecordsSistema = new ArrayList<TaskRecord>();
        for (final TaskRecord taskRecord : tasksRecords) {
            if (taskRecord.isSistemaAlm()) {
                tasksRecordsSistema.add(taskRecord);
            }
        }
        return tasksRecordsSistema;
    }

    public void setTasksRecords(List<TaskRecord> tasksRecords) {
        this.tasksRecords = tasksRecords;
    }

    public Config getConfig() {
        return config;
    }

    public class Config {
        private static final String SGI_BROWSER_FIREFOX_PROFILE = "sgi.browser.firefox.profile";

        private static final String SGI_BROWSER_TYPE = "sgi.browser.type";

        private static final String SGI_URL = "sgi.url";

        private static final String ALM_URL = "alm.url";

        private static final String SGI_SUBORDINADO = "sgi.subordinado";

        private static final String SGI_DEFAULT_TIPO_INSUMO = "sgi.defaultTipoInsumo";

        private static final String SGI_DEFAULT_INSUMO = "sgi.defaultInsumo";

        private static final String SGI_DEFAULT_TIPO_HORA = "sgi.defaultTipoHora";

        private static final String SGI_MACROS_VERSION = "sgi.macros.version";

        private static final String SGI_CPF = "sgi.cpf";

        public static final String SGI_VERIFICAR_FEZ_APROPRIACAO = "sgi.verificarFezApropriacao";

        public static final String SALVAR_SENHA = "salvar.senha";

        public void setProperty(String key, String value) {
            if (key == null || key.trim().length() == 0)
                return;

            final String newKeyName = enforceNewKeyName(key);

            System.setProperty(newKeyName, value);
        }

        private String enforceNewKeyName(String key) {
            if ("cpf".equals(key)) {
                return SGI_CPF;

            } else if ("version".equals(key)) {
                return SGI_MACROS_VERSION;

            } else if ("firefoxProfile".equals(key)) {
                return SGI_BROWSER_FIREFOX_PROFILE;
            }
            return key;
        }

        public String getPlanilhaDir() {
            final String parent = inputFile.getParent();
            if (parent == null)
                return ".";
            return parent;
        }

        public String getMacrosVersion() {
            return System.getProperty(SGI_MACROS_VERSION);
        }

        public String getCpf() {
            return System.getProperty(SGI_CPF);
        }

        public String getUrlSgi() {
            return System.getProperty(SGI_URL, "https://sgi.portalcorporativo.serpro/");
        }

        public String getUrlAlm() {
            return System.getProperty(ALM_URL, "https://alm.serpro/");
        }

        public String getBrowserType() {
            return System.getProperty(SGI_BROWSER_TYPE, "firefox");
        }

        public String getFirefoxProfile() {
            final String firefoxProfile = System.getProperty(SGI_BROWSER_FIREFOX_PROFILE,
                "default");
            return checkCompatibilidadeFirefoxProfile(firefoxProfile);
        }

        private String checkCompatibilidadeFirefoxProfile(String firefoxProfile) {
            return substringAfterLast(firefoxProfile, File.separatorChar);
        }

        /**
         * Retorna a substring da posicao do ultimo separador encontrado ate o
         * final. Ex: substringAfterLast("/a/b/c", "/") --> "c" Ex:
         * substringAfterLast("a.b", ".") --> "b" Ex: substringAfterLast("a",
         * ".") --> "a"
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

        public String getDefaultTipoHora() {
            return System.getProperty(SGI_DEFAULT_TIPO_HORA, "Normal");
        }

        public String getDefaultInsumo() {
            return System.getProperty(SGI_DEFAULT_INSUMO,
                "Desenvolvimento e Manutenção de Software");
        }

        public String getDefaultTipoInsumo() {
            return System.getProperty(SGI_DEFAULT_TIPO_INSUMO, "Novo Sistema");
        }

        public String getNomeSubordinado() {
            return System.getProperty(SGI_SUBORDINADO);
        }

        public int getMinimoMinutosApropriacaoDia() {
            final String minutos =
                System.getProperty(getChaveMinimoMinutosApropriacaoDia());
            return Util.parseInt(minutos, 480 - 80);
        }

        public String getChaveMinimoMinutosApropriacaoDia() {
            return "minutosApropriacaoDia.minimo";
        }

        public String getChaveMaximoMinutosApropriacaoDia() {
            return "minutosApropriacaoDia.maximo";
        }

        public int getMaximoMinutosApropriacaoDia() {
            final String minutos =
                System.getProperty(getChaveMaximoMinutosApropriacaoDia());
            return Util.parseInt(minutos, 480);
        }

    }

    public Collection<Projeto> getProjects() {
        return projects;
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setCaptureProjects(boolean captureProjects) {
        this.captureProjects = captureProjects;
    }

    public boolean isCaptureProjects() {
        return captureProjects;
    }

    public void adicionarTasksRecord(TaskRecord taskRecord) {
        tasksRecords.add(taskRecord);
    }

}
