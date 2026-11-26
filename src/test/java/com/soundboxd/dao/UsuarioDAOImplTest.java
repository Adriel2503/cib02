package com.soundboxd.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.soundboxd.model.Rol;
import com.soundboxd.model.Usuario;
import com.soundboxd.pruebas.PruebaConBase;
import com.soundboxd.util.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import java.util.List;

class UsuarioDAOImplTest extends PruebaConBase {

    private final UsuarioDAO dao = new UsuarioDAOImpl();

    @Test
    void insertaLeeActualizaYDesactiva() {
        Usuario u = crearUsuario(Rol.USUARIO);
        assertEquals(u.getNombreUsuario(), dao.buscarPorId(u.getIdUsuario()).orElseThrow().getNombreUsuario());

        u.setCorreo("nuevo_" + u.getCorreo());
        dao.actualizar(u);
        assertEquals(u.getCorreo(), dao.buscarPorId(u.getIdUsuario()).orElseThrow().getCorreo());

        dao.desactivar(u.getIdUsuario());
        assertFalse(dao.buscarPorId(u.getIdUsuario()).orElseThrow().isActivo());
    }

    @Test
    void rechazaNombreRepetido() {
        Usuario u = crearUsuario(Rol.USUARIO);
        assertThrows(ReglaNegocioException.class, () -> dao.insertar(u));
    }

    @Test
    void laBusquedaNoExponeCorreoNiHash() {
        Usuario u = crearUsuario(Rol.USUARIO);
        List<Usuario> encontrados = dao.buscarPorTexto(u.getNombreUsuario());
        assertEquals(1, encontrados.size());
        assertNull(encontrados.get(0).getCorreo());
        assertNull(encontrados.get(0).getHashContrasena());
    }
}
