package br.com.marcosoft.sgi.selenium;

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
    private Selenium selenium;
    private final Config config;

    public SeleniumSupport(Config config) {
        this.config = config;
    }

    public Selenium getSelenium() {
        return selenium;
    }

    /**
     * Inicializa o Selenium.
     */
    public void initSelenium() {
        final WebDriver driver = getDriver();
        final String browserUrl = config.getUrlSgi();
        selenium = new WebDriverBackedSelenium(driver, browserUrl);
    }

    private WebDriver getDriver() {
        final String browser = config.getBrowserType();
        if ("chrome".equals(browser)) {
            return getChromeDriver();
        }
        return getFirefoxDriver();
    }

    private WebDriver getFirefoxDriver() {
        final ProfilesIni allProfiles = new ProfilesIni();
        final String profile = config.getFirefoxProfile();
        final FirefoxProfile firefoxProfile = allProfiles.getProfile(profile);
        final WebDriver driver = new FirefoxDriver(firefoxProfile);
        return driver;
    }

    private WebDriver getChromeDriver() {
        final String key = "webdriver.chrome.driver";
        if (System.getProperty(key) != null) {
            System.setProperty(key, "/usr/bin/chromedriver");
        }
        final WebDriver driver = new ChromeDriver();
        return driver;
    }

    public  void stopSelenium() {
        selenium.stop();
    }

}

