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
class AulaTest {

    @Test
    void constructor_debeCrearAulaConDatosCorrectos() {
        Aula aula = new Aula("Laboratorio", 30, "Edificio A", "Laboratorio");
        assertEquals("Laboratorio", aula.getNombre());
        assertEquals(30, aula.getCapacidad());
        assertEquals("Edificio A", aula.getUbicacion());
        assertEquals("Laboratorio", aula.getTipo());
        assertEquals(ModalidadAula.PRESENCIAL, aula.getModalidad());
    }

    @Test
    void constructor_legacy_debeAsignarPresencialPorDefecto() {
        Aula aula = new Aula("Aula 101", 40, "Pabellón 1", "Teoría");
        assertEquals(ModalidadAula.PRESENCIAL, aula.getModalidad());
    }

    @Test
    void constructor_conModalidad_debeAsignarModalidadCorrecta() {
        Aula aula = new Aula("Zoom-101", 50, "Plataforma", "Teoría", ModalidadAula.VIRTUAL);
        assertEquals("Zoom-101", aula.getNombre());
        assertEquals(50, aula.getCapacidad());
        assertEquals("Plataforma", aula.getUbicacion());
        assertEquals("Teoría", aula.getTipo());
        assertEquals(ModalidadAula.VIRTUAL, aula.getModalidad());
    }

    @Test
    void constructor_conModalidadNula_debeAsignarPresencial() {
        Aula aula = new Aula("Híbrida", 30, "Pabellón 3", "Teoría", null);
        assertEquals(ModalidadAula.PRESENCIAL, aula.getModalidad());
    }

    @Test
    void setModalidad_debeActualizarModalidad() {
        Aula aula = new Aula("Aula 101", 40, "Pabellón 1", "Teoría");
        aula.setModalidad(ModalidadAula.REMOTO);
        assertEquals(ModalidadAula.REMOTO, aula.getModalidad());
    }

    @Test
    void constructor_debeLanzarExcepcionSiNombreNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Aula(null, 30, "Edificio A", "Laboratorio"));
    }

    @Test
    void constructor_debeLanzarExcepcionSiNombreVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Aula("", 30, "Edificio A", "Laboratorio"));
    }

    @Test
    void constructor_debeLanzarExcepcionSiCapacidadCero() {
        assertThrows(IllegalArgumentException.class,
                () -> new Aula("Lab", 0, "Edificio A", "Laboratorio"));
    }

    @Test
    void constructor_debeLanzarExcepcionSiCapacidadNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> new Aula("Lab", -5, "Edificio A", "Laboratorio"));
    }

    @Test
    void constructor_debeLanzarExcepcionSiUbicacionNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new Aula("Lab", 30, null, "Laboratorio"));
    }

    @Test
    void constructor_debeLanzarExcepcionSiUbicacionVacia() {
        assertThrows(IllegalArgumentException.class,
                () -> new Aula("Lab", 30, "", "Laboratorio"));
    }

    @Test
    void constructor_debeLanzarExcepcionSiTipoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Aula("Lab", 30, "Edificio A", null));
    }

    @Test
    void constructor_debeLanzarExcepcionSiTipoVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Aula("Lab", 30, "Edificio A", ""));
    }

    @Test
    void equals_debeSerSimetrico() {
        Aula a1 = new Aula("Aula 101", 40, "Pabellón 1", "Teoría");
        Aula a2 = new Aula("Aula 101", 30, "Otro lugar", "Laboratorio");
        assertEquals(a1, a2);
    }

    @Test
    void equals_debeIgnorarMayusculas() {
        Aula a1 = new Aula("aula 101", 40, "Pabellón 1", "Teoría");
        Aula a2 = new Aula("Aula 101", 30, "Otro lugar", "Laboratorio");
        assertEquals(a1, a2);
    }

    @Test
    void equals_debeIgnorarModalidad() {
        Aula a1 = new Aula("Aula 101", 40, "Pabellón 1", "Teoría", ModalidadAula.PRESENCIAL);
        Aula a2 = new Aula("Aula 101", 30, "Otro lugar", "Laboratorio", ModalidadAula.VIRTUAL);
        assertEquals(a1, a2);
    }

    @Test
    void hashCode_debeSerIgualParaNombresIgualesIgnorandoModalidad() {
        Aula a1 = new Aula("Aula 101", 40, "Pabellón 1", "Teoría", ModalidadAula.PRESENCIAL);
        Aula a2 = new Aula("Aula 101", 30, "Otro lugar", "Laboratorio", ModalidadAula.VIRTUAL);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    void equals_debeRetornarFalsoSiNombresDiferentes() {
        Aula a1 = new Aula("Aula 101", 40, "Pabellón 1", "Teoría");
        Aula a2 = new Aula("Aula 102", 40, "Pabellón 1", "Teoría");
        assertNotEquals(a1, a2);
    }

    @Test
    void setters_debenActualizarPropiedades() {
        Aula aula = new Aula("Aula 101", 40, "Pabellón 1", "Teoría");
        aula.setNombre("Aula Magna");
        aula.setCapacidad(100);
        aula.setUbicacion("Rectorado");
        aula.setTipo("Auditorio");
        aula.setModalidad(ModalidadAula.REMOTO);

        assertEquals("Aula Magna", aula.getNombre());
        assertEquals(100, aula.getCapacidad());
        assertEquals("Rectorado", aula.getUbicacion());
        assertEquals("Auditorio", aula.getTipo());
        assertEquals(ModalidadAula.REMOTO, aula.getModalidad());
    }

    @Test
    void setModalidad_conNull_asignaPresencial() {
        Aula aula = new Aula("Aula 101", 40, "Pabellón 1", "Teoría");
        aula.setModalidad(ModalidadAula.VIRTUAL);
        aula.setModalidad(null);
        assertEquals(ModalidadAula.PRESENCIAL, aula.getModalidad(),
                "setModalidad(null) debe fallback a PRESENCIAL para evitar NPE en AulaMapper.toEntity");
    }
}
