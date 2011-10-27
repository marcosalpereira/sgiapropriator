package br.com.marcosoft.sgi.selenium;

import java.io.File;

import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.seleniuminspector.QSelenium;
import org.seleniuminspector.SeleniumWithServerAutostartFactory;

import com.thoughtworks.selenium.Selenium;

public class MySeleniumFactory extends SeleniumWithServerAutostartFactory {
    private File firefoxProfileTemplate;

    public MySeleniumFactory(int serverPort, String browserPath, String browserUrl,
        File firefoxProfileTemplate) {
        super(serverPort, browserPath, browserUrl);
        this.firefoxProfileTemplate = firefoxProfileTemplate;
    }

    @Override
    public Selenium getSelenium() {
        try {
            RemoteControlConfiguration conf = new RemoteControlConfiguration();

            // specify firefox profile template here
            conf.setFirefoxProfileTemplate(firefoxProfileTemplate);

            conf.setPort(getServerPort());
            new SeleniumServer(isSlowResources(), conf).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new QSelenium(getServerHost(), getServerPort(), getBrowserPath(),
            getBrowserUrl(), isAddNamespacesToXpath());
    }

}
