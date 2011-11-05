package br.com.marcosoft.sgi.po;

public class NotSelectedException extends RuntimeException {

    private static final long serialVersionUID = 8430237685933237580L;

    public NotSelectedException() {
        super();
    }

    public NotSelectedException(String message) {
        super(message);
    }

}
