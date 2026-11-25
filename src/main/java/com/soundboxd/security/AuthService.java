package com.soundboxd.security;

import com.soundboxd.model.Usuario;

/**
 * M1: registro, inicio y cierre de sesión.
 * FIRMAS PUBLICADAS (viernes 20 nov). La implementación llega en B2 (20–25 nov),
 * cuando Amado publique UsuarioDAO.
 * Reglas: mensaje de error genérico ("Usuario o contraseña incorrectos"),
 * bloqueo de 3 min tras 5 fallos seguidos de un mismo usuario, rol USUARIO por defecto.
 */
public class AuthService {

    /** Valida usuario, correo y clave; rechaza duplicados; guarda hash BCrypt con rol USUARIO. */
    public Usuario registrar(String nombreUsuario, String correo, String clave) {
        throw new UnsupportedOperationException("Pendiente B2");
    }

    /** Devuelve el Usuario si las credenciales son correctas; si no, ReglaNegocioException genérica. */
    public Usuario iniciarSesion(String nombreUsuario, String clave) {
        throw new UnsupportedOperationException("Pendiente B2");
    }

    /** Limpia la Sesion del frontend. */
    public void cerrarSesion() {
        throw new UnsupportedOperationException("Pendiente B2");
    }
}
