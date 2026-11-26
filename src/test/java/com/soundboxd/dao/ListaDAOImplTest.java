package com.soundboxd.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.soundboxd.model.Lista;
import com.soundboxd.model.ListaAlbum;
import com.soundboxd.model.Rol;
import com.soundboxd.pruebas.PruebaConBase;
import com.soundboxd.util.Transaccion;
import org.junit.jupiter.api.Test;

import java.util.List;

class ListaDAOImplTest extends PruebaConBase {

    private final ListaDAO dao = new ListaDAOImpl();

    @Test
    void creaLeeReordenaQuitaYElimina() {
        int primero = crearAlbum().getIdAlbum();
        int segundo = crearAlbum().getIdAlbum();
        Lista lista = new Lista();
        lista.setIdUsuario(crearUsuario(Rol.USUARIO).getIdUsuario());
        lista.setNombre("Mi lista");
        lista.setEsPublica(true);

        int id = new Transaccion().ejecutar(conexion -> {
            int nueva = dao.insertar(conexion, lista);
            dao.agregarAlbum(conexion, nueva, primero, 1);
            dao.agregarAlbum(conexion, nueva, segundo, 2);
            return nueva;
        }, "No se pudo crear la lista.");

        assertEquals("Mi lista", dao.buscarPorId(id).orElseThrow().getNombre());
        assertEquals(1, dao.listarPorUsuario(lista.getIdUsuario()).size());

        lista.setIdLista(id);
        lista.setNombre("Nombre nuevo");
        lista.setEsPublica(false);
        dao.actualizar(lista);
        Lista actualizada = dao.buscarPorId(id).orElseThrow();
        assertEquals("Nombre nuevo", actualizada.getNombre());
        assertFalse(actualizada.isEsPublica());

        dao.actualizarPosicion(id, segundo, 1);
        dao.actualizarPosicion(id, primero, 2);
        List<Integer> orden = dao.listarAlbumes(id).stream().map(ListaAlbum::getIdAlbum).toList();
        assertEquals(List.of(segundo, primero), orden);

        dao.quitarAlbum(id, primero);
        assertEquals(1, dao.listarAlbumes(id).size());

        dao.eliminar(id);
        assertTrue(dao.buscarPorId(id).isEmpty());
    }
}
