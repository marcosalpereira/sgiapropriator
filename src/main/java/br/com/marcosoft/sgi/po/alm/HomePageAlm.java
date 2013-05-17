package br.com.marcosoft.sgi.po.alm;

import br.com.marcosoft.sgi.model.Task;
import br.com.marcosoft.sgi.model.TaskDailySummary;
import br.com.marcosoft.sgi.po.PageObject;

public class HomePageAlm extends PageObject {

    private final String senha;

    public String getSenha() {
        return senha;
    }

    public HomePageAlm(String senha) {
        this.senha = senha;
    }

    public ApropriationPageAlm gotoApropriationPage(TaskDailySummary tds) {
        final Task task = tds.getFirstTask();
        final String url = String.format(
        		"https://alm.serpro/ccm/web/projects/%s" +
        		"#action=com.ibm.team.workitem.viewWorkItem&id=%s" +
        		"&tab=rastreamentodehoras", task.getProjeto());
        getSelenium().open(url);
        return new ApropriationPageAlm();
    }

    public LoginPageAlm logout() {
        getSelenium().selectFrame("relative=top");
        getSelenium().selectFrame("menu");
        getSelenium().click("link=Sair");
        getSelenium().waitForPageToLoad("30000");
        return new LoginPageAlm();
    }
}
