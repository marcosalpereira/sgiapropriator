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

        criarLinhaTempo();
        irParaSemana(data);
        digitarMinutos(data, qtdMinutos);
        click("css=button.SaveButtonBottom");

        incluirComentario(task, qtdMinutos);

    }

    private void incluirComentario(final Task task, int qtdMinutos) {
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
        final WebElement saveButton = driver.findElement(
            By.cssSelector("button.SaveButtonBottom"));
        saveButton.click();
    }

    private void digitarMinutos(Date data, int qtdMinutos) {
        final int weekDay = Util.getWeekDay(data);
        final String locator =
            String.format("//*[@id='Timesheet_Table_Row_ID0']/td[%d]/span/input", weekDay + 2);
        final double valorAtual = Util.parseDouble(getSelenium().getText(locator), 0);
        final double horaDecimal = qtdMinutos / 60.0;
        final double novoValor = valorAtual + horaDecimal;
        type(locator, Util.formatMinutesDecimal(novoValor));
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
       click("//*[@id='Timesheet_previous_button']/a");
    }

    private void irParaSemanaAnterior() {
        click("//*[@id='Timesheet_next_button']/a");
    }

    private int isDataDentroSemanaAtual(Calendar dataApropriacao) {
        final String text = getSelenium().getValue(
            "//*[@id='widget_com_ibm_team_tpt_web_ui_timesheet_internal_editor_WeekDateTextBox_0']/div[3]/input[2]");
        final Calendar dataInicialPagina = Util.parseCalendar(Util.YYYY_MM_DD_FORMAT, text);
        final Calendar dataFinalPagina = (Calendar) dataInicialPagina.clone(); dataFinalPagina.add(Calendar.DAY_OF_MONTH, 7);
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
