package br.mycontacts.app.exceptions;


	// Tratamento de Exceção customizada para quando um contato não é encontrado na agenda
public class ContatoNaoEncontradoException extends Exception {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContatoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}