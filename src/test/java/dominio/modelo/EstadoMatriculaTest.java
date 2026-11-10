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
class EstadoMatriculaTest {

    @Test
    void valoresDebenSerTres() {
        assertEquals(3, EstadoMatricula.values().length);
    }

    @Test
    void fromStringDebeRetornarActivaConTextoNull() {
        assertEquals(EstadoMatricula.ACTIVA, EstadoMatricula.fromString(null));
    }

    @Test
    void fromStringDebeRetornarActivaConTextoVacio() {
        assertEquals(EstadoMatricula.ACTIVA, EstadoMatricula.fromString(""));
    }

    @Test
    void fromStringDebeRetornarActivaConTextoBlanco() {
        assertEquals(EstadoMatricula.ACTIVA, EstadoMatricula.fromString("   "));
    }

    @Test
    void fromStringDebeReconocerActiva() {
        assertEquals(EstadoMatricula.ACTIVA, EstadoMatricula.fromString("Activa"));
    }

    @Test
    void fromStringDebeIgnorarMayusculas() {
        assertEquals(EstadoMatricula.RETIRADA, EstadoMatricula.fromString("retirada"));
        assertEquals(EstadoMatricula.CULMINADA, EstadoMatricula.fromString("Culminada"));
    }

    @Test
    void fromStringDebeRetornarActivaConTextoInvalido() {
        assertEquals(EstadoMatricula.ACTIVA, EstadoMatricula.fromString("inexistente"));
    }
}