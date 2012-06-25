package br.com.marcosoft.sgi.po;



public class HomePage extends PageObject {

    public ApropriationPage gotoApropriationPage(boolean apropriacaoSubordinado) {
        getSelenium().selectFrame("menu");
        getSelenium().click("link=Apropria��o");
        getSelenium().selectFrame("relative=top");
        getSelenium().selectFrame("principal");
        if (apropriacaoSubordinado) {
            getSelenium().click("link=Registrar Apropria��o dos Subordinados");
        } else {
            getSelenium().click("link=Registrar apropria��o");
        }
        getSelenium().waitForPageToLoad("30000");
        return new ApropriationPage();
    }

    public LoginPage logout() {
        getSelenium().selectFrame("relative=top");
        getSelenium().selectFrame("menu");
        getSelenium().click("link=Sair");
        getSelenium().waitForPageToLoad("30000");
        return new LoginPage();
    }
}