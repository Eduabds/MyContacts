package br.mycontacts.app.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ValidadorEmailTest {

    @Test
    void aceitaEnderecosDeEmailValidos() {
        assertTrue(ValidadorEmail.isValido("pessoa@example.com"));
        assertTrue(ValidadorEmail.isValido("nome.sobrenome+tag@empresa.org"));
        assertTrue(ValidadorEmail.isValido("  contato@exemplo.com  "));
    }

    @Test
    void rejeitaEnderecosInvalidosOuNulos() {
        assertFalse(ValidadorEmail.isValido(null));
        assertFalse(ValidadorEmail.isValido(""));
        assertFalse(ValidadorEmail.isValido("sem-arroba.example.com"));
        assertFalse(ValidadorEmail.isValido("pessoa@dominio"));
        assertFalse(ValidadorEmail.isValido("@exemplo.com"));
    }
}
