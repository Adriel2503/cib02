package com.soundboxd.dao;

import com.soundboxd.model.Album;
import com.soundboxd.util.ConexionBD;
import com.soundboxd.util.ReglaNegocioException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

/** Implementación JDBC de AlbumDAO. */
public class AlbumDAOImpl implements AlbumDAO {

    private static final String ERROR_CONSULTA = "No se pudo consultar el catálogo.";
    private static final String NO_EXISTE = "El álbum no existe.";
    private static final String DATOS_INVALIDOS = "El artista no existe o los datos del álbum no son válidos.";

    @Override
    public int insertar(Album album) {
        String sql = """
                INSERT INTO ALBUM (titulo, id_artista, anio, genero, portada)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(ps, album);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new ReglaNegocioException("No se pudo guardar el álbum.");
        } catch (SQLException e) {
            if (ErroresSql.esRestriccion(e)) {
                throw new ReglaNegocioException(DATOS_INVALIDOS, e);
            }
            throw new ReglaNegocioException("No se pudo guardar el álbum.", e);
        }
    }

    @Override
    public Optional<Album> buscarPorId(int idAlbum) {
        String sql = """
                SELECT a.id_album, a.titulo, a.id_artista, ar.nombre AS nombre_artista,
                       a.anio, a.genero, a.portada
                FROM ALBUM a INNER JOIN ARTISTA ar ON ar.id_artista = a.id_artista
                WHERE a.id_album = ?
                """;
        return consultar(sql, idAlbum).stream().findFirst();
    }

    @Override
    public List<Album> buscar(String texto) {
        String sql = """
                SELECT a.id_album, a.titulo, a.id_artista, ar.nombre AS nombre_artista,
                       a.anio, a.genero, a.portada
                FROM ALBUM a INNER JOIN ARTISTA ar ON ar.id_artista = a.id_artista
                WHERE a.titulo LIKE ? OR ar.nombre LIKE ?
                ORDER BY a.titulo
                """;
        // El patrón viaja como parámetro; los comodines escritos por el usuario se tratan como texto.
        String patron = "%" + escaparLike(texto) + "%";
        return consultar(sql, patron, patron);
    }

    @Override
    public List<Album> listarTodos() {
        String sql = """
                SELECT a.id_album, a.titulo, a.id_artista, ar.nombre AS nombre_artista,
                       a.anio, a.genero, a.portada
                FROM ALBUM a INNER JOIN ARTISTA ar ON ar.id_artista = a.id_artista
                ORDER BY a.titulo
                """;
        return consultar(sql);
    }

    @Override
    public void actualizar(Album album) {
        String sql = """
                UPDATE ALBUM SET titulo = ?, id_artista = ?, anio = ?, genero = ?, portada = ?
                WHERE id_album = ?
                """;
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, album);
            ps.setInt(6, album.getIdAlbum());
            if (ps.executeUpdate() == 0) {
                throw new ReglaNegocioException(NO_EXISTE);
            }
        } catch (SQLException e) {
            if (ErroresSql.esRestriccion(e)) {
                throw new ReglaNegocioException(DATOS_INVALIDOS, e);
            }
            throw new ReglaNegocioException("No se pudo actualizar el álbum.", e);
        }
    }

    @Override
    public void eliminar(int idAlbum) {
        String sql = "DELETE FROM ALBUM WHERE id_album = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idAlbum);
            if (ps.executeUpdate() == 0) {
                throw new ReglaNegocioException(NO_EXISTE);
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo eliminar el álbum.", e);
        }
    }

    @Override
    public OptionalDouble promedioPublicadas(int idAlbum) {
        String sql = "SELECT promedio_calificacion FROM vw_promedio_album WHERE id_album = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idAlbum);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal promedio = rs.getBigDecimal(1);
                    if (promedio != null) {
                        return OptionalDouble.of(promedio.doubleValue());
                    }
                }
                return OptionalDouble.empty();
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo calcular el promedio.", e);
        }
    }

    // ------------------------------------------------------------------ apoyo

    private static String escaparLike(String texto) {
        return texto.replace("[", "[[]").replace("%", "[%]").replace("_", "[_]");
    }

    private static void asignar(PreparedStatement ps, Album album) throws SQLException {
        ps.setString(1, album.getTitulo());
        ps.setInt(2, album.getIdArtista());
        ps.setInt(3, album.getAnio());
        ps.setString(4, album.getGenero());
        ps.setString(5, album.getPortada());
    }

    private List<Album> consultar(String sql, Object... parametros) {
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Album> albumes = new ArrayList<>();
                while (rs.next()) {
                    albumes.add(mapear(rs));
                }
                return albumes;
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException(ERROR_CONSULTA, e);
        }
    }

    private static Album mapear(ResultSet rs) throws SQLException {
        Album a = new Album();
        a.setIdAlbum(rs.getInt("id_album"));
        a.setTitulo(rs.getString("titulo"));
        a.setIdArtista(rs.getInt("id_artista"));
        a.setNombreArtista(rs.getString("nombre_artista"));
        a.setAnio(rs.getInt("anio"));
        a.setGenero(rs.getString("genero"));
        a.setPortada(rs.getString("portada"));
        return a;
    }
}
