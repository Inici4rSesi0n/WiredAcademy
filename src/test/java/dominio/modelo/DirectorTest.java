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
class DirectorTest {

    @Test
    void constructor_debeCrearDirectorConRolCorrecto() {
        Director director = new Director("D001", "hash", "111", "Dir", "Uno", 40);

        assertEquals("D001", director.getCodigo());
        assertEquals("hash", director.getHashContrasena());
        assertEquals("111", director.getDni());
        assertEquals("Dir", director.getNombre());
        assertEquals("Uno", director.getApellido());
        assertEquals(40, director.getEdad());
        assertEquals(Usuario.Rol.DIRECTOR, director.getRol());
    }

    @Test
    void constructor_debeLanzarExcepcionConCodigoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Director(null, "hash", "111", "Dir", "Uno", 40));
    }

    @Test
    void constructor_debeLanzarExcepcionConEdadCero() {
        assertThrows(IllegalArgumentException.class,
                () -> new Director("D001", "hash", "111", "Dir", "Uno", 0));
    }
}