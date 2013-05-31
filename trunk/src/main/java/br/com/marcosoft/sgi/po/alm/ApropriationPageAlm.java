package br.com.marcosoft.sgi.po.alm;

import java.util.Calendar;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import br.com.marcosoft.sgi.model.Task;
import br.com.marcosoft.sgi.model.TaskDailySummary;
import br.com.marcosoft.sgi.po.PageObject;
import br.com.marcosoft.sgi.util.Util;

public class ApropriationPageAlm extends PageObject {

    public ApropriationPageAlm() {
        if (!isElementPresent("Timesheet_next_button")) {
            throw new RuntimeException("Ops! Não estou na página de apropriação!");
        }
    }

    /**
     * Apropriate task.
     * @param taskDailySummary task daily summary
     */
    public void apropriate(final TaskDailySummary taskDailySummary) {
        final Date data = taskDailySummary.getData();
        final Task task = taskDailySummary.getFirstTask();
        final int qtdMinutos = taskDailySummary.getSum();

        irParaSemana(data);
        criarLinhaTempo();
        digitarMinutos(data, qtdMinutos);
        click("css=button.SaveButtonBottom");
        verificarMensagemErro();

        incluirComentario(task, qtdMinutos);

    }

    private void verificarMensagemErro() {
        if (getSelenium().isElementPresent("css=div.validationMessageError")) {
            final WebElement element = getWebDriver().findElement(By.className("validationMessageAnchor"));
            final String text = element.getAttribute("title");
            throw new RuntimeException(text);
        }
    }

    private void incluirComentario(final Task task, int qtdMinutos) {
        if (task.getDescricao().isEmpty()) {
            return;
        }

        final String url = montarUrlVisaoGeralAlm(task.getProjetoAlm(), task.getIdItemTrabalho());
        getSelenium().open(url);

        final String comentario = task.getDescricao() + " (" +  Util.formatMinutes(qtdMinutos) + ")";

        click("link=Incluir Comentário");

        final WebDriver driver = getWebDriver();
        final WebElement fr = driver.findElement(By.className("dijitEditorIFrame"));
        driver.switchTo().frame(fr);

        final WebElement element = driver.findElement(By.className("RichTextBody"));
        element.sendKeys(comentario);

        driver.switchTo().defaultContent();
        salvarAlteracoes();
    }

    private void salvarAlteracoes() {
        final WebDriver driver = getWebDriver();
        final WebElement saveButton = driver.findElement(
            By.cssSelector("button.SaveButtonBottom"));
        saveButton.click();


        for (int i = 0; i < 30; i++) {
            sleep(1000);
            if (!saveButton.isEnabled()) {
                break;
            }
        }
    }

    private void digitarMinutos(Date data, int qtdMinutos) {
        final int indiceDiaSemana = Util.getWeekDay(data) + 2;
        final String horaXpath = String.format(
            "//*[@id='Timesheet_Table']/div[1]/table[1]/tbody/tr[1]/td[%d]/span/input",
            indiceDiaSemana);
        final WebElement element = getWebDriver().findElement(By.xpath(horaXpath));
        final String preValue = element.getAttribute("prevalue").replace(',', '.');
        final double valorAtual = Util.parseDouble(preValue, 0);
        final double horaDecimal = qtdMinutos / 60.0;
        final double novoValor = valorAtual + horaDecimal;
        element.sendKeys(Util.formatMinutesDecimal(novoValor));

    }

    private void irParaSemana(Date data) {
        final Calendar dataApropriacao = Util.toCalendar(data);
        for (;;) {
            final int dataNaSemana = isDataDentroSemanaAtual(dataApropriacao);
            if (dataNaSemana == 0) {
                break;
            }
            if (dataNaSemana < 0) {
                irParaSemanaAnterior();
            } else {
                irParaSemanaSeguinte();
            }
        }

    }

    private void irParaSemanaSeguinte() {
       click("//*[@id='Timesheet_next_button']/a");
    }

    private void irParaSemanaAnterior() {
        click("//*[@id='Timesheet_previous_button']/a");
    }

    private int isDataDentroSemanaAtual(Calendar dataApropriacao) {
        final String text = getSelenium().getValue(
            "//*[@id='Timesheet_weekTextBox']/div[1]/div[1]/div[1]/div[3]/input[2]");
        final Calendar dataInicialPagina = Util.parseCalendar(Util.YYYY_MM_DD_FORMAT, text);

        final Calendar dataFinalPagina = Util.addDay(dataInicialPagina, 7);

        if (dataApropriacao.compareTo(dataInicialPagina) == 0) {
            return 0;
        }
        if (dataApropriacao.compareTo(dataInicialPagina) < 0) {
            return -1;
        }
        if (dataApropriacao.compareTo(dataFinalPagina) <= 0) {
            return 0;
        }
        return 1;
    }

    private void criarLinhaTempo() {
        click("//span[@id='Timecode_addButton']/span[2]/a/span[2]");
    }

}
