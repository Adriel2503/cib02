package com.soundboxd.tools;

import com.soundboxd.dao.UsuarioDAO;
import com.soundboxd.dao.UsuarioDAOImpl;
import com.soundboxd.model.Usuario;
import com.soundboxd.security.PasswordHasher;

/**
 * Prueba manual de solo lectura: lee a moderador1 de la base real y comprueba que
 * su hash corresponde a la clave de demostración. Ejecutar con el botón verde de IntelliJ.
 * Requiere db.properties con tus datos y los scripts 00 a 03 ya corridos.
 */
public class ProbarUsuarioDAO {
    public static void main(String[] args) {
        UsuarioDAO dao = new UsuarioDAOImpl();

        Usuario u = dao.buscarPorNombreUsuario("moderador1")
                .orElseThrow(() -> new IllegalStateException("No existe moderador1: ¿corriste el script 02?"));
        System.out.println("Usuario: " + u.getNombreUsuario() + " | rol: " + u.getRol() + " | activo: " + u.isActivo());
        System.out.println("Clave de demostración correcta: " + PasswordHasher.verificar("Moder1234", u.getHashContrasena()));
        System.out.println("Existe usuario1: " + dao.existeNombreUsuario("usuario1"));
        System.out.println("Existe 'nadie': " + dao.existeNombreUsuario("nadie"));
        System.out.println("Búsqueda por correo: " + dao.buscarPorCorreo("usuario1@example.com").isPresent());
    }
}
