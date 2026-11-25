package com.soundboxd.view.falso;

import com.soundboxd.model.Album;
import com.soundboxd.model.Cancion;
import com.soundboxd.model.EstadoResena;
import com.soundboxd.model.Lista;
import com.soundboxd.model.Resena;
import com.soundboxd.model.Rol;
import com.soundboxd.model.Usuario;
import com.soundboxd.util.ReglaNegocioException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Datos de ejemplo en memoria, SOLO para desarrollar el frontend antes de que existan
 * los servicios reales. Se pierden al cerrar la aplicación. Las claves son de mentira
 * y nunca salen de este archivo (los Usuario falsos no llevan hash).
 *
 * Cuentas de prueba:  ana / ana12345 (USUARIO), luis / luis12345 (USUARIO), mod / mod12345 (MODERADOR)
 */
final class DatosFalsos {

    static final List<Usuario> USUARIOS = new ArrayList<>();
    static final Map<Integer, String> CLAVES = new HashMap<>();
    static final List<Album> ALBUMES = new ArrayList<>();
    static final List<Cancion> CANCIONES = new ArrayList<>();
    static final List<Resena> RESENAS = new ArrayList<>();
    static final List<Lista> LISTAS = new ArrayList<>();
    static final Map<Integer, List<Integer>> ALBUMES_DE_LISTA = new HashMap<>();
    /** Pares "idSeguidor>idSeguido". */
    static final Set<String> SEGUIMIENTOS = new HashSet<>();

    private static int sigUsuario = 1;
    private static int sigAlbum = 1;
    private static int sigCancion = 1;
    private static int sigResena = 1;
    private static int sigLista = 1;

    static {
        Usuario ana = nuevoUsuario("ana", "ana@correo.pe", Rol.USUARIO, "ana12345");
        Usuario luis = nuevoUsuario("luis", "luis@correo.pe", Rol.USUARIO, "luis12345");
        Usuario mod = nuevoUsuario("mod", "mod@correo.pe", Rol.MODERADOR, "mod12345");

        int abbey = nuevoAlbum("Abbey Road", "The Beatles", 1969, "Rock");
        int kind = nuevoAlbum("Kind of Blue", "Miles Davis", 1959, "Jazz");
        int thriller = nuevoAlbum("Thriller", "Michael Jackson", 1982, "Pop");
        int rumours = nuevoAlbum("Rumours", "Fleetwood Mac", 1977, "Rock");
        int tpab = nuevoAlbum("To Pimp a Butterfly", "Kendrick Lamar", 2015, "Hip hop");
        int ram = nuevoAlbum("Random Access Memories", "Daft Punk", 2013, "Electrónica");

        nuevaCancion(abbey, 1, "Come Together", 259);
        nuevaCancion(abbey, 2, "Something", 183);
        nuevaCancion(abbey, 3, "Here Comes the Sun", 185);
        nuevaCancion(kind, 1, "So What", 562);
        nuevaCancion(kind, 2, "Freddie Freeloader", 584);
        nuevaCancion(kind, 3, "Blue in Green", 337);
        nuevaCancion(thriller, 1, "Wanna Be Startin' Somethin'", 363);
        nuevaCancion(thriller, 2, "Billie Jean", 294);
        nuevaCancion(thriller, 3, "Beat It", 258);
        nuevaCancion(rumours, 1, "Second Hand News", 163);
        nuevaCancion(rumours, 2, "Go Your Own Way", 218);
        nuevaCancion(rumours, 3, "Dreams", 257);
        nuevaCancion(tpab, 1, "Wesley's Theory", 287);
        nuevaCancion(tpab, 2, "King Kunta", 234);
        nuevaCancion(tpab, 3, "Alright", 219);
        nuevaCancion(ram, 1, "Give Life Back to Music", 274);
        nuevaCancion(ram, 2, "Get Lucky", 369);
        nuevaCancion(ram, 3, "Instant Crush", 337);

        nuevaResena(ana, abbey, 5, "Un clásico absoluto.", EstadoResena.PUBLICADA, 6);
        nuevaResena(luis, abbey, 4, "Gran cierre con el medley.", EstadoResena.PUBLICADA, 5);
        nuevaResena(ana, kind, 5, "Perfecto para concentrarse.", EstadoResena.PUBLICADA, 4);
        nuevaResena(luis, thriller, 4, "Cada pista funciona.", EstadoResena.PUBLICADA, 3);
        nuevaResena(ana, thriller, 3, "Borrador pendiente.", EstadoResena.BORRADOR, 1);
        nuevaResena(mod, rumours, 5, "Producción impecable.", EstadoResena.PUBLICADA, 2);
        nuevaResena(luis, tpab, 3, "Denso, requiere varias escuchas.", EstadoResena.REPORTADA, 2);
        nuevaResena(ana, ram, 4, "Elegante y bien producido.", EstadoResena.PUBLICADA, 1);

        Lista estudiar = new Lista();
        estudiar.setIdLista(sigLista++);
        estudiar.setIdUsuario(ana.getIdUsuario());
        estudiar.setNombre("Para estudiar");
        estudiar.setDescripcion("Álbumes tranquilos para concentrarse");
        estudiar.setEsPublica(true);
        estudiar.setFechaCreacion(LocalDateTime.now().minusDays(3));
        LISTAS.add(estudiar);
        ALBUMES_DE_LISTA.put(estudiar.getIdLista(), new ArrayList<>(List.of(kind, rumours)));

        SEGUIMIENTOS.add(claveSeguimiento(ana.getIdUsuario(), luis.getIdUsuario()));
    }

