package com.soundboxd.view.falso;

import com.soundboxd.model.EstadoResena;
import com.soundboxd.model.Resena;
import com.soundboxd.model.Usuario;
import com.soundboxd.security.ControlAcceso;
import com.soundboxd.service.SocialService;
import com.soundboxd.util.ReglaNegocioException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Reemplaza temporalmente al servicio social (M5) de Amado. */
public class SocialServiceFalso implements SocialService {

    @Override
    public void seguir(Usuario actor, int idSeguido) {
        ControlAcceso.exigirSesion(actor);
        if (actor.getIdUsuario() == idSeguido) {
            throw new ReglaNegocioException("No puedes seguirte a ti mismo.");
        }
        DatosFalsos.usuarioPorId(idSeguido);
        if (!DatosFalsos.SEGUIMIENTOS.add(DatosFalsos.claveSeguimiento(actor.getIdUsuario(), idSeguido))) {
            throw new ReglaNegocioException("Ya sigues a este usuario.");
        }
    }

    @Override
    public void dejarDeSeguir(Usuario actor, int idSeguido) {
        ControlAcceso.exigirSesion(actor);
        DatosFalsos.SEGUIMIENTOS.remove(DatosFalsos.claveSeguimiento(actor.getIdUsuario(), idSeguido));
    }

    @Override
    public List<Usuario> listarSeguidos(Usuario actor) {
        ControlAcceso.exigirSesion(actor);
        List<Usuario> resultado = new ArrayList<>();
        for (Usuario u : DatosFalsos.USUARIOS) {
            if (sigue(actor, u.getIdUsuario())) {
                resultado.add(u);
            }
        }
        return resultado;
    }

    @Override
    public List<Resena> actividadReciente(Usuario actor, int limite) {
        ControlAcceso.exigirSesion(actor);
        List<Resena> resultado = new ArrayList<>();
        for (Resena r : DatosFalsos.RESENAS) {
            if (r.getEstado() == EstadoResena.PUBLICADA && sigue(actor, r.getIdUsuario())) {
                resultado.add(r);
            }
        }
        resultado.sort(Comparator.comparing(Resena::getFecha).reversed());
        return resultado.size() > limite ? new ArrayList<>(resultado.subList(0, Math.max(limite, 0))) : resultado;
    }

    private static boolean sigue(Usuario actor, int idSeguido) {
        return DatosFalsos.SEGUIMIENTOS.contains(DatosFalsos.claveSeguimiento(actor.getIdUsuario(), idSeguido));
    }
}
