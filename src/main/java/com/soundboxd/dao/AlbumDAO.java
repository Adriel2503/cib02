package com.soundboxd.dao;

import com.soundboxd.model.Album;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

/** Acceso a datos de ALBUM. */
public interface AlbumDAO {

    int insertar(Album album);

    Optional<Album> buscarPorId(int idAlbum);

    /** Búsqueda por texto en el título del álbum, el nombre del artista o el género. */
    List<Album> buscar(String texto);

    List<Album> listarTodos();

    void actualizar(Album album);

    void eliminar(int idAlbum);

    /** Promedio de calificación contando solo reseñas PUBLICADAS; vacío si no hay ninguna. */
    OptionalDouble promedioPublicadas(int idAlbum);
}
