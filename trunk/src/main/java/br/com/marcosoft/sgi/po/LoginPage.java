package br.com.marcosoft.sgi.po;

import javax.swing.JOptionPane;

public class LoginPage extends PageObject {

    public HomePage login(String cpf, String pwd) {
        type("tbUserId", cpf);
        if (cpf != null && pwd != null) {
            getSelenium().type("tbSenha", pwd);
            clickAndWait("btnAvancar");
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, digite a senha e clique em avancar");
            getSelenium().setCursorPosition("tbSenha", "0");
        }
        waitForCorrectLogin();
        return new HomePage();
    }


    private void waitForCorrectLogin() {
        for(;;) {
            if (getSelenium().isElementPresent("menulist")) {
                sleep(2000);
                break;
            }
        }
    }

}
