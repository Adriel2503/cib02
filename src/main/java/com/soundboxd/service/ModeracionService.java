package com.soundboxd.service;

import com.soundboxd.model.Resena;
import com.soundboxd.model.Usuario;

import java.util.List;

/**
 * M6: moderación. FIRMAS PUBLICADAS (viernes 20 nov); implementación en B3 (24–27 nov).
 * Estados de reseña: PUBLICADA, REPORTADA, OCULTA (vía ResenaDAO.cambiarEstado / listarPorEstado de Amado).
 * Cada método llama primero a ControlAcceso.
 */
public class ModeracionService {

    /** Usuario o moderador reporta: PUBLICADA -> REPORTADA. */
    public void reportar(Usuario actor, int idResena) {
        throw new UnsupportedOperationException("Pendiente B3");
    }

    /** Solo moderador: lista las reseñas en estado REPORTADA. */
    public List<Resena> listarReportadas(Usuario actor) {
        throw new UnsupportedOperationException("Pendiente B3");
    }

    /** Solo moderador: -> OCULTA (desaparece del álbum y del promedio). */
    public void ocultar(Usuario actor, int idResena) {
        throw new UnsupportedOperationException("Pendiente B3");
    }

    /** Solo moderador: OCULTA/REPORTADA -> PUBLICADA. */
    public void restituir(Usuario actor, int idResena) {
        throw new UnsupportedOperationException("Pendiente B3");
    }
}
