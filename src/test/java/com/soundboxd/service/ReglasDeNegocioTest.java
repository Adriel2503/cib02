package com.soundboxd.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.soundboxd.dao.UsuarioDAOImpl;
import com.soundboxd.model.Album;
import com.soundboxd.model.Lista;
import com.soundboxd.model.Resena;
import com.soundboxd.model.Rol;
import com.soundboxd.model.Usuario;
import com.soundboxd.pruebas.PruebaConBase;
import com.soundboxd.util.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

/** Reglas que la guía pide comprobar, contra la base real. */
class ReglasDeNegocioTest extends PruebaConBase {

    private final ResenaService resenas = new ResenaServiceImpl();
    private final ListaService listas = new ListaServiceImpl();
    private final SocialService social = new SocialServiceImpl();

    @Test
    void rechazaCalificacionFueraDeRango() {
        Usuario u = crearUsuario(Rol.USUARIO);
        Album a = crearAlbum();
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> resenas.guardarBorrador(u, a.getIdAlbum(), 6, "texto"));
        assertEquals("La calificación debe estar entre 1 y 5.", e.getMessage());
    }

    @Test
    void rechazaUnaSegundaResenaConMensajeClaro() {
        Usuario u = crearUsuario(Rol.USUARIO);
        Album a = crearAlbum();
        Resena borrador = resenas.guardarBorrador(u, a.getIdAlbum(), 4, "texto");
        resenas.publicar(u, borrador.getIdResena());

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> resenas.guardarBorrador(u, a.getIdAlbum(), 5, "otra"));
        assertEquals("Ya escribiste una reseña de este álbum.", e.getMessage());
    }

    @Test
    void unAlbumInexistenteNoDejaDatosParciales() throws SQLException {
        Usuario u = crearUsuario(Rol.USUARIO);
        Album a = crearAlbum();

        assertThrows(ReglaNegocioException.class, () -> listas.crearListaConAlbumes(
                u, "Parcial", null, false, List.of(a.getIdAlbum(), Integer.MAX_VALUE)));

        assertEquals(0, contar("SELECT COUNT(*) FROM LISTA WHERE id_usuario = ?", u.getIdUsuario()));
        assertEquals(0, contar("SELECT COUNT(*) FROM LISTA_ALBUM WHERE id_album = ?", a.getIdAlbum()));
    }

    @Test
    void unaListaPrivadaSoloLaVeSuDueno() {
        Usuario dueno = crearUsuario(Rol.USUARIO);
        Usuario otro = crearUsuario(Rol.USUARIO);
        Album a = crearAlbum();
        Lista privada = listas.crearListaConAlbumes(dueno, "Privada", null, false, List.of(a.getIdAlbum()));

        assertEquals(1, listas.listarAlbumes(dueno, privada.getIdLista()).size());
        assertThrows(ReglaNegocioException.class, () -> listas.listarAlbumes(otro, privada.getIdLista()));
    }

    @Test
    void noSePuedeSeguirUnaCuentaDesactivada() throws SQLException {
        Usuario yo = crearUsuario(Rol.USUARIO);
        Usuario baja = crearUsuario(Rol.USUARIO);
        new UsuarioDAOImpl().desactivar(baja.getIdUsuario());

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> social.seguir(yo, baja.getIdUsuario()));

        assertEquals("El usuario no existe.", e.getMessage());
        assertEquals(0, contar("SELECT COUNT(*) FROM SEGUIMIENTO WHERE id_seguidor = ?", yo.getIdUsuario()));
    }

    @Test
    void nadiePuedeSeguirseASiMismo() {
        Usuario u = crearUsuario(Rol.USUARIO);
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> social.seguir(u, u.getIdUsuario()));
        assertEquals("No puedes seguirte a ti mismo.", e.getMessage());
    }
}
