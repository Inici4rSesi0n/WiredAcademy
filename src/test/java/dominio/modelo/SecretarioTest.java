/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author inici4rsesi0n
 */
class SecretarioTest {

    @Test
    void constructor_debeCrearSecretarioConRolCorrecto() {
        Secretario secretario = new Secretario("S001", "hash", "111", "Sec", "Uno", 30);

        assertEquals("S001", secretario.getCodigo());
        assertEquals("hash", secretario.getHashContrasena());
        assertEquals("111", secretario.getDni());
        assertEquals("Sec", secretario.getNombre());
        assertEquals("Uno", secretario.getApellido());
        assertEquals(30, secretario.getEdad());
        assertEquals(Usuario.Rol.SECRETARIO, secretario.getRol());
    }

    @Test
    void constructor_debeLanzarExcepcionConCodigoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Secretario(null, "hash", "111", "Sec", "Uno", 30));
    }

    @Test
    void constructor_debeLanzarExcepcionConEdadCero() {
        assertThrows(IllegalArgumentException.class,
                () -> new Secretario("S001", "hash", "111", "Sec", "Uno", 0));
    }
}