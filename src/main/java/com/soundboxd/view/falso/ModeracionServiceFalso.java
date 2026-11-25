package com.soundboxd.view.falso;

import com.soundboxd.model.EstadoResena;
import com.soundboxd.model.Resena;
import com.soundboxd.model.Usuario;
import com.soundboxd.security.ControlAcceso;
import com.soundboxd.service.ModeracionService;
import com.soundboxd.util.ReglaNegocioException;

import java.util.ArrayList;
import java.util.List;

/** Reemplaza temporalmente a ModeracionService (B3 de Bryan). Hereda de la clase porque no es una interfaz. */
public class ModeracionServiceFalso extends ModeracionService {

    @Override
    public void reportar(Usuario actor, int idResena) {
        ControlAcceso.exigirSesion(actor);
        Resena r = DatosFalsos.resenaPorId(idResena);
        if (r.getEstado() != EstadoResena.PUBLICADA) {
            throw new ReglaNegocioException("Solo se pueden reportar reseñas publicadas.");
        }
        r.setEstado(EstadoResena.REPORTADA);
    }

    @Override
    public List<Resena> listarReportadas(Usuario actor) {
        ControlAcceso.exigirModerador(actor);
        List<Resena> resultado = new ArrayList<>();
        for (Resena r : DatosFalsos.RESENAS) {
            if (r.getEstado() == EstadoResena.REPORTADA) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    @Override
    public void ocultar(Usuario actor, int idResena) {
        ControlAcceso.exigirModerador(actor);
        DatosFalsos.resenaPorId(idResena).setEstado(EstadoResena.OCULTA);
    }

    @Override
    public void restituir(Usuario actor, int idResena) {
        ControlAcceso.exigirModerador(actor);
        Resena r = DatosFalsos.resenaPorId(idResena);
        if (r.getEstado() != EstadoResena.OCULTA && r.getEstado() != EstadoResena.REPORTADA) {
            throw new ReglaNegocioException("Solo se pueden restituir reseñas ocultas o reportadas.");
        }
        r.setEstado(EstadoResena.PUBLICADA);
    }
}
