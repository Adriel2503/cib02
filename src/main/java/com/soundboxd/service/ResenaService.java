package com.soundboxd.service;

import com.soundboxd.model.Resena;
import com.soundboxd.model.Usuario;

import java.util.List;

/** Módulo M3: reseñas. Calificación 1 a 5, una reseña por usuario y álbum. */
public interface ResenaService {

    Resena guardarBorrador(Usuario actor, int idAlbum, int calificacion, String texto);

    Resena publicar(Usuario actor, int idResena);

    /** Solo el autor de la reseña. */
    void editar(Usuario actor, int idResena, int calificacion, String texto);

    /** Solo el autor de la reseña. */
    void eliminar(Usuario actor, int idResena);

    /** Solo reseñas PUBLICADAS. Disponible también para visitantes. */
    List<Resena> listarPorAlbum(int idAlbum);

    List<Resena> listarMias(Usuario actor);
}
