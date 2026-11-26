package com.soundboxd.service;

import com.soundboxd.dao.AlbumDAO;
import com.soundboxd.dao.AlbumDAOImpl;
import com.soundboxd.dao.ResenaDAO;
import com.soundboxd.dao.ResenaDAOImpl;
import com.soundboxd.model.Album;
import com.soundboxd.model.EstadoResena;
import com.soundboxd.model.Resena;
import com.soundboxd.model.Usuario;
import com.soundboxd.security.ControlAcceso;
import com.soundboxd.util.ReglaNegocioException;
import com.soundboxd.util.Validador;

import java.time.LocalDateTime;
import java.util.List;

/** Reglas del módulo M3: reseñas. */
public class ResenaServiceImpl implements ResenaService {

    private static final int MAX_TEXTO = 2000;

    private final ResenaDAO resenas;
    private final AlbumDAO albumes;

    public ResenaServiceImpl() {
        this(new ResenaDAOImpl(), new AlbumDAOImpl());
    }

    public ResenaServiceImpl(ResenaDAO resenas, AlbumDAO albumes) {
        this.resenas = resenas;
        this.albumes = albumes;
    }

    /** Si ya hay un borrador del mismo álbum, lo actualiza. */
    @Override
    public Resena guardarBorrador(Usuario actor, int idAlbum, int calificacion, String texto) {
        ControlAcceso.exigirSesion(actor);
        validar(calificacion, texto);
        Album album = albumes.buscarPorId(idAlbum)
                .orElseThrow(() -> new ReglaNegocioException("El álbum no existe."));

        Resena existente = resenas.buscarPorUsuarioYAlbum(actor.getIdUsuario(), idAlbum).orElse(null);
        if (existente != null) {
            if (existente.getEstado() != EstadoResena.BORRADOR) {
                throw new ReglaNegocioException("Ya escribiste una reseña de este álbum.");
            }
            existente.setCalificacion(calificacion);
            existente.setTexto(texto);
            resenas.actualizar(existente);
            return existente;
        }

        Resena nueva = new Resena();
        nueva.setIdUsuario(actor.getIdUsuario());
        nueva.setNombreUsuario(actor.getNombreUsuario());
        nueva.setIdAlbum(idAlbum);
        nueva.setTituloAlbum(album.getTitulo());
        nueva.setCalificacion(calificacion);
        nueva.setTexto(texto);
        nueva.setEstado(EstadoResena.BORRADOR);
        nueva.setFecha(LocalDateTime.now());
        nueva.setIdResena(resenas.insertar(nueva));
        return nueva;
    }

    @Override
    public Resena publicar(Usuario actor, int idResena) {
        Resena r = propia(actor, idResena);
        if (r.getEstado() != EstadoResena.BORRADOR) {
            throw new ReglaNegocioException("Solo se pueden publicar borradores.");
        }
        r.setEstado(EstadoResena.PUBLICADA);
        r.setFecha(LocalDateTime.now());
        resenas.actualizar(r);
        return r;
    }

    @Override
    public void editar(Usuario actor, int idResena, int calificacion, String texto) {
        Resena r = propia(actor, idResena);
        validar(calificacion, texto);
        r.setCalificacion(calificacion);
        r.setTexto(texto);
        resenas.actualizar(r);
    }

    @Override
    public void eliminar(Usuario actor, int idResena) {
        propia(actor, idResena);
        resenas.eliminar(idResena);
    }

    @Override
    public List<Resena> listarPorAlbum(int idAlbum) {
        return resenas.listarPublicadasPorAlbum(idAlbum);
    }

    @Override
    public List<Resena> listarMias(Usuario actor) {
        ControlAcceso.exigirSesion(actor);
        return resenas.listarPorUsuario(actor.getIdUsuario());
    }

    private Resena propia(Usuario actor, int idResena) {
        Resena r = resenas.buscarPorId(idResena)
                .orElseThrow(() -> new ReglaNegocioException("La reseña no existe."));
        ControlAcceso.exigirPropietario(actor, r.getIdUsuario());
        return r;
    }

    private static void validar(int calificacion, String texto) {
        if (!Validador.calificacionValida(calificacion)) {
            throw new ReglaNegocioException("La calificación debe estar entre 1 y 5.");
        }
        if (texto != null && texto.length() > MAX_TEXTO) {
            throw new ReglaNegocioException("La reseña no puede superar los 2000 caracteres.");
        }
    }
}
