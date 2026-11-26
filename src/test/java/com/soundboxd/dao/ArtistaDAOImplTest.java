package com.soundboxd.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.soundboxd.model.Album;
import com.soundboxd.model.Artista;
import com.soundboxd.pruebas.PruebaConBase;
import com.soundboxd.util.ReglaNegocioException;
import org.junit.jupiter.api.Test;

class ArtistaDAOImplTest extends PruebaConBase {

    private final ArtistaDAO dao = new ArtistaDAOImpl();

    @Test
    void insertaLeeActualizaYElimina() {
        Artista a = crearArtista();
        a.setPais("Chile");
        dao.actualizar(a);
        assertEquals("Chile", dao.buscarPorId(a.getIdArtista()).orElseThrow().getPais());
        assertTrue(dao.listarTodos().stream().anyMatch(x -> x.getIdArtista() == a.getIdArtista()));

        dao.eliminar(a.getIdArtista());
        assertTrue(dao.buscarPorId(a.getIdArtista()).isEmpty());
    }

    @Test
    void noEliminaUnArtistaConAlbumes() {
        Album album = crearAlbum();
        assertThrows(ReglaNegocioException.class, () -> dao.eliminar(album.getIdArtista()));
    }
}
