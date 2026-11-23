package com.soundboxd.service;

import com.soundboxd.model.Album;
import com.soundboxd.model.Cancion;
import com.soundboxd.model.Usuario;

import java.util.List;

/** Módulo M2: catálogo. Es lo único que llama la vista para álbumes y canciones. */
public interface CatalogoService {

    List<Album> buscarAlbumes(String texto);

    Album obtenerAlbum(int idAlbum);

    List<Cancion> listarCanciones(int idAlbum);

    /** Promedio con solo reseñas PUBLICADAS; 0 si el álbum no tiene reseñas. */
    double promedioAlbum(int idAlbum);

    /** Solo MODERADOR. */
    Album crearAlbum(Usuario actor, Album album);

    /** Solo MODERADOR. */
    void actualizarAlbum(Usuario actor, Album album);

    /** Solo MODERADOR. */
    void eliminarAlbum(Usuario actor, int idAlbum);
}
