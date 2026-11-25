package com.soundboxd.view.falso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.soundboxd.model.Album;
import com.soundboxd.model.Rol;
import com.soundboxd.model.Usuario;
import com.soundboxd.util.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Pruebas de los servicios con datos falsos. No abren ventanas ni tocan la base de datos.
 * Ninguna modifica los datos de ejemplo, así que no dependen del orden de ejecución.
 */
class ServiciosFalsosTest {

    private final AuthServiceFalso auth = new AuthServiceFalso();
    private final CatalogoServiceFalso catalogo = new CatalogoServiceFalso();
    private final ResenaServiceFalso resenas = new ResenaServiceFalso();

    @Test
    void loginCorrectoDevuelveElUsuario() {
        Usuario u = auth.iniciarSesion("ana", "ana12345");
        assertEquals("ana", u.getNombreUsuario());
        assertEquals(Rol.USUARIO, u.getRol());
    }

    @Test
    void loginIncorrectoUsaMensajeGenerico() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> auth.iniciarSesion("ana", "clave-mala"));
        assertEquals("Usuario o contraseña incorrectos", e.getMessage());
    }

    @Test
    void usuarioInexistenteTambienUsaMensajeGenerico() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> auth.iniciarSesion("nadie", "x"));
        assertEquals("Usuario o contraseña incorrectos", e.getMessage());
    }

    @Test
    void registroRechazaClaveCorta() {
        assertThrows(ReglaNegocioException.class,
                () -> auth.registrar("nuevo_corto", "nuevo@correo.pe", "123"));
    }

    @Test
    void busquedaFiltraPorTituloSinDistinguirMayusculas() {
        List<Album> resultado = catalogo.buscarAlbumes("ABBEY");
        assertEquals(1, resultado.size());
        assertEquals("Abbey Road", resultado.get(0).getTitulo());
    }

    @Test
    void busquedaVaciaDevuelveTodoElCatalogo() {
        assertEquals(6, catalogo.buscarAlbumes("").size());
    }

    @Test
    void promedioIgnoraReseniasNoPublicadas() {
        // Thriller tiene una reseña PUBLICADA de 4 y un BORRADOR de 3: el promedio solo cuenta la primera.
        int id = catalogo.buscarAlbumes("thriller").get(0).getIdAlbum();
        assertTrue(Math.abs(catalogo.promedioAlbum(id) - 4.0) < 0.001);
    }

    @Test
    void calificacionFueraDeRangoSeRechaza() {
        Usuario ana = auth.iniciarSesion("ana", "ana12345");
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> resenas.guardarBorrador(ana, 1, 6, "texto"));
        assertEquals("La calificación debe estar entre 1 y 5.", e.getMessage());
    }

    @Test
    void visitanteNoPuedeEscribirResenias() {
        assertThrows(ReglaNegocioException.class, () -> resenas.guardarBorrador(null, 1, 5, "texto"));
    }

    @Test
    void soloElModeradorPuedeCrearAlbumes() {
        Usuario ana = auth.iniciarSesion("ana", "ana12345");
        assertThrows(ReglaNegocioException.class, () -> catalogo.crearAlbum(ana, new Album()));
    }
}
