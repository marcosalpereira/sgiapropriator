package br.com.marcosoft.sgi.selenium;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;

import br.com.marcosoft.sgi.model.ApropriationFile.Config;

import com.thoughtworks.selenium.Selenium;

/**
 * Selenium Support.
 */
public class SeleniumSupport {
    private static Selenium selenium;

    private SeleniumSupport() {
    }

    public static Selenium getSelenium() {
        return selenium;
    }

    /**
     * Inicializa o Selenium.
     */
    public static void initSelenium(Config config) {
        final WebDriver driver = getDriver(config);
        final String browserUrl = config.getUrlSgi();
        selenium = new WebDriverBackedSelenium(driver, browserUrl);
    }

    private static WebDriver getDriver(Config config) {
        final String browser = config.getBrowserType();
        if ("chrome".equals(browser)) {
            return getChromeDriver(config);
        }
        return getFirefoxDriver(config);
    }

    private static WebDriver getFirefoxDriver(Config config) {
        final ProfilesIni allProfiles = new ProfilesIni();
        final String profile = config.getFirefoxProfile();
        final FirefoxProfile firefoxProfile = allProfiles.getProfile(profile);
        final WebDriver driver = new FirefoxDriver(firefoxProfile);
        return driver;
    }

    private static WebDriver getChromeDriver(Config config) {
        final String key = "webdriver.chrome.driver";
        if (System.getProperty(key) == null) {
            final String planilhaDir = config.getPlanilhaDir();
            final String chromedriverPath = planilhaDir + File.separator + "chromedriver";
            if (new File(chromedriverPath).canExecute()) {
                System.setProperty(key, chromedriverPath);
            } else {
                System.setProperty(key, "/usr/bin/chromedriver");
            }
        }
        final WebDriver driver = new ChromeDriver();
        return driver;
    }

    public static  void stopSelenium() {
        selenium.stop();
    }

}