    private DatosFalsos() { }

    // ---------- altas de ejemplo ----------

    static Usuario nuevoUsuario(String nombre, String correo, Rol rol, String clave) {
        Usuario u = new Usuario();
        u.setIdUsuario(sigUsuario++);
        u.setNombreUsuario(nombre);
        u.setCorreo(correo);
        u.setRol(rol);
        u.setActivo(true);
        u.setFechaRegistro(LocalDateTime.now());
        USUARIOS.add(u);
        CLAVES.put(u.getIdUsuario(), clave);
        return u;
    }

    static int nuevoAlbum(String titulo, String artista, int anio, String genero) {
        Album a = new Album();
        a.setIdAlbum(sigAlbum++);
        a.setTitulo(titulo);
        a.setIdArtista(a.getIdAlbum());
        a.setNombreArtista(artista);
        a.setAnio(anio);
        a.setGenero(genero);
        ALBUMES.add(a);
        return a.getIdAlbum();
    }

    static void nuevaCancion(int idAlbum, int pista, String titulo, int segundos) {
        Cancion c = new Cancion();
        c.setIdCancion(sigCancion++);
        c.setIdAlbum(idAlbum);
        c.setNumeroPista(pista);
        c.setTitulo(titulo);
        c.setDuracionSeg(segundos);
        CANCIONES.add(c);
    }

    static Resena nuevaResena(Usuario autor, int idAlbum, int calificacion, String texto,
                              EstadoResena estado, int diasAtras) {
        Resena r = new Resena();
        r.setIdResena(sigResena++);
        r.setIdUsuario(autor.getIdUsuario());
        r.setNombreUsuario(autor.getNombreUsuario());
        r.setIdAlbum(idAlbum);
        r.setCalificacion(calificacion);
        r.setTexto(texto);
        r.setEstado(estado);
        r.setFecha(LocalDateTime.now().minusDays(diasAtras));
        RESENAS.add(r);
        return r;
    }

    static int nuevoIdAlbum() { return sigAlbum++; }

    static int nuevoIdLista() { return sigLista++; }

    static int nuevoIdUsuario() { return sigUsuario++; }

    static String claveSeguimiento(int idSeguidor, int idSeguido) {
        return idSeguidor + ">" + idSeguido;
    }

    // ---------- búsquedas (lanzan ReglaNegocioException si no existe) ----------

    static Usuario usuarioPorNombre(String nombre) {
        for (Usuario u : USUARIOS) {
            if (u.getNombreUsuario().equalsIgnoreCase(nombre)) {
                return u;
            }
        }
        return null;
    }

    static Usuario usuarioPorId(int id) {
        for (Usuario u : USUARIOS) {
            if (u.getIdUsuario() == id) {
                return u;
            }
        }
        throw new ReglaNegocioException("El usuario no existe.");
    }

    static Album albumPorId(int id) {
        for (Album a : ALBUMES) {
            if (a.getIdAlbum() == id) {
                return a;
            }
        }
        throw new ReglaNegocioException("El álbum no existe.");
    }

    static Resena resenaPorId(int id) {
        for (Resena r : RESENAS) {
            if (r.getIdResena() == id) {
                return r;
            }
        }
        throw new ReglaNegocioException("La reseña no existe.");
    }

    static Lista listaPorId(int id) {
        for (Lista l : LISTAS) {
            if (l.getIdLista() == id) {
                return l;
            }
        }
        throw new ReglaNegocioException("La lista no existe.");
    }
}
