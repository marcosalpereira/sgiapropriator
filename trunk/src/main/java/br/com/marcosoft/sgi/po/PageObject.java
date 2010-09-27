package br.com.marcosoft.sgi.po;

import org.seleniuminspector.SeleniumHolder;
import org.seleniuminspector.ServerLoadingMode;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

public class PageObject {
    /**
     * Time out em segundos.
     */
    private static final int TIME_OUT = 20;

    public Selenium getSelenium() {
        return SeleniumHolder.getInstance().getSelenium();
    }

    /**
     * Espera a pagina ser carregada.
     */
    protected final void waitForPageToLoad() {
        ServerLoadingMode.getInstance().waitForLoad();
    }

    /**
     * Clicks on a link, button, checkbox or radio button and wait for page to
     * load.
     * @param locator an element locator
     */
    protected final void clickAndWait(String locator) {
        getSelenium().click(locator);
        waitForPageToLoad();
    }


    /**
     * Pausa a execucao durante o numero de milisegundos especificados.
     * @param millis numero de milisegundos
     */
    protected final void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            return;
        }
    }

    /**
     * Veja {@link Selenium#select(String, String)}. Espera até que o elemento esteja com
     * o valor definido
     * @param locator an element locator
     * @param value value Se for <code>null</code>, retorna sem fazer nenhuma ação
     */
    protected final void select(String locator, String value) {
        if (value == null) {
            return;
        }
        for (int second = 0; second < TIME_OUT; second++) {
            try {
                //se está editavel seleciona.
                //util nos casos em que o selec está desabilitado e o valor
                //já está setado
                if (getSelenium().isEditable(locator)) {
                    getSelenium().select(locator, value);
                }
                //Se ficou igual, pode sair
                if (value.equals(getSelenium().getSelectedLabel(locator))) {
                    return;
                }
            } catch (SeleniumException e) {
                System.out.println(e);
            }
            sleep(1000);
        }
        throw new RuntimeException("Valor '" + value + "' para elemento '" + locator + "' não selecionado");
    }

    protected void type(String locator, String value) {
        if (value != null) {
            getSelenium().type(locator, value);
        }
    }


}
