package br.com.marcosoft.sgi.po.alm;

import br.com.marcosoft.sgi.po.PageObject;


public class Alm extends PageObject {

    public LoginPageAlm gotoLoginPage() {
        getSelenium().open("/");
        return new LoginPageAlm();
    }
}
