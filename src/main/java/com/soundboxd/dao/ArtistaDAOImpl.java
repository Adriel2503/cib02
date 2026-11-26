package com.soundboxd.dao;

import com.soundboxd.model.Artista;
import com.soundboxd.util.ConexionBD;
import com.soundboxd.util.ReglaNegocioException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Implementación JDBC de ArtistaDAO. */
public class ArtistaDAOImpl implements ArtistaDAO {

    private static final String NO_EXISTE = "El artista no existe.";
    private static final String ERROR_CONSULTA = "No se pudieron consultar los artistas.";

    @Override
    public int insertar(Artista artista) {
        String sql = "INSERT INTO ARTISTA (nombre, pais) VALUES (?, ?)";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, artista.getNombre());
            ps.setString(2, artista.getPais());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new ReglaNegocioException("No se pudo guardar el artista.");
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo guardar el artista.", e);
        }
    }

    @Override
    public Optional<Artista> buscarPorId(int idArtista) {
        String sql = "SELECT id_artista, nombre, pais FROM ARTISTA WHERE id_artista = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idArtista);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException(ERROR_CONSULTA, e);
        }
    }

    @Override
    public List<Artista> listarTodos() {
        String sql = "SELECT id_artista, nombre, pais FROM ARTISTA ORDER BY nombre";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Artista> artistas = new ArrayList<>();
            while (rs.next()) {
                artistas.add(mapear(rs));
            }
            return artistas;
        } catch (SQLException e) {
            throw new ReglaNegocioException(ERROR_CONSULTA, e);
        }
    }

    @Override
    public void actualizar(Artista artista) {
        String sql = "UPDATE ARTISTA SET nombre = ?, pais = ? WHERE id_artista = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, artista.getNombre());
            ps.setString(2, artista.getPais());
            ps.setInt(3, artista.getIdArtista());
            if (ps.executeUpdate() == 0) {
                throw new ReglaNegocioException(NO_EXISTE);
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException("No se pudo actualizar el artista.", e);
        }
    }

    @Override
    public void eliminar(int idArtista) {
        String sql = "DELETE FROM ARTISTA WHERE id_artista = ?";
        try (Connection c = ConexionBD.obtener();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idArtista);
            if (ps.executeUpdate() == 0) {
                throw new ReglaNegocioException(NO_EXISTE);
            }
        } catch (SQLException e) {
            if (ErroresSql.esRestriccion(e)) {
                throw new ReglaNegocioException("No se puede eliminar un artista que todavía tiene álbumes.", e);
            }
            throw new ReglaNegocioException("No se pudo eliminar el artista.", e);
        }
    }

    private static Artista mapear(ResultSet rs) throws SQLException {
        Artista a = new Artista();
        a.setIdArtista(rs.getInt("id_artista"));
        a.setNombre(rs.getString("nombre"));
        a.setPais(rs.getString("pais"));
        return a;
    }
}
