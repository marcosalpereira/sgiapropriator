package br.com.marcosoft.sgi.po;

import br.com.marcosoft.sgi.WaitWindow;

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
        WaitWindow esperarLoginUsuario = null;
        for(;;) {
            if (getSelenium().isElementPresent("menulist")) {
                clearAlerts();
                break;
            } else {
                if (esperarLoginUsuario == null) {
                    esperarLoginUsuario = new WaitWindow("Esperando pelo login do usuário");
                }
            }
        }
        if (esperarLoginUsuario != null) {
            esperarLoginUsuario.dispose();
        }
    }

}
