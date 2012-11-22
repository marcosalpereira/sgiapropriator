package br.com.marcosoft.sgi.po;

import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;

import br.com.marcosoft.sgi.ErroInesperadoSgi;
import br.com.marcosoft.sgi.WaitWindow;
import br.com.marcosoft.sgi.selenium.SeleniumSupport;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

public class PageObject {
    /**
     * Time out em segundos.
     */
    private static final int TIME_OUT_SEGUNDOS = 10;

    protected Selenium getSelenium() {
        return SeleniumSupport.getSelenium();
    }

    protected WebDriver getWebDriver() {
        return SeleniumSupport.getWebDriver();
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
     * Veja {@link Selenium#select(String, String)}. Espera at� que o elemento esteja com
     * o valor definido
     * @param locator an element locator
     * @param value value Se for <code>null</code>, retorna sem fazer nenhuma a��o
     */
    protected final void select(String locator, String value) {
        select(locator, value, false, TIME_OUT_SEGUNDOS);
    }

    /**
     * Veja {@link Selenium#select(String, String)}. Espera at� que o elemento esteja com
     * o valor definido
     * @param locator an element locator
     * @param value value Se for <code>null</code>, retorna sem fazer nenhuma a��o
     */
    protected final void select(String locator, String value,
        boolean ignoreSelectionError, int timeOut) {
        if (value == null || value.length() == 0) {
            return;
        }
        highlight(locator);
        for (int second = 0; second < timeOut; second++) {
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
                if (isErroInesperadoSgi()) {
                    throw new ErroInesperadoSgi();
                }
                System.out.println(e);
            }
            sleep(1000);
        }
        if (!ignoreSelectionError) {
            throw new NotSelectedException(
                "Valor '" + value + "' para elemento '" + locator + "' n�o selecionado");
        }
    }

    /**
     * highlight.
     * @param locator locator
     */
    private void highlight(String locator) {
        try {
            getSelenium().highlight(locator);
        } catch (final SeleniumException e) {
            //NOP
        }
    }

    protected void type(String locator, String value) {
        if (value != null) {
            try {
                getSelenium().type(locator, value);
            } catch (final SeleniumException e) {
                if (isErroInesperadoSgi()) {
                    throw new ErroInesperadoSgi();
                }
            }
        }
    }

    protected void click(String locator) {
        try {
            getSelenium().click(locator);
        } catch (final SeleniumException e) {
            if (isErroInesperadoSgi()) {
                throw new ErroInesperadoSgi();
            }
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

    /**
     * @param locator locator que deve esperar ficar presente
     * @param message mensagem de espera
     */
    protected void waitWindow(final String locator, final String message) {
        WaitWindow waitWindow = null;
        for(;;) {
            final boolean elementPresent = isElementPresentIgnoreUnhandledAlertException(locator);
            if (elementPresent) {
                clearAlertsIgnoreUnhandledAlertException();
                break;
            } else {
                if (waitWindow == null) {
                    waitWindow = new WaitWindow(message);
                }
            }

            if (isErroInesperadoSgi()) {
                waitWindow.dispose();
                throw new ErroInesperadoSgi();
            }
        }
        if (waitWindow != null) {
            waitWindow.dispose();
        }
    }

    public boolean isErroInesperadoSgi() {
        try {
        return getSelenium().isTextPresent(
            "An unhandled exception was generated during the execution of the current web request");
        } catch (final SeleniumException e) {
            return false;
        }
    }

    private void clearAlertsIgnoreUnhandledAlertException() {
        try {
            clearAlerts();
        } catch (final SeleniumException e) {
            if (e.getCause() instanceof UnhandledAlertException) {
                System.out.println("IGNORE ANY: UnhandledAlertException: Modal dialog present");
                sleep(1000);
            } else {
                throw e;
            }
        }
    }

    private boolean isElementPresentIgnoreUnhandledAlertException(String locator) {
        try {
            return getSelenium().isElementPresent(locator);
        } catch (final SeleniumException e) {
            if (e.getCause() instanceof UnhandledAlertException) {
                System.out.println("IGNORE ANY: UnhandledAlertException: Modal dialog present");
                sleep(1000);
            } else {
                throw e;
            }
        }
        return false;

    }

    /**
     * Obtem o texto de uma celula de uma tabela.
     * @param tableCellAddress endereco da celula na tabela
     * @return o texto.
     */
    protected String getTable(String tableCellAddress) {
        return getSelenium().getTable(tableCellAddress);
    }

    /**
     * Gera um localizador para a celula da tabela especificada.
     * @param idTabela id da tabela
     * @param linha numero da linha
     * @param coluna numero da coluna
     * @return localizador
     */
    protected String gerarLocalizadorTabela(final String idTabela, int linha, int coluna) {
        return String.format("%s.%d.%d", idTabela, linha, coluna);
    }


}
