/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.seguridad;
import dominio.puerto.externo.HashProvider;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;
/**
 *
 * @author inici4rsesi0n
 */
@Component
public class BCryptHashProvider implements HashProvider {
    private static final int FACTOR_COSTO = 12;
    @Override
    public String generarHash(char[] contraseña) {
        if (contraseña == null) return null;
        String hash = BCrypt.hashpw(new String(contraseña), BCrypt.gensalt(FACTOR_COSTO));
        UtilLimpieza.limpiarContraseña(contraseña);
        return hash;
    }
    @Override
    public boolean verificarHash(String hashAlmacenado, char[] contraseña) {
        if (hashAlmacenado == null || contraseña == null) return false;
        try {
            return BCrypt.checkpw(new String(contraseña), hashAlmacenado);
        } catch (IllegalArgumentException e) {
            return false;
        } finally {
            UtilLimpieza.limpiarContraseña(contraseña);
        }
    }
}