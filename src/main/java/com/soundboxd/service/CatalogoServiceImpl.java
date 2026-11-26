package com.soundboxd.service;

import com.soundboxd.dao.AlbumDAO;
import com.soundboxd.dao.AlbumDAOImpl;
import com.soundboxd.dao.CancionDAO;
import com.soundboxd.dao.CancionDAOImpl;
import com.soundboxd.model.Album;
import com.soundboxd.model.Cancion;
import com.soundboxd.model.Usuario;
import com.soundboxd.security.ControlAcceso;
import com.soundboxd.util.ReglaNegocioException;

import java.util.List;

/** Reglas del módulo M2: catálogo. */
public class CatalogoServiceImpl implements CatalogoService {

    private final AlbumDAO albumes;
    private final CancionDAO canciones;

    public CatalogoServiceImpl() {
        this(new AlbumDAOImpl(), new CancionDAOImpl());
    }

    public CatalogoServiceImpl(AlbumDAO albumes, CancionDAO canciones) {
        this.albumes = albumes;
        this.canciones = canciones;
    }

    @Override
    public List<Album> buscarAlbumes(String texto) {
        return albumes.buscar(texto == null ? "" : texto.trim());
    }

    @Override
    public Album obtenerAlbum(int idAlbum) {
        return albumes.buscarPorId(idAlbum)
                .orElseThrow(() -> new ReglaNegocioException("El álbum no existe."));
    }

    @Override
    public List<Cancion> listarCanciones(int idAlbum) {
        obtenerAlbum(idAlbum);
        return canciones.listarPorAlbum(idAlbum);
    }

    @Override
    public double promedioAlbum(int idAlbum) {
        return albumes.promedioPublicadas(idAlbum).orElse(0);
    }

    @Override
    public Album crearAlbum(Usuario actor, Album album) {
        ControlAcceso.exigirModerador(actor);
        validar(album);
        album.setIdAlbum(albumes.insertar(album));
        return album;
    }

    @Override
    public void actualizarAlbum(Usuario actor, Album album) {
        ControlAcceso.exigirModerador(actor);
        validar(album);
        albumes.actualizar(album);
    }

    @Override
    public void eliminarAlbum(Usuario actor, int idAlbum) {
        ControlAcceso.exigirModerador(actor);
        albumes.eliminar(idAlbum);
    }

    /** Límites iguales a los de la tabla ALBUM. */
    private static void validar(Album album) {
        if (album == null || album.getTitulo() == null || album.getTitulo().isBlank()) {
            throw new ReglaNegocioException("El título del álbum es obligatorio.");
        }
        if (album.getTitulo().length() > 200) {
            throw new ReglaNegocioException("El título no puede superar los 200 caracteres.");
        }
        if (album.getAnio() < 1900 || album.getAnio() > 2100) {
            throw new ReglaNegocioException("El año debe estar entre 1900 y 2100.");
        }
        if (album.getGenero() != null && album.getGenero().length() > 60) {
            throw new ReglaNegocioException("El género no puede superar los 60 caracteres.");
        }
        if (album.getPortada() != null && album.getPortada().length() > 500) {
            throw new ReglaNegocioException("La ruta de la portada no puede superar los 500 caracteres.");
        }
    }
}
