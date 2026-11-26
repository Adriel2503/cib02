package com.soundboxd.dao;

import com.soundboxd.model.Cancion;
import com.soundboxd.util.ConexionBD;
import com.soundboxd.util.ReglaNegocioException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC de CancionDAO. */
public class CancionDAOImpl implements CancionDAO {

    private static final String NO_EXISTE = "La canción no existe.";

    @Override
    public int insertar(Cancion cancion) {
        String sql = """
                INSERT INTO CANCION (id_album, titulo, numero_pista, duracion_seg)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(ps, cancion);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new ReglaNegocioException("No se pudo guardar la canción.");
        } catch (SQLException e) {
            throw traducir(e, "No se pudo guardar la canción.");
        }
    }

    @Override
    public List<Cancion> listarPorAlbum(int idAlbum) {
        String sql = """
                SELECT id_cancion, id_album, titulo, numero_pista, duracion_seg
                FROM CANCION WHERE id_album = ?
                ORDER BY numero_pista
                """;
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idAlbum);
            try (ResultSet rs = ps.executeQuery()) {
                List<Cancion> canciones = new ArrayList<>();
                while (rs.next()) {
                    canciones.add(mapear(rs));
                }
                return canciones;
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudieron consultar las canciones.", e);
        }
    }

    @Override
    public void actualizar(Cancion cancion) {
        String sql = """
                UPDATE CANCION SET id_album = ?, titulo = ?, numero_pista = ?, duracion_seg = ?
                WHERE id_cancion = ?
                """;
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, cancion);
            ps.setInt(5, cancion.getIdCancion());
            if (ps.executeUpdate() == 0) {
                throw new ReglaNegocioException(NO_EXISTE);
            }
        } catch (SQLException e) {
            throw traducir(e, "No se pudo actualizar la canción.");
        }
    }

    @Override
    public void eliminar(int idCancion) {
        String sql = "DELETE FROM CANCION WHERE id_cancion = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCancion);
            if (ps.executeUpdate() == 0) {
                throw new ReglaNegocioException(NO_EXISTE);
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo eliminar la canción.", e);
        }
    }

    // ------------------------------------------------------------------ apoyo

    private static ReglaNegocioException traducir(SQLException e, String porDefecto) {
        if (ErroresSql.esDuplicado(e)) {
            return new ReglaNegocioException("Ya existe una canción con ese número de pista.", e);
        }
        if (ErroresSql.esRestriccion(e)) {
            return new ReglaNegocioException("El álbum no existe o los datos de la canción no son válidos.", e);
        }
        return new ReglaNegocioException(porDefecto, e);
    }

    private static void asignar(PreparedStatement ps, Cancion cancion) throws SQLException {
        ps.setInt(1, cancion.getIdAlbum());
        ps.setString(2, cancion.getTitulo());
        ps.setInt(3, cancion.getNumeroPista());
        ps.setInt(4, cancion.getDuracionSeg());
    }

    private static Cancion mapear(ResultSet rs) throws SQLException {
        Cancion c = new Cancion();
        c.setIdCancion(rs.getInt("id_cancion"));
        c.setIdAlbum(rs.getInt("id_album"));
        c.setTitulo(rs.getString("titulo"));
        c.setNumeroPista(rs.getInt("numero_pista"));
        c.setDuracionSeg(rs.getInt("duracion_seg"));
        return c;
    }
}
