package com.soundboxd.view.falso;

import com.soundboxd.model.EstadoResena;
import com.soundboxd.model.Resena;
import com.soundboxd.model.Usuario;
import com.soundboxd.security.ControlAcceso;
import com.soundboxd.service.ResenaService;
import com.soundboxd.util.ReglaNegocioException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Reemplaza temporalmente al servicio de reseñas (M3) de Amado. */
public class ResenaServiceFalso implements ResenaService {

    @Override
    public Resena guardarBorrador(Usuario actor, int idAlbum, int calificacion, String texto) {
        ControlAcceso.exigirSesion(actor);
        validarCalificacion(calificacion);
        DatosFalsos.albumPorId(idAlbum);

        for (Resena existente : DatosFalsos.RESENAS) {
            if (existente.getIdUsuario() == actor.getIdUsuario() && existente.getIdAlbum() == idAlbum) {
                if (existente.getEstado() != EstadoResena.BORRADOR) {
                    throw new ReglaNegocioException("Ya escribiste una reseña de este álbum.");
                }
                existente.setCalificacion(calificacion);
                existente.setTexto(texto);
                return existente;
            }
        }
        return DatosFalsos.nuevaResena(actor, idAlbum, calificacion, texto, EstadoResena.BORRADOR, 0);
    }

    @Override
    public Resena publicar(Usuario actor, int idResena) {
        Resena r = DatosFalsos.resenaPorId(idResena);
        ControlAcceso.exigirPropietario(actor, r.getIdUsuario());
        if (r.getEstado() != EstadoResena.BORRADOR) {
            throw new ReglaNegocioException("Solo se pueden publicar borradores.");
        }
        r.setEstado(EstadoResena.PUBLICADA);
        r.setFecha(LocalDateTime.now());
        return r;
    }

    @Override
    public void editar(Usuario actor, int idResena, int calificacion, String texto) {
        Resena r = DatosFalsos.resenaPorId(idResena);
        ControlAcceso.exigirPropietario(actor, r.getIdUsuario());
        validarCalificacion(calificacion);
        r.setCalificacion(calificacion);
        r.setTexto(texto);
    }

    @Override
    public void eliminar(Usuario actor, int idResena) {
        Resena r = DatosFalsos.resenaPorId(idResena);
        ControlAcceso.exigirPropietario(actor, r.getIdUsuario());
        DatosFalsos.RESENAS.remove(r);
    }

    @Override
    public List<Resena> listarPorAlbum(int idAlbum) {
        List<Resena> resultado = new ArrayList<>();
        for (Resena r : DatosFalsos.RESENAS) {
            if (r.getIdAlbum() == idAlbum && r.getEstado() == EstadoResena.PUBLICADA) {
                resultado.add(r);
            }
        }
        resultado.sort(Comparator.comparing(Resena::getFecha).reversed());
        return resultado;
    }

    @Override
    public List<Resena> listarMias(Usuario actor) {
        ControlAcceso.exigirSesion(actor);
        List<Resena> resultado = new ArrayList<>();
        for (Resena r : DatosFalsos.RESENAS) {
            if (r.getIdUsuario() == actor.getIdUsuario()) {
                resultado.add(r);
            }
        }
        resultado.sort(Comparator.comparing(Resena::getFecha).reversed());
        return resultado;
    }

    private static void validarCalificacion(int calificacion) {
        if (calificacion < 1 || calificacion > 5) {
            throw new ReglaNegocioException("La calificación debe estar entre 1 y 5.");
        }
    }
}
