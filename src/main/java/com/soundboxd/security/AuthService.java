package com.soundboxd.security;

import com.soundboxd.dao.UsuarioDAO;
import com.soundboxd.dao.UsuarioDAOImpl;
import com.soundboxd.model.Rol;
import com.soundboxd.model.Usuario;
import com.soundboxd.util.ReglaNegocioException;
import com.soundboxd.util.Validador;
import com.soundboxd.view.Sesion;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * M1: registro, inicio y cierre de sesión (RF-01 a RF-04).
 * <ul>
 *   <li>Las contraseñas se guardan solo como hash BCrypt (PasswordHasher); nunca en claro.</li>
 *   <li>El mensaje de un login fallido es siempre el mismo, sin decir qué dato falló.</li>
 *   <li>Tras 5 fallos seguidos de un mismo usuario, el acceso se bloquea 3 minutos.</li>
 *   <li>Todo registro nuevo es USUARIO; el moderador solo se crea desde la base de datos.</li>
 * </ul>
 * La pantalla de Johan llama a Sesion.iniciar(...) con el usuario que devuelve iniciarSesion.
 */
public class AuthService {

    static final String ERROR_LOGIN = "Usuario o contraseña incorrectos";
    static final String ERROR_BLOQUEO = "Demasiados intentos fallidos. Inténtalo de nuevo en unos minutos.";
    static final int MAX_FALLOS = 5;
    static final Duration BLOQUEO = Duration.ofMinutes(3);

    /** Hash de relleno: se compara cuando el usuario no existe, para que tarde igual y no delate cuentas. */
    private static final class Relleno {
        static final String HASH = PasswordHasher.hash("relleno-sin-uso-1");
    }

    private static final class Intentos {
        int fallos;
        Instant bloqueadoHasta;
    }

    private final UsuarioDAO usuarioDAO;
    private final Clock reloj;
    private final Map<String, Intentos> intentos = new ConcurrentHashMap<>();

    /** Uso normal de la aplicación: contra SQL Server. No abre conexión hasta que se usa. */
    public AuthService() {
        this(new UsuarioDAOImpl());
    }

    public AuthService(UsuarioDAO usuarioDAO) {
        this(usuarioDAO, Clock.systemDefaultZone());
    }

    /** Para pruebas: permite controlar la hora y comprobar el desbloqueo. */
    AuthService(UsuarioDAO usuarioDAO, Clock reloj) {
        this.usuarioDAO = usuarioDAO;
        this.reloj = reloj;
    }

    /** Valida los datos, rechaza duplicados y guarda la cuenta con hash BCrypt y rol USUARIO. */
    public Usuario registrar(String nombreUsuario, String correo, String clave) {
        if (!Validador.usuarioValido(nombreUsuario)) {
            throw new ReglaNegocioException("El usuario debe tener de 3 a 20 caracteres: letras, números o guion bajo.");
        }
        if (!Validador.correoValido(correo)) {
            throw new ReglaNegocioException("El correo no es válido.");
        }
        if (!Validador.claveValida(clave)) {
            throw new ReglaNegocioException("La contraseña debe tener al menos 8 caracteres, con letras y números.");
        }
        String correoNormal = correo.trim().toLowerCase();
        if (usuarioDAO.existeNombreUsuario(nombreUsuario) || usuarioDAO.existeCorreo(correoNormal)) {
            throw new ReglaNegocioException("El nombre de usuario o el correo ya están registrados.");
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombreUsuario(nombreUsuario);
        nuevo.setCorreo(correoNormal);
        nuevo.setHashContrasena(PasswordHasher.hash(clave));
        nuevo.setRol(Rol.USUARIO);
        nuevo.setFechaRegistro(LocalDateTime.now(reloj));
        nuevo.setActivo(true);
        nuevo.setIdUsuario(usuarioDAO.insertar(nuevo));
        return nuevo;
    }

    /** Devuelve el usuario si las credenciales son correctas; si no, un error siempre igual. */
    public Usuario iniciarSesion(String nombreUsuario, String clave) {
        if (nombreUsuario == null || nombreUsuario.isBlank() || clave == null || clave.isEmpty()) {
            throw new ReglaNegocioException(ERROR_LOGIN);
        }
        String llave = nombreUsuario.trim().toLowerCase();
        exigirNoBloqueado(llave);

        Usuario u = usuarioDAO.buscarPorNombreUsuario(nombreUsuario.trim()).orElse(null);
        String hash = (u == null) ? Relleno.HASH : u.getHashContrasena();
        boolean claveCorrecta = PasswordHasher.verificar(clave, hash);

        if (u == null || !claveCorrecta || !u.isActivo()) {
            registrarFallo(llave);
            throw new ReglaNegocioException(ERROR_LOGIN);
        }
        intentos.remove(llave);
        return u;
    }

    /** Limpia la sesión de la aplicación (RF-04). */
    public void cerrarSesion() {
        Sesion.cerrar();
    }

    private void exigirNoBloqueado(String llave) {
        Intentos i = intentos.get(llave);
        if (i == null || i.bloqueadoHasta == null) {
            return;
        }
        if (Instant.now(reloj).isBefore(i.bloqueadoHasta)) {
            throw new ReglaNegocioException(ERROR_BLOQUEO);
        }
        intentos.remove(llave);          // el bloqueo ya venció
    }

    private void registrarFallo(String llave) {
        Intentos i = intentos.computeIfAbsent(llave, k -> new Intentos());
        synchronized (i) {
            i.fallos++;
            if (i.fallos >= MAX_FALLOS) {
                i.bloqueadoHasta = Instant.now(reloj).plus(BLOQUEO);
                i.fallos = 0;
            }
        }
    }
}
