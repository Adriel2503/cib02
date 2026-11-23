package com.soundboxd.dao;

import com.soundboxd.model.Usuario;

import java.util.Optional;

/** Acceso a datos de USUARIO. Todas las consultas usan PreparedStatement. */
public interface UsuarioDAO {

    /** Inserta la cuenta y devuelve el id generado. Lanza ReglaNegocioException si el usuario o el correo ya existen. */
    int insertar(Usuario usuario);

    Optional<Usuario> buscarPorId(int idUsuario);

    Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario);

    Optional<Usuario> buscarPorCorreo(String correo);

    boolean existeNombreUsuario(String nombreUsuario);

    boolean existeCorreo(String correo);

    /** Actualiza correo, hash, rol y estado activo de la cuenta. */
    void actualizar(Usuario usuario);

    /** Baja lógica (activo = 0). No se borra la fila para no perder reseñas ni seguimientos. */
    void desactivar(int idUsuario);
}
