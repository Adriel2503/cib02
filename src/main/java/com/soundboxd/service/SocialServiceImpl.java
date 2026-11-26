package com.soundboxd.service;

import com.soundboxd.dao.ResenaDAO;
import com.soundboxd.dao.ResenaDAOImpl;
import com.soundboxd.dao.SeguimientoDAO;
import com.soundboxd.dao.SeguimientoDAOImpl;
import com.soundboxd.dao.UsuarioDAO;
import com.soundboxd.dao.UsuarioDAOImpl;
import com.soundboxd.model.Resena;
import com.soundboxd.model.Usuario;
import com.soundboxd.security.ControlAcceso;
import com.soundboxd.util.ReglaNegocioException;

import java.util.List;

/** Reglas del módulo M5: red social. */
public class SocialServiceImpl implements SocialService {

    private final SeguimientoDAO seguimientos;
    private final ResenaDAO resenas;
    private final UsuarioDAO usuarios;

    public SocialServiceImpl() {
        this(new SeguimientoDAOImpl(), new ResenaDAOImpl(), new UsuarioDAOImpl());
    }

    public SocialServiceImpl(SeguimientoDAO seguimientos, ResenaDAO resenas, UsuarioDAO usuarios) {
        this.seguimientos = seguimientos;
        this.resenas = resenas;
        this.usuarios = usuarios;
    }

    @Override
    public void seguir(Usuario actor, int idSeguido) {
        ControlAcceso.exigirSesion(actor);
        if (actor.getIdUsuario() == idSeguido) {
            throw new ReglaNegocioException("No puedes seguirte a ti mismo.");
        }
        // Una cuenta desactivada se trata como inexistente.
        if (usuarios.buscarPorId(idSeguido).filter(Usuario::isActivo).isEmpty()) {
            throw new ReglaNegocioException("El usuario no existe.");
        }
        if (seguimientos.existe(actor.getIdUsuario(), idSeguido)) {
            throw new ReglaNegocioException("Ya sigues a este usuario.");
        }
        seguimientos.seguir(actor.getIdUsuario(), idSeguido);
    }

    @Override
    public void dejarDeSeguir(Usuario actor, int idSeguido) {
        ControlAcceso.exigirSesion(actor);
        seguimientos.dejarDeSeguir(actor.getIdUsuario(), idSeguido);
    }

    @Override
    public List<Usuario> listarSeguidos(Usuario actor) {
        ControlAcceso.exigirSesion(actor);
        return seguimientos.listarSeguidos(actor.getIdUsuario());
    }

    @Override
    public List<Usuario> buscarUsuarios(Usuario actor, String texto) {
        ControlAcceso.exigirSesion(actor);
        return usuarios.buscarPorTexto(texto == null ? "" : texto.trim()).stream()
                .filter(u -> u.getIdUsuario() != actor.getIdUsuario())
                .toList();
    }

    @Override
    public List<Resena> actividadReciente(Usuario actor, int limite) {
        ControlAcceso.exigirSesion(actor);
        if (limite <= 0) {
            return List.of();
        }
        return resenas.listarActividadDeSeguidos(actor.getIdUsuario(), limite);
    }
}
