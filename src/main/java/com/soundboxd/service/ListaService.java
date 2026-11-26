package com.soundboxd.service;

import com.soundboxd.model.Album;
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

    /** Devuelve la lista solo si es pública o si el actor es su dueño. */
    default Lista obtenerLista(Usuario actor, int idLista) {
        throw new UnsupportedOperationException("Pendiente en esta implementación");
    }

    /** Álbumes de la lista en orden de posición, con el mismo permiso que obtenerLista(actor, idLista). */
    default List<Album> listarAlbumes(Usuario actor, int idLista) {
        throw new UnsupportedOperationException("Pendiente en esta implementación");
    }

    void eliminarLista(Usuario actor, int idLista);
}
