package com.soundboxd.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorTest {

    @Test void claveDebilRechazada() {
        assertFalse(Validador.claveValida("abc123"));      // corta
        assertFalse(Validador.claveValida("abcdefgh"));    // sin número
        assertFalse(Validador.claveValida("12345678"));    // sin letra
        assertFalse(Validador.claveValida(null));
    }

    @Test void claveFuerteAceptada() {
        assertTrue(Validador.claveValida("Clave1234"));
    }

    @Test void correoInvalidoRechazado() {
        assertFalse(Validador.correoValido("sin-arroba"));
        assertFalse(Validador.correoValido("a@b"));
        assertTrue(Validador.correoValido("bryan@uni.pe"));
    }

    @Test void usuarioInvalidoRechazado() {
        assertFalse(Validador.usuarioValido("ab"));
        assertFalse(Validador.usuarioValido("' OR 1=1 --"));
        assertTrue(Validador.usuarioValido("bryan_t"));
    }

    @Test void calificacionFueraDeRango() {
        assertFalse(Validador.calificacionValida(0));
        assertFalse(Validador.calificacionValida(6));
        assertTrue(Validador.calificacionValida(5));
    }
}
