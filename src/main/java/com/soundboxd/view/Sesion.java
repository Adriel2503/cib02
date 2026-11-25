package com.soundboxd.view;

import com.soundboxd.model.Rol;
import com.soundboxd.model.Usuario;

/**
 * Usuario con sesión iniciada en la aplicación de escritorio.
 *
 * Visitante = no hay sesión (getUsuario() devuelve null), igual que el actor null
 * que esperan los servicios. Las pantallas la usan para ocultar o deshabilitar opciones,
 * pero la validación real de permisos está en los servicios (ControlAcceso).
 */
public final class Sesion {

    private static Usuario usuarioActual;

    private Sesion() { }

    public static void iniciar(Usuario usuario) {
        usuarioActual = usuario;
    }

    /** AuthService.cerrarSesion() debe llamar a este método. */
    public static void cerrar() {
        usuarioActual = null;
    }

    /** Usuario actual, o null si es visitante. Es el "actor" que se pasa a los servicios. */
    public static Usuario getUsuario() {
        return usuarioActual;
    }

    public static boolean haySesion() {
        return usuarioActual != null;
    }

    public static boolean esModerador() {
        return usuarioActual != null && usuarioActual.getRol() == Rol.MODERADOR;
    }
}
