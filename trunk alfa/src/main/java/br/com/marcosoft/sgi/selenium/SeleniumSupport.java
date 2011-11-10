package br.com.marcosoft.sgi.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;

import com.thoughtworks.selenium.Selenium;

/**
 * Selenium Support.
 */
public class SeleniumSupport {
    private static Selenium selenium;

    public static Selenium getSelenium() {
        return selenium;
    }

    /**
     * Inicializa o Selenium.
     */
    public static void initSelenium(String profile) {
        final String browserUrl = System.getProperty("sgi.url", "https://sgi.portalcorporativo.serpro/");

        final ProfilesIni allProfiles = new ProfilesIni();
        final FirefoxProfile firefoxProfile = allProfiles.getProfile(profile);
        final WebDriver driver = new FirefoxDriver(firefoxProfile);

//        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
//        final WebDriver driver = new ChromeDriver();

        selenium = new WebDriverBackedSelenium(driver, browserUrl);

    }

    public static void stopSelenium() {
        selenium.stop();
    }

    /**
     * Pausa a execucao durante o numero de milisegundos especificados.
     * @param millis numero de milisegundos
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            return;
        }
    }

}

