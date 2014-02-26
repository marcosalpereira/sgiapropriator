package br.com.marcosoft.sgi.model;


public class ProjetoAlm extends Projeto {

    public ProjetoAlm(String tarefa, String idItemTrabalho) {
        super(new String[] {tarefa, idItemTrabalho});
    }

    @Override
    public String getNome() {
        return getDado(0);
    }

    public String getIdItemTrabalho() {
        return getDado(1);
    }

}
