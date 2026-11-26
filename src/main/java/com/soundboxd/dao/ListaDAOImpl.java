package com.soundboxd.dao;

import com.soundboxd.model.Lista;
import com.soundboxd.model.ListaAlbum;
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

/** Implementación JDBC de ListaDAO. Los métodos que reciben Connection no la cierran. */
public class ListaDAOImpl implements ListaDAO {

    private static final String ERROR_CONSULTA = "No se pudieron consultar las listas.";

    @Override
    public int insertar(Connection conexion, Lista lista) {
        String sql = """
                INSERT INTO LISTA (id_usuario, nombre, descripcion, es_publica)
                VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, lista.getIdUsuario());
            ps.setString(2, lista.getNombre());
            ps.setString(3, lista.getDescripcion());
            ps.setBoolean(4, lista.isEsPublica());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new ReglaNegocioException("No se pudo crear la lista.");
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo crear la lista.", e);
        }
    }

    @Override
    public void agregarAlbum(Connection conexion, int idLista, int idAlbum, int posicion) {
        String sql = "INSERT INTO LISTA_ALBUM (id_lista, id_album, posicion) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idLista);
            ps.setInt(2, idAlbum);
            ps.setInt(3, posicion);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (ErroresSql.esDuplicado(e)) {
                throw new ReglaNegocioException("El álbum ya está en la lista.", e);
            }
            if (ErroresSql.esRestriccion(e)) {
                throw new ReglaNegocioException("El álbum no existe.", e);
            }
            throw new ReglaNegocioException("No se pudo agregar el álbum a la lista.", e);
        }
    }

    @Override
    public Optional<Lista> buscarPorId(int idLista) {
        String sql = """
                SELECT id_lista, id_usuario, nombre, descripcion, es_publica, fecha_creacion
                FROM LISTA WHERE id_lista = ?
                """;
        return consultar(sql, idLista).stream().findFirst();
    }

    @Override
    public List<Lista> listarPorUsuario(int idUsuario) {
        String sql = """
                SELECT id_lista, id_usuario, nombre, descripcion, es_publica, fecha_creacion
                FROM LISTA WHERE id_usuario = ?
                ORDER BY fecha_creacion DESC, id_lista DESC
                """;
        return consultar(sql, idUsuario);
    }

    @Override
    public void actualizar(Lista lista) {
        String sql = "UPDATE LISTA SET nombre = ?, descripcion = ?, es_publica = ? WHERE id_lista = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, lista.getNombre());
            ps.setString(2, lista.getDescripcion());
            ps.setBoolean(3, lista.isEsPublica());
            ps.setInt(4, lista.getIdLista());
            if (ps.executeUpdate() == 0) {
                throw new ReglaNegocioException("La lista no existe.");
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo actualizar la lista.", e);
        }
    }

    @Override
    public List<ListaAlbum> listarAlbumes(int idLista) {
        String sql = """
                SELECT id_lista, id_album, posicion
                FROM LISTA_ALBUM WHERE id_lista = ?
                ORDER BY posicion
                """;
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idLista);
            try (ResultSet rs = ps.executeQuery()) {
                List<ListaAlbum> albumes = new ArrayList<>();
                while (rs.next()) {
                    ListaAlbum la = new ListaAlbum();
                    la.setIdLista(rs.getInt("id_lista"));
                    la.setIdAlbum(rs.getInt("id_album"));
                    la.setPosicion(rs.getInt("posicion"));
                    albumes.add(la);
                }
                return albumes;
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException(ERROR_CONSULTA, e);
        }
    }

    @Override
    public void quitarAlbum(int idLista, int idAlbum) {
        String sql = "DELETE FROM LISTA_ALBUM WHERE id_lista = ? AND id_album = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idLista);
            ps.setInt(2, idAlbum);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo quitar el álbum de la lista.", e);
        }
    }

    @Override
    public void actualizarPosicion(int idLista, int idAlbum, int posicion) {
        try (Connection c = ConexionBD.obtener()) {
            actualizarPosicion(c, idLista, idAlbum, posicion);
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo reordenar la lista.", e);
        }
    }

    @Override
    public void actualizarPosicion(Connection conexion, int idLista, int idAlbum, int posicion) {
        String sql = "UPDATE LISTA_ALBUM SET posicion = ? WHERE id_lista = ? AND id_album = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, posicion);
            ps.setInt(2, idLista);
            ps.setInt(3, idAlbum);
            if (ps.executeUpdate() == 0) {
                throw new ReglaNegocioException("El álbum no está en la lista.");
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo reordenar la lista.", e);
        }
    }

    @Override
    public void eliminar(int idLista) {
        String sql = "DELETE FROM LISTA WHERE id_lista = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idLista);
            if (ps.executeUpdate() == 0) {
                throw new ReglaNegocioException("La lista no existe.");
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo eliminar la lista.", e);
        }
    }

    // ------------------------------------------------------------------ apoyo

    private List<Lista> consultar(String sql, int parametro) {
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                List<Lista> listas = new ArrayList<>();
                while (rs.next()) {
                    listas.add(mapear(rs));
                }
                return listas;
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException(ERROR_CONSULTA, e);
        }
    }

    private static Lista mapear(ResultSet rs) throws SQLException {
        Lista l = new Lista();
        l.setIdLista(rs.getInt("id_lista"));
        l.setIdUsuario(rs.getInt("id_usuario"));
        l.setNombre(rs.getString("nombre"));
        l.setDescripcion(rs.getString("descripcion"));
        l.setEsPublica(rs.getBoolean("es_publica"));
        Timestamp t = rs.getTimestamp("fecha_creacion");
        l.setFechaCreacion(t == null ? null : t.toLocalDateTime());
        return l;
    }
}
