package com.soundboxd.view.falso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.soundboxd.model.Album;
import com.soundboxd.model.EstadoResena;
import com.soundboxd.model.Lista;
import com.soundboxd.model.Resena;
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
    private final ListaServiceFalso listas = new ListaServiceFalso();
    private final SocialServiceFalso social = new SocialServiceFalso();
    private final ModeracionServiceFalso moderacion = new ModeracionServiceFalso();

    /** Crea un usuario nuevo y distinto en cada prueba, para no depender del orden de ejecución. */
    private Usuario usuarioNuevo(String nombre) {
        auth.registrar(nombre, nombre + "@correo.pe", "clave12345");
        return auth.iniciarSesion(nombre, "clave12345");
    }

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

    @Test
    void borradorSePublicaYApareceEnElAlbum() {
        Usuario u = usuarioNuevo("t_publica");
        Resena borrador = resenas.guardarBorrador(u, 4, 5, "Excelente");
        assertEquals(EstadoResena.BORRADOR, borrador.getEstado());
        assertEquals(0L, resenas.listarPorAlbum(4).stream().filter(r -> r.getIdUsuario() == u.getIdUsuario()).count());
        resenas.publicar(u, borrador.getIdResena());
        assertEquals(1L, resenas.listarPorAlbum(4).stream().filter(r -> r.getIdUsuario() == u.getIdUsuario()).count());
    }

    @Test
    void segundaReseniaDelMismoAlbumSeRechazaSiLaPrimeraYaEstaPublicada() {
        Usuario u = usuarioNuevo("t_doble");
        resenas.publicar(u, resenas.guardarBorrador(u, 2, 3, "ok").getIdResena());
        assertThrows(ReglaNegocioException.class, () -> resenas.guardarBorrador(u, 2, 4, "otra"));
    }

    @Test
    void listaSeCreaAgregaReordenaYElimina() {
        Usuario u = usuarioNuevo("t_listas");
        Lista l = listas.crearListaConAlbumes(u, "Mi lista", "", false, List.of());
        listas.agregarAlbum(u, l.getIdLista(), 1);
        listas.agregarAlbum(u, l.getIdLista(), 2);
        assertThrows(ReglaNegocioException.class, () -> listas.agregarAlbum(u, l.getIdLista(), 1));
        listas.reordenar(u, l.getIdLista(), List.of(2, 1));
        assertEquals(2, listas.listarAlbumes(l.getIdLista()).get(0).getIdAlbum());
        assertThrows(ReglaNegocioException.class, () -> listas.reordenar(u, l.getIdLista(), List.of(2)));
        listas.eliminarLista(u, l.getIdLista());
        assertEquals(0, listas.listarMisListas(u).size());
    }

    @Test
    void otroUsuarioNoPuedeModificarMiLista() {
        Usuario dueno = usuarioNuevo("t_dueno");
        Usuario intruso = usuarioNuevo("t_intruso");
        Lista l = listas.crearListaConAlbumes(dueno, "Privada", "", false, List.of());
        assertThrows(ReglaNegocioException.class, () -> listas.agregarAlbum(intruso, l.getIdLista(), 1));
    }

    @Test
    void seguirYDejarDeSeguir() {
        Usuario a = usuarioNuevo("t_seguidor");
        Usuario b = usuarioNuevo("t_seguido");
        social.seguir(a, b.getIdUsuario());
        assertEquals(1, social.listarSeguidos(a).size());
        assertThrows(ReglaNegocioException.class, () -> social.seguir(a, b.getIdUsuario()));
        assertThrows(ReglaNegocioException.class, () -> social.seguir(a, a.getIdUsuario()));
        social.dejarDeSeguir(a, b.getIdUsuario());
        assertEquals(0, social.listarSeguidos(a).size());
    }

    @Test
    void reportarYModerar() {
        Usuario autor = usuarioNuevo("t_autor");
        Usuario lector = usuarioNuevo("t_lector");
        Usuario mod = auth.iniciarSesion("mod", "mod12345");
        Resena r = resenas.publicar(autor, resenas.guardarBorrador(autor, 6, 2, "meh").getIdResena());
        moderacion.reportar(lector, r.getIdResena());
        assertTrue(moderacion.listarReportadas(mod).stream().anyMatch(x -> x.getIdResena() == r.getIdResena()));
        assertThrows(ReglaNegocioException.class, () -> moderacion.listarReportadas(lector));
        moderacion.ocultar(mod, r.getIdResena());
        assertEquals(EstadoResena.OCULTA, r.getEstado());
        moderacion.restituir(mod, r.getIdResena());
        assertEquals(EstadoResena.PUBLICADA, r.getEstado());
    }
}
