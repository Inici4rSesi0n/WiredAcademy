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
class EstudianteTest {

    @Test
    void constructor_debeCrearEstudianteConRolCorrecto() {
        Estudiante estudiante = new Estudiante("E001", "hash", "111", "Est", "Uno", 15);

        assertEquals("E001", estudiante.getCodigo());
        assertEquals("hash", estudiante.getHashContrasena());
        assertEquals("111", estudiante.getDni());
        assertEquals("Est", estudiante.getNombre());
        assertEquals("Uno", estudiante.getApellido());
        assertEquals(15, estudiante.getEdad());
        assertEquals(Usuario.Rol.ESTUDIANTE, estudiante.getRol());
    }

    @Test
    void constructor_debeLanzarExcepcionConCodigoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Estudiante(null, "hash", "111", "Est", "Uno", 15));
    }

    @Test
    void constructor_debeLanzarExcepcionConEdadCero() {
        assertThrows(IllegalArgumentException.class,
                () -> new Estudiante("E001", "hash", "111", "Est", "Uno", 0));
    }

    @Test
    void getNombreCompleto_debeRetornarNombreYApellido() {
        Estudiante estudiante = new Estudiante("E001", "hash", "111", "Carlos", "Gomez", 15);
        assertEquals("Carlos Gomez", estudiante.getNombreCompleto());
    }
}