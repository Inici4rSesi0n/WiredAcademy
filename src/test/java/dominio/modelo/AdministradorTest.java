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
class AdministradorTest {

    @Test
    void constructor_debeCrearAdministradorConRolCorrecto() {
        Administrador admin = new Administrador("A001", "hash", "111", "Admin", "Uno", 30);

        assertEquals("A001", admin.getCodigo());
        assertEquals("hash", admin.getHashContrasena());
        assertEquals("111", admin.getDni());
        assertEquals("Admin", admin.getNombre());
        assertEquals("Uno", admin.getApellido());
        assertEquals(30, admin.getEdad());
        assertEquals(Usuario.Rol.ADMINISTRADOR, admin.getRol());
    }

    @Test
    void constructor_debeLanzarExcepcionConCodigoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Administrador(null, "hash", "111", "Admin", "Uno", 30));
    }

    @Test
    void constructor_debeLanzarExcepcionConEdadCero() {
        assertThrows(IllegalArgumentException.class,
                () -> new Administrador("A001", "hash", "111", "Admin", "Uno", 0));
    }
}