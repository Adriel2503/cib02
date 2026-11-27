package com.soundboxd.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.soundboxd.model.Rol;
import com.soundboxd.model.Usuario;
import com.soundboxd.util.ReglaNegocioException;
import org.junit.jupiter.api.Test;

/** RF-23: los permisos se aplican en la capa de servicios. No necesita base de datos. */
class ControlAccesoTest {

    private static final String SIN_PERMISO = "No tienes permiso para esta acción.";

    private static Usuario persona(int id, Rol rol) {
        Usuario u = new Usuario();
        u.setIdUsuario(id);
        u.setRol(rol);
        u.setActivo(true);
        return u;
    }

    private final Usuario normal = persona(1, Rol.USUARIO);
    private final Usuario moderador = persona(2, Rol.MODERADOR);

    // --- Usuario normal ---

    @Test
    void unUsuarioNormalNoPuedeEjecutarAccionesDeModerador() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> ControlAcceso.exigirModerador(normal));
        assertEquals(SIN_PERMISO, e.getMessage());
    }

    @Test
    void unUsuarioNormalSiPuedeEjecutarAccionesConSesion() {
        assertDoesNotThrow(() -> ControlAcceso.exigirSesion(normal));
    }

    // --- Moderador ---

    @Test
    void unModeradorSiPuedeEjecutarAccionesDeModerador() {
        assertDoesNotThrow(() -> ControlAcceso.exigirModerador(moderador));
    }

    @Test
    void unModeradorTambienTieneSesion() {
        assertDoesNotThrow(() -> ControlAcceso.exigirSesion(moderador));
    }

    // --- Visitante (sin sesión = actor null) ---

    @Test
    void sinSesionSeRechazaCualquierAccion() {
        assertThrows(ReglaNegocioException.class, () -> ControlAcceso.exigirSesion(null));
        assertThrows(ReglaNegocioException.class, () -> ControlAcceso.exigirModerador(null));
        assertThrows(ReglaNegocioException.class, () -> ControlAcceso.exigirRol(null, Rol.USUARIO));
        assertThrows(ReglaNegocioException.class, () -> ControlAcceso.exigirPropietario(null, 1));
    }

    @Test
    void elMensajeDeRechazoNoRevelaDetallesInternos() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> ControlAcceso.exigirSesion(null));
        assertEquals(SIN_PERMISO, e.getMessage());
    }

    // --- Propietario ---

    @Test
    void elDuenoPuedeActuarSobreSuPropioContenido() {
        assertDoesNotThrow(() -> ControlAcceso.exigirPropietario(normal, 1));
    }

    @Test
    void otroUsuarioNoPuedeActuarSobreContenidoAjeno() {
        assertThrows(ReglaNegocioException.class, () -> ControlAcceso.exigirPropietario(normal, 99));
    }

    @Test
    void elModeradorNoEditaContenidoAjeno() {
        assertThrows(ReglaNegocioException.class, () -> ControlAcceso.exigirPropietario(moderador, 1));
    }

    // --- Roles permitidos ---

    @Test
    void exigirRolAceptaSoloLosRolesIndicados() {
        assertDoesNotThrow(() -> ControlAcceso.exigirRol(normal, Rol.USUARIO));
        assertThrows(ReglaNegocioException.class, () -> ControlAcceso.exigirRol(normal, Rol.MODERADOR));
        assertDoesNotThrow(() -> ControlAcceso.exigirRol(moderador, Rol.USUARIO, Rol.MODERADOR));
    }
}
