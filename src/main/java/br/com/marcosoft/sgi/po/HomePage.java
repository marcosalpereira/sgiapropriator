package br.com.marcosoft.sgi.po;


public class HomePage extends PageObject {

    public ApropriationPage gotoApropriationPage() {
        getSelenium().selectFrame("menu");
        getSelenium().click("link=Apropria��o");
        getSelenium().selectFrame("relative=up");
        getSelenium().selectFrame("principal");
        sleep(2000);
        getSelenium().click("link=Registrar apropria��o");
        getSelenium().waitForPageToLoad("30000");
        return new ApropriationPage();
    }
}
