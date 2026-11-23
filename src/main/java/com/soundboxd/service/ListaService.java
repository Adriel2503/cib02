package com.soundboxd.service;

import com.soundboxd.model.Lista;
import com.soundboxd.model.Usuario;

import java.util.List;

/** Módulo M4: listas. Solo el dueño modifica su lista; crearListaConAlbumes es atómica. */
public interface ListaService {

    /** Transacción: si falla un álbum no queda ni la lista ni ningún álbum a medias. */
    Lista crearListaConAlbumes(Usuario actor, String nombre, String descripcion, boolean esPublica, List<Integer> idsAlbum);

    void agregarAlbum(Usuario actor, int idLista, int idAlbum);

    void quitarAlbum(Usuario actor, int idLista, int idAlbum);

    /** idsAlbumEnOrden define la nueva posición de cada álbum (1, 2, 3...). */
    void reordenar(Usuario actor, int idLista, List<Integer> idsAlbumEnOrden);

    List<Lista> listarMisListas(Usuario actor);

    Lista obtenerLista(int idLista);

    void eliminarLista(Usuario actor, int idLista);
}
