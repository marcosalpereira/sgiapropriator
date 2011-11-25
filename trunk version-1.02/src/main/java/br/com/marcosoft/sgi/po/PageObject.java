package br.com.marcosoft.sgi.po;

import br.com.marcosoft.sgi.selenium.SeleniumSupport;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

public class PageObject {
    /**
     * Time out em segundos.
     */
    private static final int TIME_OUT_SEGUNDOS = 20;

    public Selenium getSelenium() {
        return SeleniumSupport.getSelenium();
    }

    /**
     * Espera a pagina ser carregada.
     */
    protected final void waitForPageToLoad() {
        getSelenium().waitForPageToLoad(String.valueOf(TIME_OUT_SEGUNDOS * 1000));
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
    public static final void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
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
        select(locator, value, false, TIME_OUT_SEGUNDOS);
    }

    /**
     * Veja {@link Selenium#select(String, String)}. Espera até que o elemento esteja com
     * o valor definido
     * @param locator an element locator
     * @param value value Se for <code>null</code>, retorna sem fazer nenhuma ação
     */
    protected final void select(String locator, String value,
        boolean ignoreSelectionError, int timeOut) {
        if (value == null || value.length() == 0) {
            return;
        }
        getSelenium().highlight(locator);
        for (int second = 0; second < timeOut; second++) {
            try {
                //se está editavel seleciona.
                //util nos casos em que o select está desabilitado e o valor
                //já está setado
                if (getSelenium().isEditable(locator)) {
                    getSelenium().select(locator, value);
                }
                //Se ficou igual, pode sair
                if (value.equals(getSelenium().getSelectedLabel(locator))) {
                    return;
                }
            } catch (final SeleniumException e) {
                System.out.println(e);
            }
            sleep(1000);
        }
        if (!ignoreSelectionError) {
            throw new NotSelectedException(
                "Valor '" + value + "' para elemento '" + locator + "' não selecionado");
        }
    }

    protected void type(String locator, String value) {
        if (value != null) {
            getSelenium().type(locator, value);
        }
    }

    /**
     * Limpar qualquer alerta que foi mostrado.
     */
    protected void clearAlerts() {
        while (getSelenium().isAlertPresent()) {
            getSelenium().getAlert();
        }
    }

    protected void setEnabled(String id, boolean enabled) {
        if (enabled == getSelenium().isEditable(id)) {
            return;
        }
        final String script = new StringBuilder()
            .append(String.format("var element = selenium.browserbot.findElement(\"id=%s\");", id))
            .append("element.disabled = " + !enabled)
            .toString();
        getSelenium().getEval(script);
    }


}
