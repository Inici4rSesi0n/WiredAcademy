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
class AsignaturaTest {

    @Test
    void constructor_debeCrearAsignaturaConNombreCorrecto() {
        Asignatura asignatura = new Asignatura("Matemáticas");
        assertEquals("Matemáticas", asignatura.getNombre());
    }

    @Test
    void constructor_debeLanzarExcepcionSiNombreNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Asignatura(null));
    }

    @Test
    void constructor_debeLanzarExcepcionSiNombreVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Asignatura(""));
    }

    @Test
    void constructor_debeLanzarExcepcionSiNombreBlanco() {
        assertThrows(IllegalArgumentException.class,
                () -> new Asignatura("   "));
    }

    @Test
    void equals_debeSerSimetrico() {
        Asignatura a1 = new Asignatura("Matemáticas");
        Asignatura a2 = new Asignatura("Matemáticas");
        assertTrue(a1.equals(a2));
        assertTrue(a2.equals(a1));
    }

    @Test
    void equals_debeIgnorarMayusculas() {
        Asignatura a1 = new Asignatura("matemáticas");
        Asignatura a2 = new Asignatura("Matemáticas");
        assertEquals(a1, a2);
    }

    @Test
    void equals_debeRetornarFalsoSiNombresDiferentes() {
        Asignatura a1 = new Asignatura("Matemáticas");
        Asignatura a2 = new Asignatura("Física");
        assertNotEquals(a1, a2);
    }

    @Test
    void hashCode_debeSerIgualParaNombresIgualesIgnorandoMayusculas() {
        Asignatura a1 = new Asignatura("matemáticas");
        Asignatura a2 = new Asignatura("Matemáticas");
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    void setNombre_debeActualizarNombre() {
        Asignatura asignatura = new Asignatura("Matemáticas");
        asignatura.setNombre("Álgebra");
        assertEquals("Álgebra", asignatura.getNombre());
    }
}