package com.soundboxd.view.falso;

import com.soundboxd.model.Album;
import com.soundboxd.model.Cancion;
import com.soundboxd.model.EstadoResena;
import com.soundboxd.model.Resena;
import com.soundboxd.model.Usuario;
import com.soundboxd.security.ControlAcceso;
import com.soundboxd.service.CatalogoService;
import com.soundboxd.util.ReglaNegocioException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Reemplaza temporalmente al servicio de catálogo (M2) de Amado. */
public class CatalogoServiceFalso implements CatalogoService {

    @Override
    public List<Album> buscarAlbumes(String texto) {
        String t = texto == null ? "" : texto.trim().toLowerCase();
        List<Album> resultado = new ArrayList<>();
        for (Album a : DatosFalsos.ALBUMES) {
            if (t.isEmpty()
                    || a.getTitulo().toLowerCase().contains(t)
                    || a.getNombreArtista().toLowerCase().contains(t)
                    || a.getGenero().toLowerCase().contains(t)) {
                resultado.add(a);
            }
        }
        return resultado;
    }

    @Override
    public Album obtenerAlbum(int idAlbum) {
        return DatosFalsos.albumPorId(idAlbum);
    }

    @Override
    public List<Cancion> listarCanciones(int idAlbum) {
        DatosFalsos.albumPorId(idAlbum);
        List<Cancion> resultado = new ArrayList<>();
        for (Cancion c : DatosFalsos.CANCIONES) {
            if (c.getIdAlbum() == idAlbum) {
                resultado.add(c);
            }
        }
        resultado.sort(Comparator.comparingInt(Cancion::getNumeroPista));
        return resultado;
    }

    @Override
    public double promedioAlbum(int idAlbum) {
        int suma = 0;
        int cantidad = 0;
        for (Resena r : DatosFalsos.RESENAS) {
            if (r.getIdAlbum() == idAlbum && r.getEstado() == EstadoResena.PUBLICADA) {
                suma += r.getCalificacion();
                cantidad++;
            }
        }
        return cantidad == 0 ? 0 : (double) suma / cantidad;
    }

    @Override
    public Album crearAlbum(Usuario actor, Album album) {
        ControlAcceso.exigirModerador(actor);
        validar(album);
        album.setIdAlbum(DatosFalsos.nuevoIdAlbum());
        DatosFalsos.ALBUMES.add(album);
        return album;
    }

    @Override
    public void actualizarAlbum(Usuario actor, Album album) {
        ControlAcceso.exigirModerador(actor);
        validar(album);
        Album existente = DatosFalsos.albumPorId(album.getIdAlbum());
        DatosFalsos.ALBUMES.set(DatosFalsos.ALBUMES.indexOf(existente), album);
    }

    @Override
    public void eliminarAlbum(Usuario actor, int idAlbum) {
        ControlAcceso.exigirModerador(actor);
        Album existente = DatosFalsos.albumPorId(idAlbum);
        DatosFalsos.ALBUMES.remove(existente);
        DatosFalsos.CANCIONES.removeIf(c -> c.getIdAlbum() == idAlbum);
        DatosFalsos.RESENAS.removeIf(r -> r.getIdAlbum() == idAlbum);
    }

    private static void validar(Album album) {
        if (album == null || album.getTitulo() == null || album.getTitulo().isBlank()) {
            throw new ReglaNegocioException("El título del álbum es obligatorio.");
        }
    }
}
