package br.com.marcosoft.sgi.model;


public class ProjetoSgi extends Projeto {

    public ProjetoSgi(String ug, String nome) {
        super(new String[] {ug, nome});
    }
    
    public String getUg() {
        return getDado(0);
    }
    
    public void setUg(String ug) {
        setDado(0, ug);
    }
    
    public String getNome() {
        return getDado(1);
    }
    
    public void setNome(String nome) {
        setDado(1, nome);
    }    
    
}
