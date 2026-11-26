package com.soundboxd.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.soundboxd.model.Cancion;
import com.soundboxd.pruebas.PruebaConBase;
import com.soundboxd.util.ReglaNegocioException;
import org.junit.jupiter.api.Test;

class CancionDAOImplTest extends PruebaConBase {

    private final CancionDAO dao = new CancionDAOImpl();

    @Test
    void insertaLeeActualizaYElimina() {
        int idAlbum = crearAlbum().getIdAlbum();
        Cancion c = cancion(idAlbum, 1);
        c.setIdCancion(dao.insertar(c));

        c.setTitulo("Otro título");
        dao.actualizar(c);
        assertEquals("Otro título", dao.listarPorAlbum(idAlbum).get(0).getTitulo());

        dao.eliminar(c.getIdCancion());
        assertTrue(dao.listarPorAlbum(idAlbum).isEmpty());
    }

    @Test
    void rechazaNumeroDePistaRepetido() {
        int idAlbum = crearAlbum().getIdAlbum();
        dao.insertar(cancion(idAlbum, 1));
        assertThrows(ReglaNegocioException.class, () -> dao.insertar(cancion(idAlbum, 1)));
    }

    private static Cancion cancion(int idAlbum, int pista) {
        Cancion c = new Cancion();
        c.setIdAlbum(idAlbum);
        c.setTitulo("Pista " + pista);
        c.setNumeroPista(pista);
        c.setDuracionSeg(200);
        return c;
    }
}
