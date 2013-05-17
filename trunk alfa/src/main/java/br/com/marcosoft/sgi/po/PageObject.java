package br.com.marcosoft.sgi.po;

import br.com.marcosoft.sgi.selenium.SeleniumSupport;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

public class PageObject {
    /**
     * Time out em segundos.
     */
    private static final int TIME_OUT_SEGUNDOS = 20;

    private boolean ignoreSelectionErrors = false;

    public Selenium getSelenium() {
        return SeleniumSupport.getSelenium();
    }

    protected void setIgnoreSelectionErrors(boolean ignoreSelectionErrors) {
        this.ignoreSelectionErrors = ignoreSelectionErrors;
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
    protected final void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            return;
        }
    }

    /**
     * Veja {@link Selenium#select(String, String)}. Espera at� que o elemento esteja com
     * o valor definido
     * @param locator an element locator
     * @param value value Se for <code>null</code>, retorna sem fazer nenhuma a��o
     * @param checkEditableFirst verifica se est� editavel antes de iniciar
     */
    protected final void select(String locator, String value) {
        if (value == null) {
            return;
        }
        getSelenium().highlight(locator);
        for (int second = 0; second < TIME_OUT_SEGUNDOS; second++) {
            try {
                //se est� editavel seleciona.
                //util nos casos em que o select est� desabilitado e o valor
                //j� est� setado
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
        if (!ignoreSelectionErrors) {
            throw new NotSelectedException(
                "Valor '" + value + "' para elemento '" + locator + "' n�o selecionado");
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



}