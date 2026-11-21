package com.soundboxd;

/**
 * Clase de arranque para el .jar con dependencias.
 *
 * No extiende Application a propósito: si el Main-Class del jar fuera una
 * clase que extiende Application, la JVM exigiría JavaFX como módulo y el
 * jar no abriría con "java -jar". Esta clase solo delega en Main.
 */
public class Launcher {
    public static void main(String[] args) {
        Main.main(args);
    }
}
