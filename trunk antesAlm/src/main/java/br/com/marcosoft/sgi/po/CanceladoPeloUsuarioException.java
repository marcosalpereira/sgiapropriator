package br.com.marcosoft.sgi.po;

public class CanceladoPeloUsuarioException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    @Override
    public String getMessage() {
        return "Cancelado pelo usuário";
    }
    
}
