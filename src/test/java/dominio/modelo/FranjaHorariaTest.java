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
class FranjaHorariaTest {

    @Test
    void constructor_debeCrearConHorasCorrectas() {
        FranjaHoraria franja = new FranjaHoraria("Lunes", "08:00", "10:00");
        assertEquals("Lunes", franja.getDiaSemana());
        assertEquals("08:00", franja.getHoraInicio());
        assertEquals("10:00", franja.getHoraFin());
    }

    @Test
    void constructor_debeLanzarExcepcionSiDiaVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new FranjaHoraria("", "08:00", "10:00"));
    }

    @Test
    void constructor_debeLanzarExcepcionSiDiaNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new FranjaHoraria(null, "08:00", "10:00"));
    }

    @Test
    void constructor_debeLanzarExcepcionSiHoraInicioVacia() {
        assertThrows(IllegalArgumentException.class,
                () -> new FranjaHoraria("Lunes", "", "10:00"));
    }

    @Test
    void constructor_debeLanzarExcepcionSiHoraFinVacia() {
        assertThrows(IllegalArgumentException.class,
                () -> new FranjaHoraria("Lunes", "08:00", ""));
    }

    @Test
    void constructor_debeLanzarExcepcionSiHoraInicioEsMayorOIgualAHoraFin() {
        assertThrows(IllegalArgumentException.class,
                () -> new FranjaHoraria("Lunes", "10:00", "08:00"));
        assertThrows(IllegalArgumentException.class,
                () -> new FranjaHoraria("Lunes", "08:00", "08:00"));
    }

    @Test
    void equals_debeSerSimetrico() {
        FranjaHoraria f1 = new FranjaHoraria("Lunes", "08:00", "10:00");
        FranjaHoraria f2 = new FranjaHoraria("Lunes", "08:00", "10:00");
        assertEquals(f1, f2);
    }

    @Test
    void equals_debeRetornarFalsoSiDiferente() {
        FranjaHoraria f1 = new FranjaHoraria("Lunes", "08:00", "10:00");
        FranjaHoraria f2 = new FranjaHoraria("Martes", "08:00", "10:00");
        assertNotEquals(f1, f2);
    }
}