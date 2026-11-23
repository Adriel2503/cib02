package com.soundboxd.dao;

import com.soundboxd.model.Artista;

import java.util.List;
import java.util.Optional;

/** Acceso a datos de ARTISTA. */
public interface ArtistaDAO {

    int insertar(Artista artista);

    Optional<Artista> buscarPorId(int idArtista);

    List<Artista> listarTodos();

    void actualizar(Artista artista);

    /** Falla si el artista aún tiene álbumes (regla NO ACTION de la base). */
    void eliminar(int idArtista);
}
