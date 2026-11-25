package com.soundboxd.security;

import com.soundboxd.model.Rol;
import com.soundboxd.model.Usuario;
import com.soundboxd.util.ReglaNegocioException;

import java.util.Arrays;

/**
 * Punto único de decisión de permisos. Los servicios (Amado) lo llaman ANTES de actuar.
 * Visitante = actor null (sin sesión).
 * NOTA: ajusta los imports de Rol, Usuario y ReglaNegocioException a los paquetes de Amado.
 */
public final class ControlAcceso {

    private static final String SIN_PERMISO = "No tienes permiso para esta acción.";

    private ControlAcceso() { }

    /** Exige que el actor tenga uno de los roles permitidos. */
    public static void exigirRol(Usuario actor, Rol... permitidos) {
        if (actor == null || !Arrays.asList(permitidos).contains(actor.getRol())) {
            throw new ReglaNegocioException(SIN_PERMISO);
        }
    }

    /** Exige sesión iniciada (Usuario o Moderador). */
    public static void exigirSesion(Usuario actor) {
        exigirRol(actor, Rol.USUARIO, Rol.MODERADOR);
    }

    /** Exige moderador (ocultar/restituir reseñas, gestionar catálogo). */
    public static void exigirModerador(Usuario actor) {
        exigirRol(actor, Rol.MODERADOR);
    }

    /** Solo el autor puede editar/borrar su reseña o lista (el moderador no edita contenido ajeno). */
    public static void exigirPropietario(Usuario actor, int idPropietario) {
        exigirSesion(actor);
        if (actor.getIdUsuario() != idPropietario) {
            throw new ReglaNegocioException(SIN_PERMISO);
        }
    }
}
