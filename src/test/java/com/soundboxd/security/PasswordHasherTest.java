package com.soundboxd.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    @Test
    void verificaClaveCorrectaYRechazaIncorrecta() {
        String hash = PasswordHasher.hash("Clave1234");
        assertTrue(PasswordHasher.verificar("Clave1234", hash));
        assertFalse(PasswordHasher.verificar("otra", hash));
    }

    @Test
    void elHashNoEsLaClaveYUsaCosto12() {
        String hash = PasswordHasher.hash("Clave1234");
        assertNotEquals("Clave1234", hash);
        assertTrue(hash.startsWith("$2a$12$"));
    }

    @Test
    void mismaClaveGeneraHashesDistintosPorLaSal() {
        assertNotEquals(PasswordHasher.hash("Clave1234"), PasswordHasher.hash("Clave1234"));
    }

    @Test
    void entradasInvalidasNoLanzanExcepcionAlVerificar() {
        assertFalse(PasswordHasher.verificar(null, "x"));
        assertFalse(PasswordHasher.verificar("Clave1234", null));
        assertFalse(PasswordHasher.verificar("Clave1234", "esto-no-es-un-hash"));
    }
}
