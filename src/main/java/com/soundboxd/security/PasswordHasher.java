package com.soundboxd.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Hash de contraseñas con BCrypt (costo 12).
 * Regla: una contraseña NUNCA se guarda ni se registra en texto plano.
 */
public final class PasswordHasher {

    private static final int COSTO = 12;

    private PasswordHasher() { }

    /** Devuelve el hash BCrypt (con sal incluida) de la contraseña. */
    public static String hash(String clave) {
        if (clave == null || clave.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        return BCrypt.hashpw(clave, BCrypt.gensalt(COSTO));
    }

    /** true si la contraseña corresponde al hash. Nunca lanza por datos inválidos. */
    public static boolean verificar(String clave, String hash) {
        if (clave == null || hash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(clave, hash);
        } catch (IllegalArgumentException e) {   // hash con formato inválido
            return false;
        }
    }
}
