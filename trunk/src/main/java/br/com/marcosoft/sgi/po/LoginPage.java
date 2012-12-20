package br.com.marcosoft.sgi.po;

import br.com.marcosoft.sgi.ReadPasswordWindow;
import br.com.marcosoft.sgi.model.ApropriationFile.Config;


public class LoginPage extends PageObject {

    public HomePage login(String cpf, String pwd) {
        final String password = determinarPassword(pwd);

        type("tbUserId", cpf);
        if (cpf != null && password != null) {
            getSelenium().type("tbSenha", password);
            clickAndWait("btnAvancar");

            if (pwd != null && !isElementPresentIgnoreUnhandledAlertException("menulist")) {
                final ReadPasswordWindow passwordWindow = new ReadPasswordWindow(
                    "Informe a nova senha",
                    "N�o consegui me logar usando a senha atualmente salva." +
                    "\nTalvez a senha anterior esteja expirada.");
                return login(cpf, passwordWindow.getPassword());
            }
        }

        waitWindow("menulist", "Esperando pelo login do usu�rio");
        return new HomePage(password);
    }

    private String determinarPassword(String pwd) {
        final String password;
        if (pwd == null && isSalvarSenha()) {
            final String info = String.format(
                "Essa vers�o, por default, se oferece para salvar a senha. " +
                "\nSe n�o quiser que a senha seja salva localmente. Use o bot�o 'Cancelar' agora e " +
                "\ncoloque na aba 'Config' da planilha o valor 'N�o' para op��o '%s'" +
                "\nOBS:A senha � salva criptografada.", Config.SGI_SALVAR_SENHA);
            final ReadPasswordWindow passwordWindow = new ReadPasswordWindow(
                "Informe a senha",
                info);
            password = passwordWindow.getPassword();
        } else {
            password = pwd;
        }
        return password;
    }

    private boolean isSalvarSenha() {
        final String salvarSenha = System.getProperty(Config.SGI_SALVAR_SENHA, "Sim");
        if ("Sim".equalsIgnoreCase(salvarSenha)) {
            return true;
        }
        return false;
    }

}
