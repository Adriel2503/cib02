package com.soundboxd.dao;

import com.soundboxd.model.Rol;
import com.soundboxd.model.Usuario;
import com.soundboxd.util.ConexionBD;
import com.soundboxd.util.ReglaNegocioException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC de SeguimientoDAO. */
public class SeguimientoDAOImpl implements SeguimientoDAO {

    @Override
    public void seguir(int idSeguidor, int idSeguido) {
        String sql = "INSERT INTO SEGUIMIENTO (id_seguidor, id_seguido) VALUES (?, ?)";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idSeguidor);
            ps.setInt(2, idSeguido);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (ErroresSql.esDuplicado(e)) {
                throw new ReglaNegocioException("Ya sigues a este usuario.", e);
            }
            if (ErroresSql.esRestriccion(e)) {
                throw new ReglaNegocioException("No se puede seguir a ese usuario.", e);
            }
            throw new ReglaNegocioException("No se pudo seguir al usuario.", e);
        }
    }

    @Override
    public void dejarDeSeguir(int idSeguidor, int idSeguido) {
        String sql = "DELETE FROM SEGUIMIENTO WHERE id_seguidor = ? AND id_seguido = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idSeguidor);
            ps.setInt(2, idSeguido);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo dejar de seguir al usuario.", e);
        }
    }

    @Override
    public boolean existe(int idSeguidor, int idSeguido) {
        String sql = "SELECT 1 FROM SEGUIMIENTO WHERE id_seguidor = ? AND id_seguido = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idSeguidor);
            ps.setInt(2, idSeguido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo consultar el seguimiento.", e);
        }
    }

    @Override
    public List<Usuario> listarSeguidos(int idSeguidor) {
        // No se selecciona hash_contrasena: estos usuarios llegan hasta la vista.
        String sql = """
                SELECT u.id_usuario, u.nombre_usuario, u.correo, u.rol, u.fecha_registro, u.activo
                FROM SEGUIMIENTO s INNER JOIN USUARIO u ON u.id_usuario = s.id_seguido
                WHERE s.id_seguidor = ?
                ORDER BY u.nombre_usuario
                """;
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idSeguidor);
            try (ResultSet rs = ps.executeQuery()) {
                List<Usuario> seguidos = new ArrayList<>();
                while (rs.next()) {
                    seguidos.add(mapear(rs));
                }
                return seguidos;
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudieron consultar los usuarios seguidos.", e);
        }
    }

    private static Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombreUsuario(rs.getString("nombre_usuario"));
        u.setCorreo(rs.getString("correo"));
        u.setRol(Rol.valueOf(rs.getString("rol")));
        Timestamp t = rs.getTimestamp("fecha_registro");
        u.setFechaRegistro(t == null ? null : t.toLocalDateTime());
        u.setActivo(rs.getBoolean("activo"));
        return u;
    }
}
