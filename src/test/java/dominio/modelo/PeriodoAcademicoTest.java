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
class PeriodoAcademicoTest {

    @Test
    void constructor_debeCrearPeriodoConDatosCorrectos() {
        PeriodoAcademico periodo = new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", "Activo");

        assertEquals("2026-I", periodo.getNombre());
        assertEquals("2026-01-01", periodo.getFechaInicio());
        assertEquals("2026-12-31", periodo.getFechaFin());
        assertEquals("Activo", periodo.getEstado());
    }

    @Test
    void constructor_debeAsignarActivoSiEstadoNulo() {
        PeriodoAcademico periodo = new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", null);
        assertEquals("Activo", periodo.getEstado());
    }

    @Test
    void constructor_debeAsignarActivoSiEstadoVacio() {
        PeriodoAcademico periodo = new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", "");
        assertEquals("Activo", periodo.getEstado());
    }

    @Test
    void constructor_debeLanzarExcepcionSiNombreNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new PeriodoAcademico(null, "2026-01-01", "2026-12-31", "Activo"));
    }

    @Test
    void constructor_debeLanzarExcepcionSiNombreVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new PeriodoAcademico("", "2026-01-01", "2026-12-31", "Activo"));
    }

    @Test
    void constructor_debeLanzarExcepcionSiFechaInicioNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new PeriodoAcademico("2026-I", null, "2026-12-31", "Activo"));
    }

    @Test
    void constructor_debeLanzarExcepcionSiFechaInicioVacia() {
        assertThrows(IllegalArgumentException.class,
                () -> new PeriodoAcademico("2026-I", "", "2026-12-31", "Activo"));
    }

    @Test
    void constructor_debeLanzarExcepcionSiFechaFinNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new PeriodoAcademico("2026-I", "2026-01-01", null, "Activo"));
    }

    @Test
    void constructor_debeLanzarExcepcionSiFechaFinVacia() {
        assertThrows(IllegalArgumentException.class,
                () -> new PeriodoAcademico("2026-I", "2026-01-01", "", "Activo"));
    }

    @Test
    void setters_debenActualizarPropiedades() {
        PeriodoAcademico periodo = new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", "Activo");

        periodo.setNombre("2026-II");
        periodo.setFechaInicio("2026-07-01");
        periodo.setFechaFin("2026-12-31");
        periodo.setEstado("Culminado");

        assertEquals("2026-II", periodo.getNombre());
        assertEquals("2026-07-01", periodo.getFechaInicio());
        assertEquals("2026-12-31", periodo.getFechaFin());
        assertEquals("Culminado", periodo.getEstado());
    }

    @Test
    void equals_debeSerSimetrico() {
        PeriodoAcademico p1 = new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", "Activo");
        PeriodoAcademico p2 = new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", "Activo");
        assertEquals(p1, p2);
    }

    @Test
    void equals_debeIgnorarMayusculas() {
        PeriodoAcademico p1 = new PeriodoAcademico("2026-i", "2026-01-01", "2026-12-31", "Activo");
        PeriodoAcademico p2 = new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", "Activo");
        assertEquals(p1, p2);
    }

    @Test
    void equals_debeRetornarFalsoSiNombresDiferentes() {
        PeriodoAcademico p1 = new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", "Activo");
        PeriodoAcademico p2 = new PeriodoAcademico("2026-II", "2026-01-01", "2026-12-31", "Activo");
        assertNotEquals(p1, p2);
    }

    @Test
    void hashCode_debeSerIgualParaNombresIgualesIgnorandoMayusculas() {
        PeriodoAcademico p1 = new PeriodoAcademico("2026-i", "2026-01-01", "2026-12-31", "Activo");
        PeriodoAcademico p2 = new PeriodoAcademico("2026-I", "2026-07-01", "2026-12-31", "Culminado");
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}