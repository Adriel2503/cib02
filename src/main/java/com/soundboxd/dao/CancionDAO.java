package com.soundboxd.dao;

import com.soundboxd.model.Cancion;

import java.util.List;

/** Acceso a datos de CANCION. */
public interface CancionDAO {

    int insertar(Cancion cancion);

    List<Cancion> listarPorAlbum(int idAlbum);

    void actualizar(Cancion cancion);

    void eliminar(int idCancion);
}
