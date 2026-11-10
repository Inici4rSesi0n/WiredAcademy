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
class TurnoTest {

    @Test
    void constructor_debeCrearTurnoConNombreCorrecto() {
        Turno turno = new Turno("Mañana");
        assertEquals("Mañana", turno.getNombre());
    }

    @Test
    void constructor_debeLanzarExcepcionSiNombreNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Turno(null));
    }

    @Test
    void constructor_debeLanzarExcepcionSiNombreVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Turno(""));
    }

    @Test
    void constructor_debeLanzarExcepcionSiNombreBlanco() {
        assertThrows(IllegalArgumentException.class,
                () -> new Turno("   "));
    }

    @Test
    void setNombre_debeActualizarNombre() {
        Turno turno = new Turno("Mañana");
        turno.setNombre("Tarde");
        assertEquals("Tarde", turno.getNombre());
    }

    @Test
    void equals_debeSerSimetrico() {
        Turno t1 = new Turno("Mañana");
        Turno t2 = new Turno("Mañana");
        assertEquals(t1, t2);
        assertEquals(t2, t1);
    }

    @Test
    void equals_debeIgnorarMayusculas() {
        Turno t1 = new Turno("mañana");
        Turno t2 = new Turno("Mañana");
        assertEquals(t1, t2);
    }

    @Test
    void equals_debeRetornarFalsoSiNombresDiferentes() {
        Turno t1 = new Turno("Mañana");
        Turno t2 = new Turno("Tarde");
        assertNotEquals(t1, t2);
    }

    @Test
    void hashCode_debeSerIgualParaNombresIgualesIgnorandoMayusculas() {
        Turno t1 = new Turno("mañana");
        Turno t2 = new Turno("Mañana");
        assertEquals(t1.hashCode(), t2.hashCode());
    }
}