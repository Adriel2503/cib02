package com.soundboxd.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.soundboxd.model.Album;
import com.soundboxd.model.EstadoResena;
import com.soundboxd.model.Rol;
import com.soundboxd.pruebas.PruebaConBase;
import org.junit.jupiter.api.Test;

class AlbumDAOImplTest extends PruebaConBase {

    private final AlbumDAO dao = new AlbumDAOImpl();

    @Test
    void insertaLeeActualizaYElimina() {
        Album a = crearAlbum();
        a.setGenero("Jazz");
        dao.actualizar(a);
        assertEquals("Jazz", dao.buscarPorId(a.getIdAlbum()).orElseThrow().getGenero());

        dao.eliminar(a.getIdAlbum());
        assertTrue(dao.buscarPorId(a.getIdAlbum()).isEmpty());
    }

    @Test
    void buscaPorTituloYPorArtista() {
        Album a = crearAlbum();
        String artista = dao.buscarPorId(a.getIdAlbum()).orElseThrow().getNombreArtista();
        assertEquals(1, dao.buscar(a.getTitulo()).size());
        assertEquals(1, dao.buscar(artista).size());
    }

    @Test
    void buscaPorGenero() {
        Album a = crearAlbum();
        a.setGenero(unico("Genero "));
        dao.actualizar(a);
        assertEquals(1, dao.buscar(a.getGenero()).size());
    }

    @Test
    void elIntentoDeInyeccionSeTrataComoTexto() {
        assertTrue(dao.buscar("' OR 1=1 --").isEmpty());
    }

    @Test
    void elPromedioSoloCuentaPublicadas() {
        Album a = crearAlbum();
        assertTrue(dao.promedioPublicadas(a.getIdAlbum()).isEmpty());

        ResenaDAO resenas = new ResenaDAOImpl();
        resenas.insertar(resena(crearUsuario(Rol.USUARIO), a, 4, EstadoResena.PUBLICADA));
        resenas.insertar(resena(crearUsuario(Rol.USUARIO), a, 1, EstadoResena.BORRADOR));

        assertEquals(4.0, dao.promedioPublicadas(a.getIdAlbum()).orElseThrow(), 0.001);
    }
}
