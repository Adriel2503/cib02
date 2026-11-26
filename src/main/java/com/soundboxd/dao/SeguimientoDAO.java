package com.soundboxd.dao;

import com.soundboxd.model.Seguimiento;
import com.soundboxd.model.Usuario;

import java.util.List;

/** Acceso a datos de SEGUIMIENTO. */
public interface SeguimientoDAO {

    void seguir(int idSeguidor, int idSeguido);

    void dejarDeSeguir(int idSeguidor, int idSeguido);

    boolean existe(int idSeguidor, int idSeguido);

    /** Usuarios activos a los que sigue idSeguidor. El seguimiento de una cuenta desactivada se conserva. */
    List<Usuario> listarSeguidos(int idSeguidor);
}
