package com.soundboxd.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.soundboxd.model.Rol;
import com.soundboxd.model.Usuario;
import com.soundboxd.pruebas.PruebaConBase;
import com.soundboxd.util.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import java.util.List;

class SeguimientoDAOImplTest extends PruebaConBase {

    private final SeguimientoDAO dao = new SeguimientoDAOImpl();

    @Test
    void sigueConsultaYDejaDeSeguir() {
        int yo = crearUsuario(Rol.USUARIO).getIdUsuario();
        int otro = crearUsuario(Rol.USUARIO).getIdUsuario();

        dao.seguir(yo, otro);
        assertTrue(dao.existe(yo, otro));
        assertThrows(ReglaNegocioException.class, () -> dao.seguir(yo, otro));

        List<Usuario> seguidos = dao.listarSeguidos(yo);
        assertEquals(1, seguidos.size());
        assertNull(seguidos.get(0).getHashContrasena());

        dao.dejarDeSeguir(yo, otro);
        assertFalse(dao.existe(yo, otro));
    }
}
