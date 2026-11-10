/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author inici4rsesi0n
 */
class RecreoTest {

    private FranjaHoraria franja;
    private PeriodoAcademico periodoA;
    private PeriodoAcademico periodoB;

    @BeforeEach
    void setUp() {
        franja = new FranjaHoraria("Lunes", "10:00", "10:30");
        periodoA = new PeriodoAcademico("2026-I", "2026-01-01", "2026-06-30", "Activo");
        periodoB = new PeriodoAcademico("2026-II", "2026-07-01", "2026-12-31", "Activo");
    }

    @Test
    void constructor_debeCrearRecreoConDescripcionYPeriodos() {
        Recreo recreo = new Recreo(franja, "Recreo Largo", List.of(periodoA));
        assertEquals("Recreo Largo", recreo.getDescripcion());
        assertEquals("Lunes", recreo.getDiaSemana());
        assertEquals("10:00", recreo.getHoraInicio());
        assertEquals("10:30", recreo.getHoraFin());
        assertTrue(recreo.aplicaEn(periodoA));
        assertFalse(recreo.aplicaEn(periodoB));
    }

    @Test
    void constructor_debeAceptarMultiplesPeriodos() {
        Recreo recreo = new Recreo(franja, "Recreo", List.of(periodoA, periodoB));
        assertEquals(2, recreo.getPeriodos().size());
        assertTrue(recreo.aplicaEn(periodoA));
        assertTrue(recreo.aplicaEn(periodoB));
    }

    @Test
    void constructor_debeAsignarRecreoPorDefectoSiDescripcionNula() {
        Recreo recreo = new Recreo(franja, null, List.of(periodoA));
        assertEquals("Recreo", recreo.getDescripcion());
    }

    @Test
    void constructor_debeAsignarRecreoPorDefectoSiDescripcionVacia() {
        Recreo recreo = new Recreo(franja, "   ", List.of(periodoA));
        assertEquals("Recreo", recreo.getDescripcion());
    }

    @Test
    void constructor_conPeriodosNulos_debeCrearListaVacia() {
        Recreo recreo = new Recreo(franja, "Recreo", null);
        assertTrue(recreo.getPeriodos().isEmpty());
        assertFalse(recreo.aplicaEn(periodoA));
    }

    @Test
    void setDescripcion_debeActualizarDescripcion() {
        Recreo recreo = new Recreo(franja, "Recreo", List.of(periodoA));
        recreo.setDescripcion("Recreo Modificado");
        assertEquals("Recreo Modificado", recreo.getDescripcion());
    }

    @Test
    void aplicarA_debeAsociarPeriodoNuevo() {
        Recreo recreo = new Recreo(franja, "Recreo", List.of(periodoA));
        recreo.aplicarA(periodoB);
        assertTrue(recreo.aplicaEn(periodoB));
        assertEquals(2, recreo.getPeriodos().size());
    }

    @Test
    void aplicarA_noDebeDuplicarPeriodoExistente() {
        Recreo recreo = new Recreo(franja, "Recreo", List.of(periodoA));
        recreo.aplicarA(periodoA);
        assertEquals(1, recreo.getPeriodos().size());
    }

    @Test
    void removerDe_debeQuitarPeriodo() {
        Recreo recreo = new Recreo(franja, "Recreo", List.of(periodoA, periodoB));
        recreo.removerDe(periodoA);
        assertFalse(recreo.aplicaEn(periodoA));
        assertTrue(recreo.aplicaEn(periodoB));
    }

    @Test
    void getPeriodos_debeSerInmutable() {
        Recreo recreo = new Recreo(franja, "Recreo", List.of(periodoA));
        assertThrows(UnsupportedOperationException.class,
                () -> recreo.getPeriodos().add(periodoB));
    }

    @Test
    void equals_noDependeDePeriodos() {
        Recreo r1 = new Recreo(franja, "Recreo", List.of(periodoA));
        Recreo r2 = new Recreo(franja, "Recreo", List.of(periodoB));
        assertEquals(r1, r2);
    }

    @Test
    void equals_debeRetornarFalsoSiFranjaDiferente() {
        Recreo r1 = new Recreo(franja, "Recreo", List.of(periodoA));
        FranjaHoraria otraFranja = new FranjaHoraria("Martes", "10:00", "10:30");
        Recreo r2 = new Recreo(otraFranja, "Recreo", List.of(periodoA));
        assertNotEquals(r1, r2);
    }

    @Test
    void equals_debeRetornarFalsoSiDescripcionDiferente() {
        Recreo r1 = new Recreo(franja, "Recreo", List.of(periodoA));
        Recreo r2 = new Recreo(franja, "Recreo Largo", List.of(periodoA));
        assertNotEquals(r1, r2);
    }

    @Test
    void hashCode_noDependeDePeriodos() {
        Recreo r1 = new Recreo(franja, "Recreo", List.of(periodoA));
        Recreo r2 = new Recreo(franja, "Recreo", List.of(periodoB));
        assertEquals(r1.hashCode(), r2.hashCode());
    }
}
