package br.com.marcosoft.sgi.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ApropriationFile {
    private List<TaskRecord> tasksRecords = new ArrayList<TaskRecord>();
    private final Config config = new Config();

    public List<TaskRecord> getTasksRecords() {
        return tasksRecords;
    }
    public void setTasksRecords(List<TaskRecord> tasksRecords) {
        this.tasksRecords = tasksRecords;
    }

    public Config getConfig() {
        return config;
    }

    public static class Config {
        private static final String PROPERTIES_PREFIX = "sgi.";
        private static final List<String> APP_PROPERTIES = Arrays.asList(
             PROPERTIES_PREFIX + "cpf"
            ,PROPERTIES_PREFIX + "url"
            ,PROPERTIES_PREFIX + "browser.type"
            ,PROPERTIES_PREFIX + "browser.firefox.profile"
        );

        private final Properties properties = new Properties();

        public void setProperty(String key, String value) {
            if (key == null || key.trim().length() == 0) return;

            final String newKeyName = enforceNewKeyName(key);

            if (isApplicationProperty(newKeyName)) {
                properties.setProperty(newKeyName, value);
            } else {
                System.setProperty(newKeyName, value);
            }
        }

        private String enforceNewKeyName(String key) {
            if ("cpf".equals(key)) return PROPERTIES_PREFIX + "cpf";
            if ("firefoxProfile".equals(key)) return PROPERTIES_PREFIX + "browser.firefox.profile";
            return key;
        }

        private boolean isApplicationProperty(String key) {
            return APP_PROPERTIES.contains(key);
        }

        public String getCpf() {
            return properties.getProperty(PROPERTIES_PREFIX + "cpf");
        }

        public String getUrlSgi() {
            return properties.getProperty(
                PROPERTIES_PREFIX + "url", "https://sgi.portalcorporativo.serpro/");
        }

        public String getBrowserType() {
            return properties.getProperty(PROPERTIES_PREFIX + "browser.type", "firefox");
        }

        public String getFirefoxProfile() {
            final String firefoxProfile = properties.getProperty(
                PROPERTIES_PREFIX + "browser.firefox.profile", "default");
            return checkCompatibilidadeFirefoxProfile(firefoxProfile);
        }

        private String checkCompatibilidadeFirefoxProfile(String firefoxProfile) {
            return substringAfterLast(firefoxProfile, File.separatorChar);
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


    }



}
