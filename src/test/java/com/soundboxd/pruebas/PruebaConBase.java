package com.soundboxd.pruebas;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.soundboxd.dao.AlbumDAOImpl;
import com.soundboxd.dao.ArtistaDAOImpl;
import com.soundboxd.dao.UsuarioDAOImpl;
import com.soundboxd.model.Album;
import com.soundboxd.model.Artista;
import com.soundboxd.model.EstadoResena;
import com.soundboxd.model.Resena;
import com.soundboxd.model.Rol;
import com.soundboxd.model.Usuario;
import com.soundboxd.util.ConexionBD;
import com.soundboxd.util.ReglaNegocioException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base de las pruebas contra SQL Server. Sin db.properties o sin base, las pruebas se omiten
 * (revisa el contador "Skipped" de mvn test). Cada prueba borra lo que crea.
 */
public abstract class PruebaConBase {

    private static final AtomicInteger SECUENCIA = new AtomicInteger();

    private final List<Integer> usuariosCreados = new ArrayList<>();
    private final List<Integer> artistasCreados = new ArrayList<>();

    @BeforeAll
    static void exigirBase() {
        try (Connection c = ConexionBD.obtener()) {
            assumeTrue(c.isValid(5));
        } catch (ReglaNegocioException | SQLException e) {
            assumeTrue(false, "Sin base de datos: " + e.getMessage());
        }
    }

    protected static String unico(String prefijo) {
        return prefijo + (System.nanoTime() % 1_000_000_000L) + SECUENCIA.incrementAndGet();
    }

    protected Usuario crearUsuario(Rol rol) {
        Usuario u = new Usuario();
        u.setNombreUsuario(unico("t_"));
        u.setCorreo(u.getNombreUsuario() + "@example.com");
        u.setHashContrasena("hash-de-prueba");
        u.setRol(rol);
        u.setActivo(true);
        u.setIdUsuario(new UsuarioDAOImpl().insertar(u));
        usuariosCreados.add(u.getIdUsuario());
        return u;
    }

    protected Artista crearArtista() {
        Artista a = new Artista();
        a.setNombre(unico("Artista "));
        a.setPais("Perú");
        a.setIdArtista(new ArtistaDAOImpl().insertar(a));
        artistasCreados.add(a.getIdArtista());
        return a;
    }

    protected Album crearAlbum() {
        Album a = new Album();
        a.setTitulo(unico("Album "));
        a.setIdArtista(crearArtista().getIdArtista());
        a.setAnio(2020);
        a.setGenero("Rock");
        a.setIdAlbum(new AlbumDAOImpl().insertar(a));
        return a;
    }

    protected static Resena resena(Usuario autor, Album album, int calificacion, EstadoResena estado) {
        Resena r = new Resena();
        r.setIdUsuario(autor.getIdUsuario());
        r.setIdAlbum(album.getIdAlbum());
        r.setCalificacion(calificacion);
        r.setTexto("Texto de prueba");
        r.setEstado(estado);
        return r;
    }

    protected static int contar(String sql, int parametro) throws SQLException {
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    @AfterEach
    void limpiar() throws SQLException {
        // Los usuarios arrastran en cascada sus reseñas y listas; los álbumes, sus canciones.
        for (int id : usuariosCreados) {
            borrar("DELETE FROM SEGUIMIENTO WHERE id_seguido = ?", id);
            borrar("DELETE FROM USUARIO WHERE id_usuario = ?", id);
        }
        for (int id : artistasCreados) {
            borrar("DELETE FROM ALBUM WHERE id_artista = ?", id);
            borrar("DELETE FROM ARTISTA WHERE id_artista = ?", id);
        }
    }

    private static void borrar(String sql, int id) throws SQLException {
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
