package br.com.marcosoft.sgi.po;


public class Sgi extends PageObject {

    public LoginPage gotoLoginPage() {
        getSelenium().open("/Default.aspx");
        return new LoginPage();
    }
}
