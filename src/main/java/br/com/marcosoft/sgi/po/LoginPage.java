package br.com.marcosoft.sgi.po;

import br.com.marcosoft.sgi.EsperarLoginUsuario;

public class LoginPage extends PageObject {

    public HomePage login(String cpf, String pwd) {

        type("tbUserId", cpf);
        if (cpf != null && pwd != null) {
            getSelenium().type("tbSenha", pwd);
            clickAndWait("btnAvancar");
        }
        waitForCorrectLogin();
        return new HomePage();
    }

    private void waitForCorrectLogin() {
        EsperarLoginUsuario esperarLoginUsuario = null;
        for(;;) {
            if (getSelenium().isElementPresent("menulist")) {
                clearAlerts();
                break;
            } else {
                if (esperarLoginUsuario == null) {
                    esperarLoginUsuario = new EsperarLoginUsuario();
                }
            }
        }
        if (esperarLoginUsuario != null) {
            esperarLoginUsuario.dispose();
        }
    }

}
