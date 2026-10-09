package br.mycontacts.app.utils;

import java.util.regex.Pattern;

	// Classe utilitária responsável por validar o formato de um email.
	// Não deve ser instanciada (construtor privado).
public class ValidadorEmail {
 
    private static final String REGEX_EMAIL = "^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern PADRAO = Pattern.compile(REGEX_EMAIL);
 
    private ValidadorEmail() {
    }
 
    public static boolean isValido(String email) {
        if (email == null) {
            return false;
        }
        return PADRAO.matcher(email.trim()).matches();
    }
}