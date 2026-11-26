package com.soundboxd.dao;

import com.soundboxd.model.EstadoResena;
import com.soundboxd.model.Resena;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Acceso a datos de RESENA. cambiarEstado y listarPorEstado son para ModeracionService. */
public interface ResenaDAO {

    int insertar(Resena resena);

    Optional<Resena> buscarPorId(int idResena);

    Optional<Resena> buscarPorUsuarioYAlbum(int idUsuario, int idAlbum);

    /** Solo reseñas en estado PUBLICADA, las más recientes primero. */
    List<Resena> listarPublicadasPorAlbum(int idAlbum);

    /** Todas las reseñas del usuario, sea cual sea su estado. */
    List<Resena> listarPorUsuario(int idUsuario);

    /** Reseñas publicadas de las cuentas activas que sigue idSeguidor (feed), las más recientes primero. */
    List<Resena> listarActividadDeSeguidos(int idSeguidor, int limite);

    void actualizar(Resena resena);

    /** Cambia calificación y texto sin tocar el estado, para no deshacer una moderación simultánea. */
    void actualizarContenido(int idResena, int calificacion, String texto);

    /** Pasa a PUBLICADA solo si sigue en BORRADOR; si no, lanza ReglaNegocioException. */
    void publicarBorrador(int idResena, LocalDateTime fecha);

    void eliminar(int idResena);

    void cambiarEstado(int idResena, EstadoResena nuevoEstado);

    List<Resena> listarPorEstado(EstadoResena estado);
}
