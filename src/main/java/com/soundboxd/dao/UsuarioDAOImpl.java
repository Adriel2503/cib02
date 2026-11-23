package com.soundboxd.dao;

import com.soundboxd.model.Rol;
import com.soundboxd.model.Usuario;
import com.soundboxd.util.ConexionBD;
import com.soundboxd.util.ReglaNegocioException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

/** Implementación JDBC de UsuarioDAO. Solo PreparedStatement: nunca se concatena SQL. */
public class UsuarioDAOImpl implements UsuarioDAO {

    /** Códigos de SQL Server para violación de UNIQUE. */
    private static final int UNIQUE_CONSTRAINT = 2627;
    private static final int UNIQUE_INDEX = 2601;

    @Override
    public int insertar(Usuario usuario) {
        String sql = """
                INSERT INTO USUARIO (nombre_usuario, correo, hash_contrasena, rol, activo)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getCorreo());
            ps.setString(3, usuario.getHashContrasena());
            ps.setString(4, usuario.getRol().name());
            ps.setBoolean(5, usuario.isActivo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new ReglaNegocioException("No se pudo registrar el usuario.");
        } catch (SQLException e) {
            if (e.getErrorCode() == UNIQUE_CONSTRAINT || e.getErrorCode() == UNIQUE_INDEX) {
                throw new ReglaNegocioException("El nombre de usuario o el correo ya están registrados.", e);
            }
            throw new ReglaNegocioException("No se pudo registrar el usuario.", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(int idUsuario) {
        String sql = """
                SELECT id_usuario, nombre_usuario, correo, hash_contrasena, rol, fecha_registro, activo
                FROM USUARIO WHERE id_usuario = ?
                """;
        return buscarUno(sql, idUsuario);
    }

    @Override
    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        String sql = """
                SELECT id_usuario, nombre_usuario, correo, hash_contrasena, rol, fecha_registro, activo
                FROM USUARIO WHERE nombre_usuario = ?
                """;
        return buscarUno(sql, nombreUsuario);
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        String sql = """
                SELECT id_usuario, nombre_usuario, correo, hash_contrasena, rol, fecha_registro, activo
                FROM USUARIO WHERE correo = ?
                """;
        return buscarUno(sql, correo);
    }

    @Override
    public boolean existeNombreUsuario(String nombreUsuario) {
        String sql = "SELECT 1 FROM USUARIO WHERE nombre_usuario = ?";
        return existe(sql, nombreUsuario);
    }

    @Override
    public boolean existeCorreo(String correo) {
        String sql = "SELECT 1 FROM USUARIO WHERE correo = ?";
        return existe(sql, correo);
    }

    @Override
    public void actualizar(Usuario usuario) {
        String sql = """
                UPDATE USUARIO
                SET correo = ?, hash_contrasena = ?, rol = ?, activo = ?
                WHERE id_usuario = ?
                """;
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuario.getCorreo());
            ps.setString(2, usuario.getHashContrasena());
            ps.setString(3, usuario.getRol().name());
            ps.setBoolean(4, usuario.isActivo());
            ps.setInt(5, usuario.getIdUsuario());
            if (ps.executeUpdate() == 0) {
                throw new ReglaNegocioException("El usuario no existe.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == UNIQUE_CONSTRAINT || e.getErrorCode() == UNIQUE_INDEX) {
                throw new ReglaNegocioException("Ese correo ya está registrado.", e);
            }
            throw new ReglaNegocioException("No se pudo actualizar el usuario.", e);
        }
    }

    @Override
    public void desactivar(int idUsuario) {
        String sql = "UPDATE USUARIO SET activo = 0 WHERE id_usuario = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            if (ps.executeUpdate() == 0) {
                throw new ReglaNegocioException("El usuario no existe.");
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo desactivar el usuario.", e);
        }
    }

    // ------------------------------------------------------------------ apoyo

    private Optional<Usuario> buscarUno(String sql, Object parametro) {
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo consultar el usuario.", e);
        }
    }

    private boolean existe(String sql, String parametro) {
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo consultar el usuario.", e);
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombreUsuario(rs.getString("nombre_usuario"));
        u.setCorreo(rs.getString("correo"));
        u.setHashContrasena(rs.getString("hash_contrasena"));
        u.setRol(Rol.valueOf(rs.getString("rol")));
        Timestamp t = rs.getTimestamp("fecha_registro");
        u.setFechaRegistro(t == null ? null : t.toLocalDateTime());
        u.setActivo(rs.getBoolean("activo"));
        return u;
    }
}
