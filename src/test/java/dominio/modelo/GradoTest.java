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
class GradoTest {

    @Test
    void constructor_debeCrearGradoConDatosCorrectos() {
        Grado grado = new Grado("1er Grado", "Primaria");
        assertEquals("1er Grado", grado.getNombre());
        assertEquals("Primaria", grado.getNivel());
    }

    @Test
    void constructor_debeLanzarExcepcionSiNombreNulo() {
        assertThrows(IllegalArgumentException.class, () -> new Grado(null, "Primaria"));
    }

    @Test
    void constructor_debeLanzarExcepcionSiNombreVacio() {
        assertThrows(IllegalArgumentException.class, () -> new Grado("", "Primaria"));
    }

    @Test
    void constructor_debeLanzarExcepcionSiNombreBlanco() {
        assertThrows(IllegalArgumentException.class, () -> new Grado("   ", "Primaria"));
    }

    @Test
    void constructor_debeLanzarExcepcionSiNivelNulo() {
        assertThrows(IllegalArgumentException.class, () -> new Grado("1er Grado", null));
    }

    @Test
    void constructor_debeLanzarExcepcionSiNivelVacio() {
        assertThrows(IllegalArgumentException.class, () -> new Grado("1er Grado", ""));
    }

    @Test
    void setters_debenActualizarPropiedades() {
        Grado grado = new Grado("1er Grado", "Primaria");
        grado.setNombre("2do Grado");
        grado.setNivel("Secundaria");
        assertEquals("2do Grado", grado.getNombre());
        assertEquals("Secundaria", grado.getNivel());
    }

    @Test
    void equals_debeSerSimetrico() {
        Grado g1 = new Grado("1er Grado", "Primaria");
        Grado g2 = new Grado("1er Grado", "Primaria");
        assertEquals(g1, g2);
        assertEquals(g2, g1);
    }

    @Test
    void equals_debeIgnorarMayusculas() {
        Grado g1 = new Grado("1er grado", "primaria");
        Grado g2 = new Grado("1er Grado", "Primaria");
        assertEquals(g1, g2);
    }

    @Test
    void equals_debeRetornarFalsoSiNombresDiferentes() {
        Grado g1 = new Grado("1er Grado", "Primaria");
        Grado g2 = new Grado("2do Grado", "Primaria");
        assertNotEquals(g1, g2);
    }

    @Test
    void equals_debeRetornarFalsoSiNivelesDiferentes() {
        Grado g1 = new Grado("1er Grado", "Primaria");
        Grado g2 = new Grado("1er Grado", "Secundaria");
        assertNotEquals(g1, g2);
    }

    @Test
    void hashCode_debeSerIgualParaGradosEquivalentes() {
        Grado g1 = new Grado("1er grado", "primaria");
        Grado g2 = new Grado("1er Grado", "Primaria");
        assertEquals(g1.hashCode(), g2.hashCode());
    }

    @Test
    void hashCode_debeSerDiferenteSiNivelesDiferentes() {
        Grado g1 = new Grado("1er Grado", "Primaria");
        Grado g2 = new Grado("1er Grado", "Secundaria");
        assertNotEquals(g1.hashCode(), g2.hashCode());
    }
}
