package com.soundboxd.util;

import java.util.regex.Pattern;

/** Validadores de entrada (lista blanca). Se usan ANTES de tocar la base de datos. */
public final class Validador {

    private static final Pattern USUARIO = Pattern.compile("^[A-Za-z0-9_]{3,20}$");
    private static final Pattern CORREO  = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validador() { }

    public static boolean usuarioValido(String s) {
        return s != null && USUARIO.matcher(s).matches();
    }

    public static boolean correoValido(String s) {
        return s != null && s.length() <= 254 && CORREO.matcher(s).matches();
    }

    /** Largo mínimo 8, al menos una letra y un número. */
    public static boolean claveValida(String s) {
        if (s == null || s.length() < 8) return false;
        boolean letra = false, numero = false;
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) letra = true;
            if (Character.isDigit(c)) numero = true;
        }
        return letra && numero;
    }

    /** Calificación de reseña: entero de 1 a 5. */
    public static boolean calificacionValida(int n) {
        return n >= 1 && n <= 5;
    }
}
