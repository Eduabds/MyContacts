package br.mycontacts.app.exceptions;

public class ErroPersistenciaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ErroPersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
