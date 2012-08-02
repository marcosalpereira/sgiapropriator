package br.com.marcosoft.sgi.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class ApropriationFile {
    private final File inputFile;

    private List<TaskRecord> tasksRecords = new ArrayList<TaskRecord>();

    private final Config config = new Config();

    private final Collection<Projeto> projects = new ArrayList<Projeto>();

    private boolean captureProjects;

    public ApropriationFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public List<TaskRecord> getTasksRecords() {
        return tasksRecords;
    }

    public void setTasksRecords(List<TaskRecord> tasksRecords) {
        this.tasksRecords = tasksRecords;
    }

    public Config getConfig() {
        return config;
    }

    public class Config {
        private static final String APP_PROPERTIES_PREFIX = "sgi.";

        private final Properties properties = new Properties();

        public void setProperty(String key, String value) {
            if (key == null || key.trim().length() == 0)
                return;

            final String newKeyName = enforceNewKeyName(key);

            if (isApplicationProperty(newKeyName)) {
                properties.setProperty(newKeyName, value);
            } else {
                System.setProperty(newKeyName, value);
            }
        }

        private String enforceNewKeyName(String key) {
            if ("cpf".equals(key)) {
                return APP_PROPERTIES_PREFIX + "cpf";

            } else if ("version".equals(key)) {
                return APP_PROPERTIES_PREFIX + "macros.version";

            } else if ("firefoxProfile".equals(key)) {
                return APP_PROPERTIES_PREFIX + "browser.firefox.profile";
            }
            return key;
        }

        public String getPlanilhaDir() {
            final String parent = inputFile.getParent();
            if (parent == null) return ".";
            return parent;
        }

        private boolean isApplicationProperty(String key) {
            return key.startsWith(APP_PROPERTIES_PREFIX);
        }

        public String getMacrosVersion() {
            return properties.getProperty(APP_PROPERTIES_PREFIX + "macros.version");
        }

        public String getCpf() {
            return properties.getProperty(APP_PROPERTIES_PREFIX + "cpf");
        }

        public String getUrlSgi() {
            return properties.getProperty(APP_PROPERTIES_PREFIX + "url",
                "https://sgi.portalcorporativo.serpro/");
        }

        public String getBrowserType() {
            return properties.getProperty(APP_PROPERTIES_PREFIX + "browser.type",
                "firefox");
        }

        public String getFirefoxProfile() {
            final String firefoxProfile = properties.getProperty(APP_PROPERTIES_PREFIX
                + "browser.firefox.profile", "default");
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
            return properties.getProperty(APP_PROPERTIES_PREFIX + "defaultTipoHora",
                "Normal");
        }

        public String getDefaultInsumo() {
            return properties.getProperty(APP_PROPERTIES_PREFIX + "defaultInsumo",
                "Desenvolvimento e Manutenção de Software");
        }

        public String getDefaultTipoInsumo() {
            return properties.getProperty(APP_PROPERTIES_PREFIX + "defaultTipoInsumo",
                "Novo Sistema");
        }

        public String getNomeSubordinado() {
            return properties.getProperty(APP_PROPERTIES_PREFIX + "subordinado");
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

}
