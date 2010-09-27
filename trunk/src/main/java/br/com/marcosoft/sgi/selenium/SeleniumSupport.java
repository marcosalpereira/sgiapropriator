package br.com.marcosoft.sgi.selenium;

import java.io.File;

import org.seleniuminspector.SeleniumFactory;
import org.seleniuminspector.SeleniumHolder;
import org.seleniuminspector.SeleniumWithServerAutostartFactory;


import com.thoughtworks.selenium.Selenium;

/**
 * Selenium Support.
 */
public class SeleniumSupport {

    /**
     * Inicializa o Selenium.
     */
    public static void initSelenium(String profile) {
        final SeleniumHolder seleniumHolder = SeleniumHolder.getInstance();

        final String browserUrl = "https://sgi.portalcorporativo.serpro/";
        final SeleniumFactory factory;
        File profileFile = checkProfile(profile);

        if (profileFile != null) {
            factory = new MySeleniumFactory(9999, "*chrome", browserUrl, profileFile);
        } else {
            factory = new SeleniumWithServerAutostartFactory(9999, "*chrome", browserUrl);
        }
        seleniumHolder.setSeleniumFactory(factory);
        seleniumHolder.getSelenium().start();
        seleniumHolder.getSelenium().windowMaximize();
    }

    private static File checkProfile(String profile) {
        if (profile != null && profile.length() != 0) {
            File ret = null;
            ret = new File(profile);
            if (ret.exists()) {
                return ret;
            }
        }
        return null;
    }

    public static void stopSelenium() {
        Selenium selenium = SeleniumHolder.getInstance().getSelenium();
        selenium.stop();
        selenium.shutDownSeleniumServer();
    }

    /**
     * Pausa a execucao durante o numero de milisegundos especificados.
     * @param millis numero de milisegundos
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            return;
        }
    }

}

