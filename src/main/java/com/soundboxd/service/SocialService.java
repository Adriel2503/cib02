package com.soundboxd.service;

import com.soundboxd.model.Resena;
import com.soundboxd.model.Usuario;

import java.util.List;

/** Módulo M5: red social. No se puede seguir a uno mismo ni seguir dos veces al mismo usuario. */
public interface SocialService {

    void seguir(Usuario actor, int idSeguido);

    void dejarDeSeguir(Usuario actor, int idSeguido);

    List<Usuario> listarSeguidos(Usuario actor);

    /** Reseñas publicadas recientes de los usuarios seguidos. */
    List<Resena> actividadReciente(Usuario actor, int limite);
}
