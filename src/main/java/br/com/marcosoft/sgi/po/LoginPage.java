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
                    "Não consegui me logar usando a senha atualmente salva." +
                    "\nTalvez a senha anterior esteja expirada.");
                return login(cpf, passwordWindow.getPassword());
            }
        }

        waitWindow("menulist", "Esperando pelo login do usuário");
        return new HomePage(password);
    }

    private String determinarPassword(String pwd) {
        final String password;
        if (pwd == null && isSalvarSenha()) {
            final String info = String.format(
                "Essa versão, por default, se oferece para salvar a senha. " +
                "\nSe não quiser que a senha seja salva localmente. Use o botão 'Cancelar' agora e " +
                "\ncoloque na aba 'Config' da planilha o valor 'Não' para opção '%s'" +
                "\nOBS:A senha é salva criptografada.", Config.SGI_SALVAR_SENHA);
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
