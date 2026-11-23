package com.soundboxd;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

/**
 * Prueba de humo del esqueleto: confirma que las dependencias y las clases
 * de arranque están disponibles. No abre ventanas, así que corre en cualquier equipo.
 */
class EsqueletoTest {

    @Test
    void javafxEstaDisponible() {
        assertDoesNotThrow(() -> Class.forName("javafx.application.Application"));
    }

    @Test
    void driverSqlServerEstaDisponible() {
        assertDoesNotThrow(() -> Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"));
    }

    @Test
    void jbcryptEstaDisponible() {
        assertDoesNotThrow(() -> Class.forName("org.mindrot.jbcrypt.BCrypt"));
    }

    @Test
    void clasesDeArranqueExisten() {
        assertDoesNotThrow(() -> Class.forName("com.soundboxd.Main"));
        assertDoesNotThrow(() -> Class.forName("com.soundboxd.Launcher"));
    }
}
