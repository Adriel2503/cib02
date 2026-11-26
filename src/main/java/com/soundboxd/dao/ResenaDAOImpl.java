package com.soundboxd.dao;

import com.soundboxd.model.EstadoResena;
import com.soundboxd.model.Resena;
import com.soundboxd.util.ConexionBD;
import com.soundboxd.util.ReglaNegocioException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Implementación JDBC de ResenaDAO. */
public class ResenaDAOImpl implements ResenaDAO {

    private static final String ERROR_CONSULTA = "No se pudieron consultar las reseñas.";
    private static final String NO_EXISTE = "La reseña no existe.";

    @Override
    public int insertar(Resena resena) {
        String sql = """
                INSERT INTO RESENA (id_usuario, id_album, calificacion, texto, estado, fecha)
                VALUES (?, ?, ?, ?, ?, COALESCE(?, SYSDATETIME()))
                """;
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, resena.getIdUsuario());
            ps.setInt(2, resena.getIdAlbum());
            ps.setInt(3, resena.getCalificacion());
            ps.setString(4, resena.getTexto());
            ps.setString(5, resena.getEstado().name());
            ps.setTimestamp(6, aTimestamp(resena));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new ReglaNegocioException("No se pudo guardar la reseña.");
        } catch (SQLException e) {
            if (ErroresSql.esDuplicado(e)) {
                throw new ReglaNegocioException("Ya escribiste una reseña de este álbum.", e);
            }
            throw new ReglaNegocioException("No se pudo guardar la reseña.", e);
        }
    }

    @Override
    public Optional<Resena> buscarPorId(int idResena) {
        String sql = """
                SELECT r.id_resena, r.id_usuario, u.nombre_usuario, r.id_album, al.titulo AS titulo_album,
                       r.calificacion, r.texto, r.estado, r.fecha
                FROM RESENA r INNER JOIN USUARIO u ON u.id_usuario = r.id_usuario
                INNER JOIN ALBUM al ON al.id_album = r.id_album
                WHERE r.id_resena = ?
                """;
        return consultar(sql, idResena).stream().findFirst();
    }

    @Override
    public Optional<Resena> buscarPorUsuarioYAlbum(int idUsuario, int idAlbum) {
        String sql = """
                SELECT r.id_resena, r.id_usuario, u.nombre_usuario, r.id_album, al.titulo AS titulo_album,
                       r.calificacion, r.texto, r.estado, r.fecha
                FROM RESENA r INNER JOIN USUARIO u ON u.id_usuario = r.id_usuario
                INNER JOIN ALBUM al ON al.id_album = r.id_album
                WHERE r.id_usuario = ? AND r.id_album = ?
                """;
        return consultar(sql, idUsuario, idAlbum).stream().findFirst();
    }

    @Override
    public List<Resena> listarPublicadasPorAlbum(int idAlbum) {
        String sql = """
                SELECT r.id_resena, r.id_usuario, u.nombre_usuario, r.id_album, al.titulo AS titulo_album,
                       r.calificacion, r.texto, r.estado, r.fecha
                FROM RESENA r INNER JOIN USUARIO u ON u.id_usuario = r.id_usuario
                INNER JOIN ALBUM al ON al.id_album = r.id_album
                WHERE r.id_album = ? AND r.estado = ?
                ORDER BY r.fecha DESC, r.id_resena DESC
                """;
        return consultar(sql, idAlbum, EstadoResena.PUBLICADA.name());
    }

    @Override
    public List<Resena> listarPorUsuario(int idUsuario) {
        String sql = """
                SELECT r.id_resena, r.id_usuario, u.nombre_usuario, r.id_album, al.titulo AS titulo_album,
                       r.calificacion, r.texto, r.estado, r.fecha
                FROM RESENA r INNER JOIN USUARIO u ON u.id_usuario = r.id_usuario
                INNER JOIN ALBUM al ON al.id_album = r.id_album
                WHERE r.id_usuario = ?
                ORDER BY r.fecha DESC, r.id_resena DESC
                """;
        return consultar(sql, idUsuario);
    }

    @Override
    public List<Resena> listarActividadDeSeguidos(int idSeguidor, int limite) {
        // La vista solo contiene reseñas publicadas, por eso el estado se fija como literal.
        String sql = """
                SELECT TOP (?) v.id_resena, v.id_usuario, v.nombre_usuario, v.id_album, v.titulo_album,
                       v.calificacion, v.texto, 'PUBLICADA' AS estado, v.fecha
                FROM vw_actividad_reciente v
                INNER JOIN SEGUIMIENTO s ON s.id_seguido = v.id_usuario
                INNER JOIN USUARIO u ON u.id_usuario = v.id_usuario
                WHERE s.id_seguidor = ? AND u.activo = 1
                ORDER BY v.fecha DESC, v.id_resena DESC
                """;
        return consultar(sql, limite, idSeguidor);
    }

    @Override
    public List<Resena> listarPorEstado(EstadoResena estado) {
        String sql = """
                SELECT r.id_resena, r.id_usuario, u.nombre_usuario, r.id_album, al.titulo AS titulo_album,
                       r.calificacion, r.texto, r.estado, r.fecha
                FROM RESENA r INNER JOIN USUARIO u ON u.id_usuario = r.id_usuario
                INNER JOIN ALBUM al ON al.id_album = r.id_album
                WHERE r.estado = ?
                ORDER BY r.fecha DESC, r.id_resena DESC
                """;
        return consultar(sql, estado.name());
    }

    @Override
    public void actualizar(Resena resena) {
        String sql = """
                UPDATE RESENA
                SET calificacion = ?, texto = ?, estado = ?, fecha = COALESCE(?, fecha)
                WHERE id_resena = ?
                """;
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, resena.getCalificacion());
            ps.setString(2, resena.getTexto());
            ps.setString(3, resena.getEstado().name());
            ps.setTimestamp(4, aTimestamp(resena));
            ps.setInt(5, resena.getIdResena());
            if (ps.executeUpdate() == 0) {
                throw new ReglaNegocioException(NO_EXISTE);
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo actualizar la reseña.", e);
        }
    }

    @Override
    public void eliminar(int idResena) {
        String sql = "DELETE FROM RESENA WHERE id_resena = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idResena);
            if (ps.executeUpdate() == 0) {
                throw new ReglaNegocioException(NO_EXISTE);
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo eliminar la reseña.", e);
        }
    }

    @Override
    public void cambiarEstado(int idResena, EstadoResena nuevoEstado) {
        String sql = "UPDATE RESENA SET estado = ? WHERE id_resena = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.name());
            ps.setInt(2, idResena);
            if (ps.executeUpdate() == 0) {
                throw new ReglaNegocioException(NO_EXISTE);
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo cambiar el estado de la reseña.", e);
        }
    }

    // ------------------------------------------------------------------ apoyo

    private List<Resena> consultar(String sql, Object... parametros) {
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Resena> resenas = new ArrayList<>();
                while (rs.next()) {
                    resenas.add(mapear(rs));
                }
                return resenas;
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException(ERROR_CONSULTA, e);
        }
    }

    private static Timestamp aTimestamp(Resena resena) {
        return resena.getFecha() == null ? null : Timestamp.valueOf(resena.getFecha());
    }

    private static Resena mapear(ResultSet rs) throws SQLException {
        Resena r = new Resena();
        r.setIdResena(rs.getInt("id_resena"));
        r.setIdUsuario(rs.getInt("id_usuario"));
        r.setNombreUsuario(rs.getString("nombre_usuario"));
        r.setIdAlbum(rs.getInt("id_album"));
        r.setTituloAlbum(rs.getString("titulo_album"));
        r.setCalificacion(rs.getInt("calificacion"));
        r.setTexto(rs.getString("texto"));
        r.setEstado(EstadoResena.valueOf(rs.getString("estado")));
        Timestamp t = rs.getTimestamp("fecha");
        r.setFecha(t == null ? null : t.toLocalDateTime());
        return r;
    }
}
