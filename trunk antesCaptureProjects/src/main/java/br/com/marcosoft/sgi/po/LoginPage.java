package br.com.marcosoft.sgi.po;


public class LoginPage extends PageObject {

    public HomePage login(String cpf, String pwd) {

        type("tbUserId", cpf);
        if (cpf != null && pwd != null) {
            getSelenium().type("tbSenha", pwd);
            clickAndWait("btnAvancar");
        }
        waitWindow("menulist", "Esperando pelo login do usuário");
        return new HomePage();
    }

}
