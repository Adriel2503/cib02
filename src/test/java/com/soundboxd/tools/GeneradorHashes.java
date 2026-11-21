package com.soundboxd.tools;

import com.soundboxd.security.PasswordHasher;

/**
 * Herramienta para Valeria (B1): imprime "usuario  hash" de los usuarios de ejemplo.
 * Ejecutar con el botón verde de IntelliJ. Las claves de ejemplo son SOLO de demostración.
 * No va en src/main: es un apoyo, no parte del sistema.
 */
public class GeneradorHashes {
    public static void main(String[] args) {
        String[][] usuarios = {
            {"moderador1", "Moder1234"},
            {"usuario1",   "Usuario1234"},
            {"usuario2",   "Usuario5678"},
            {"usuario3",   "Usuario9012"}
        };
        for (String[] u : usuarios) {
            System.out.println(u[0] + "\t" + PasswordHasher.hash(u[1]));
        }
    }
}
