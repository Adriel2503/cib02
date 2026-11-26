package com.soundboxd.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.soundboxd.model.Album;
import com.soundboxd.model.EstadoResena;
import com.soundboxd.model.Resena;
import com.soundboxd.model.Rol;
import com.soundboxd.model.Usuario;
import com.soundboxd.pruebas.PruebaConBase;
import com.soundboxd.util.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import java.util.List;

class ResenaDAOImplTest extends PruebaConBase {

    private final ResenaDAO dao = new ResenaDAOImpl();

    @Test
    void insertaLeeActualizaYElimina() {
        Usuario autor = crearUsuario(Rol.USUARIO);
        Album album = crearAlbum();
        int id = dao.insertar(resena(autor, album, 3, EstadoResena.BORRADOR));

        Resena leida = dao.buscarPorId(id).orElseThrow();
        assertEquals(autor.getNombreUsuario(), leida.getNombreUsuario());
        assertEquals(album.getTitulo(), leida.getTituloAlbum());
        assertTrue(dao.listarPublicadasPorAlbum(album.getIdAlbum()).isEmpty());

        leida.setCalificacion(5);
        leida.setEstado(EstadoResena.PUBLICADA);
        dao.actualizar(leida);
        assertEquals(5, dao.listarPublicadasPorAlbum(album.getIdAlbum()).get(0).getCalificacion());

        dao.eliminar(id);
        assertTrue(dao.buscarPorId(id).isEmpty());
    }

    @Test
    void rechazaLaSegundaResenaDelMismoAlbum() {
        Usuario autor = crearUsuario(Rol.USUARIO);
        Album album = crearAlbum();
        dao.insertar(resena(autor, album, 3, EstadoResena.PUBLICADA));
        assertThrows(ReglaNegocioException.class,
                () -> dao.insertar(resena(autor, album, 4, EstadoResena.PUBLICADA)));
    }

    @Test
    void cambiaElEstadoYListaPorEstado() {
        int id = dao.insertar(resena(crearUsuario(Rol.USUARIO), crearAlbum(), 2, EstadoResena.PUBLICADA));
        dao.cambiarEstado(id, EstadoResena.REPORTADA);
        assertTrue(dao.listarPorEstado(EstadoResena.REPORTADA).stream().anyMatch(r -> r.getIdResena() == id));
    }

    @Test
    void elFeedMuestraLasResenasDeLosSeguidos() {
        Usuario lector = crearUsuario(Rol.USUARIO);
        Usuario autor = crearUsuario(Rol.USUARIO);
        Album album = crearAlbum();
        new SeguimientoDAOImpl().seguir(lector.getIdUsuario(), autor.getIdUsuario());
        dao.insertar(resena(autor, album, 4, EstadoResena.PUBLICADA));

        List<Resena> feed = dao.listarActividadDeSeguidos(lector.getIdUsuario(), 10);

        assertEquals(1, feed.size());
        assertEquals(album.getTitulo(), feed.get(0).getTituloAlbum());
    }
}
