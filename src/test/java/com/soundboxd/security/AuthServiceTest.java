package com.soundboxd.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.soundboxd.dao.UsuarioDAO;
import com.soundboxd.model.Rol;
import com.soundboxd.model.Usuario;
import com.soundboxd.util.ReglaNegocioException;
import com.soundboxd.view.Sesion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/** M1 (RF-01 a RF-04) con un DAO en memoria: corre sin SQL Server. */
class AuthServiceTest {

    private static final String CLAVE = "Clave1234";
    private static final String HASH_CLAVE = PasswordHasher.hash(CLAVE);   // una sola vez: BCrypt es lento a propósito

    /** Reloj que se puede adelantar, para probar el desbloqueo. */
    private static final class RelojManual extends Clock {
        private Instant ahora = Instant.parse("2026-11-26T15:00:00Z");

        void avanzar(Duration d) { ahora = ahora.plus(d); }

        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return ahora; }
    }

    /** DAO en memoria: mismas reglas que el real (id generado, duplicados rechazados). */
    private static final class UsuarioDAOMemoria implements UsuarioDAO {
        private final Map<Integer, Usuario> filas = new TreeMap<>();
        private int siguiente = 1;

        @Override public int insertar(Usuario u) {
            if (existeNombreUsuario(u.getNombreUsuario()) || existeCorreo(u.getCorreo())) {
                throw new ReglaNegocioException("El nombre de usuario o el correo ya están registrados.");
            }
            int id = siguiente++;
            u.setIdUsuario(id);
            filas.put(id, u);
            return id;
        }
        @Override public Optional<Usuario> buscarPorId(int id) { return Optional.ofNullable(filas.get(id)); }
        @Override public Optional<Usuario> buscarPorNombreUsuario(String n) {
            return filas.values().stream().filter(u -> u.getNombreUsuario().equalsIgnoreCase(n)).findFirst();
        }
        @Override public Optional<Usuario> buscarPorCorreo(String c) {
            return filas.values().stream().filter(u -> u.getCorreo().equalsIgnoreCase(c)).findFirst();
        }
        @Override public List<Usuario> buscarPorTexto(String t) { return new ArrayList<>(filas.values()); }
        @Override public boolean existeNombreUsuario(String n) { return buscarPorNombreUsuario(n).isPresent(); }
        @Override public boolean existeCorreo(String c) { return buscarPorCorreo(c).isPresent(); }
        @Override public void actualizar(Usuario u) { filas.put(u.getIdUsuario(), u); }
        @Override public void desactivar(int id) { filas.get(id).setActivo(false); }
    }

    private UsuarioDAOMemoria dao;
    private RelojManual reloj;
    private AuthService auth;

    @BeforeEach
    void preparar() {
        dao = new UsuarioDAOMemoria();
        reloj = new RelojManual();
        auth = new AuthService(dao, reloj);
        Sesion.cerrar();
    }

    @AfterEach
    void limpiar() {
        Sesion.cerrar();
    }

    /** Crea una cuenta ya existente, sin pasar por registrar (evita BCrypt en cada prueba). */
    private Usuario cuenta(String nombre, Rol rol, boolean activa) {
        Usuario u = new Usuario();
        u.setNombreUsuario(nombre);
        u.setCorreo(nombre + "@uni.pe");
        u.setHashContrasena(HASH_CLAVE);
        u.setRol(rol);
        u.setActivo(activa);
        dao.insertar(u);
        return u;
    }

    private void falla(String nombre, String clave) {
        assertThrows(ReglaNegocioException.class, () -> auth.iniciarSesion(nombre, clave));
    }

    // ---------------- Registro (RF-01) ----------------

    @Test
    void registroValidoGuardaRolUsuarioYHashBCrypt() {
        Usuario u = auth.registrar("bryan_t", "Bryan@UNI.pe", CLAVE);

        assertTrue(u.getIdUsuario() > 0);
        assertEquals(Rol.USUARIO, u.getRol());
        assertTrue(u.isActivo());
        assertEquals("bryan@uni.pe", u.getCorreo());
        assertNotEquals(CLAVE, u.getHashContrasena());
        assertTrue(u.getHashContrasena().startsWith("$2a$12$"));
        assertTrue(PasswordHasher.verificar(CLAVE, u.getHashContrasena()));
    }

    @Test
    void registroRechazaUsuarioDuplicado() {
        cuenta("ana", Rol.USUARIO, true);
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> auth.registrar("ana", "otro@uni.pe", CLAVE));
        assertEquals("El nombre de usuario o el correo ya están registrados.", e.getMessage());
    }

    @Test
    void registroRechazaCorreoDuplicado() {
        cuenta("ana", Rol.USUARIO, true);
        assertThrows(ReglaNegocioException.class, () -> auth.registrar("luis", "ana@uni.pe", CLAVE));
    }

    @Test
    void registroRechazaCorreoInvalido() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> auth.registrar("luis", "sin-arroba", CLAVE));
        assertEquals("El correo no es válido.", e.getMessage());
    }

    @Test
    void registroRechazaClaveDebil() {
        assertThrows(ReglaNegocioException.class, () -> auth.registrar("luis", "luis@uni.pe", "corta1"));
        assertThrows(ReglaNegocioException.class, () -> auth.registrar("luis", "luis@uni.pe", "sinnumeros"));
        assertThrows(ReglaNegocioException.class, () -> auth.registrar("luis", "luis@uni.pe", "12345678"));
    }

    @Test
    void registroRechazaNombreDeUsuarioInvalido() {
        assertThrows(ReglaNegocioException.class, () -> auth.registrar("ab", "ab@uni.pe", CLAVE));
        assertThrows(ReglaNegocioException.class, () -> auth.registrar("' OR 1=1 --", "x@uni.pe", CLAVE));
    }

    // ---------------- Inicio de sesión (RF-02) ----------------

    @Test
    void loginCorrectoDevuelveElUsuario() {
        Usuario creado = cuenta("ana", Rol.USUARIO, true);
        Usuario u = auth.iniciarSesion("ana", CLAVE);
        assertEquals(creado.getIdUsuario(), u.getIdUsuario());
    }

    @Test
    void loginDeModeradorConservaSuRol() {
        cuenta("moderador1", Rol.MODERADOR, true);
        assertEquals(Rol.MODERADOR, auth.iniciarSesion("moderador1", CLAVE).getRol());
    }

    @Test
    void loginConClaveIncorrectaMuestraMensajeGenerico() {
        cuenta("ana", Rol.USUARIO, true);
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> auth.iniciarSesion("ana", "Otra12345"));
        assertEquals("Usuario o contraseña incorrectos", e.getMessage());
    }

    @Test
    void elMensajeEsElMismoSiElUsuarioNoExisteOEstaDesactivado() {
        cuenta("ana", Rol.USUARIO, true);
        cuenta("baja", Rol.USUARIO, false);
        String conClaveMala = assertThrows(ReglaNegocioException.class,
                () -> auth.iniciarSesion("ana", "Otra12345")).getMessage();
        String noExiste = assertThrows(ReglaNegocioException.class,
                () -> auth.iniciarSesion("fantasma", CLAVE)).getMessage();
        String desactivado = assertThrows(ReglaNegocioException.class,
                () -> auth.iniciarSesion("baja", CLAVE)).getMessage();
        assertEquals(conClaveMala, noExiste);
        assertEquals(conClaveMala, desactivado);
    }

    @Test
    void loginConCamposVaciosEsRechazado() {
        assertThrows(ReglaNegocioException.class, () -> auth.iniciarSesion("", CLAVE));
        assertThrows(ReglaNegocioException.class, () -> auth.iniciarSesion("ana", ""));
        assertThrows(ReglaNegocioException.class, () -> auth.iniciarSesion(null, null));
    }

    // ---------------- Bloqueo (RF-03) ----------------

    @Test
    void despuesDeCincoFallosSeguidosElAccesoSeBloquea() {
        cuenta("ana", Rol.USUARIO, true);
        for (int i = 0; i < 5; i++) {
            falla("ana", "Otra12345");
        }
        // Aun con la clave correcta, mientras dure el bloqueo no entra.
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> auth.iniciarSesion("ana", CLAVE));
        assertEquals(AuthService.ERROR_BLOQUEO, e.getMessage());
    }

    @Test
    void conCuatroFallosTodaviaPuedeEntrar() {
        cuenta("ana", Rol.USUARIO, true);
        for (int i = 0; i < 4; i++) {
            falla("ana", "Otra12345");
        }
        assertNotNull(auth.iniciarSesion("ana", CLAVE));
    }

    @Test
    void elBloqueoTerminaALosTresMinutos() {
        cuenta("ana", Rol.USUARIO, true);
        for (int i = 0; i < 5; i++) {
            falla("ana", "Otra12345");
        }
        reloj.avanzar(Duration.ofMinutes(2));
        falla("ana", CLAVE);                                    // sigue bloqueada
        reloj.avanzar(Duration.ofMinutes(1).plusSeconds(1));
        assertNotNull(auth.iniciarSesion("ana", CLAVE));        // ya pasaron 3 minutos
    }

    @Test
    void unLoginCorrectoReiniciaElContadorDeFallos() {
        cuenta("ana", Rol.USUARIO, true);
        for (int i = 0; i < 4; i++) {
            falla("ana", "Otra12345");
        }
        auth.iniciarSesion("ana", CLAVE);
        for (int i = 0; i < 4; i++) {
            falla("ana", "Otra12345");
        }
        assertNotNull(auth.iniciarSesion("ana", CLAVE));         // no acumuló 8 fallos
    }

    @Test
    void elBloqueoDeUnUsuarioNoAfectaAOtro() {
        cuenta("ana", Rol.USUARIO, true);
        cuenta("luis", Rol.USUARIO, true);
        for (int i = 0; i < 5; i++) {
            falla("ana", "Otra12345");
        }
        assertNotNull(auth.iniciarSesion("luis", CLAVE));
    }

    // ---------------- Cierre de sesión (RF-04) ----------------

    @Test
    void cerrarSesionLimpiaLaSesionActiva() {
        Usuario u = cuenta("ana", Rol.USUARIO, true);
        Sesion.iniciar(u);
        assertTrue(Sesion.haySesion());

        auth.cerrarSesion();

        assertFalse(Sesion.haySesion());
        assertNull(Sesion.getUsuario());
    }
}
