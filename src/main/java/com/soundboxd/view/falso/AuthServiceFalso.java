package com.soundboxd.view.falso;

import com.soundboxd.model.Rol;
import com.soundboxd.model.Usuario;
import com.soundboxd.security.AuthService;
import com.soundboxd.util.ReglaNegocioException;
import com.soundboxd.view.Sesion;

import java.time.LocalDateTime;

/** Reemplaza temporalmente a AuthService (B2 de Bryan). Hereda de la clase porque no es una interfaz. */
public class AuthServiceFalso extends AuthService {

    @Override
    public Usuario registrar(String nombreUsuario, String correo, String clave) {
        if (vacio(nombreUsuario) || vacio(correo) || vacio(clave)) {
            throw new ReglaNegocioException("Completa todos los campos.");
        }
        if (!correo.contains("@") || !correo.contains(".")) {
            throw new ReglaNegocioException("El correo no es válido.");
        }
        if (clave.length() < 8) {
            throw new ReglaNegocioException("La clave debe tener al menos 8 caracteres.");
        }
        if (DatosFalsos.usuarioPorNombre(nombreUsuario) != null) {
            throw new ReglaNegocioException("Ese nombre de usuario ya está en uso.");
        }
        Usuario u = new Usuario();
        u.setIdUsuario(DatosFalsos.nuevoIdUsuario());
        u.setNombreUsuario(nombreUsuario);
        u.setCorreo(correo);
        u.setRol(Rol.USUARIO);
        u.setActivo(true);
        u.setFechaRegistro(LocalDateTime.now());
        DatosFalsos.USUARIOS.add(u);
        DatosFalsos.CLAVES.put(u.getIdUsuario(), clave);
        return u;
    }

    @Override
    public Usuario iniciarSesion(String nombreUsuario, String clave) {
        Usuario u = vacio(nombreUsuario) ? null : DatosFalsos.usuarioPorNombre(nombreUsuario);
        if (u == null || clave == null || !clave.equals(DatosFalsos.CLAVES.get(u.getIdUsuario()))) {
            // Mensaje genérico a propósito: no revela si el usuario existe.
            throw new ReglaNegocioException("Usuario o contraseña incorrectos");
        }
        return u;
    }

    @Override
    public void cerrarSesion() {
        Sesion.cerrar();
    }

    private static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
