package com.soundboxd.service;

import com.soundboxd.model.Resena;
import com.soundboxd.model.Usuario;

import java.util.List;

/** Módulo M5: red social. No se puede seguir a uno mismo ni seguir dos veces al mismo usuario. */
public interface SocialService {

    void seguir(Usuario actor, int idSeguido);

    void dejarDeSeguir(Usuario actor, int idSeguido);

    List<Usuario> listarSeguidos(Usuario actor);

    /** Usuarios que se pueden seguir cuyo nombre contiene el texto; no incluye al actor. */
    default List<Usuario> buscarUsuarios(Usuario actor, String texto) {
        throw new UnsupportedOperationException("Pendiente en esta implementación");
    }

    /** Reseñas publicadas recientes de los usuarios seguidos. */
    List<Resena> actividadReciente(Usuario actor, int limite);
}
