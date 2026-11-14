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
class GrupoTest {

    private Grado grado;
    private Grupo grupo;

    @BeforeEach
    void setUp() {
        grado = new Grado("1er Grado", "Primaria");
        grupo = new Grupo("1A", grado);
    }

    @Test
    void constructor_debeCrearGrupoConDatosCorrectos() {
        assertEquals("1A", grupo.getNombre());
        assertEquals(grado, grupo.getGrado());
        assertNull(grupo.getTutor());
        assertTrue(grupo.getEstudiantes().isEmpty());
    }

    @Test
    void constructor_debeLanzarExcepcionSiNombreNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Grupo(null, grado));
    }

    @Test
    void constructor_debeLanzarExcepcionSiNombreVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Grupo("", grado));
    }

    @Test
    void constructor_debeLanzarExcepcionSiGradoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Grupo("1A", null));
    }

    @Test
    void asignarTutor_debeEstablecerRelacionBidireccional() {
        Docente tutor = new Docente("D001", "hash", "111", "Juan", "Perez", 30, "Mat", new ArrayList<>());
        grupo.asignarTutor(tutor);

        assertEquals(tutor, grupo.getTutor());
        assertEquals(grupo, tutor.getTutoria());
    }

    @Test
    void asignarTutor_debeReemplazarTutorAnterior() {
        Docente tutor1 = new Docente("D001", "hash", "111", "Juan", "Perez", 30, "Mat", new ArrayList<>());
        Docente tutor2 = new Docente("D002", "hash2", "222", "Ana", "Lopez", 28, "Ing", new ArrayList<>());

        grupo.asignarTutor(tutor1);
        grupo.asignarTutor(tutor2);

        assertEquals(tutor2, grupo.getTutor());
        assertNull(tutor1.getTutoria());
        assertEquals(grupo, tutor2.getTutoria());
    }

    @Test
    void asignarTutor_null_debeQuitarTutor() {
        Docente tutor = new Docente("D001", "hash", "111", "Juan", "Perez", 30, "Mat", new ArrayList<>());
        grupo.asignarTutor(tutor);
        grupo.asignarTutor(null);

        assertNull(grupo.getTutor());
        assertNull(tutor.getTutoria());
    }

    @Test
    void agregarEstudiante_debeAgregarALista() {
        Estudiante estudiante = new Estudiante("E001", "hash", "111", "Carlos", "Gomez", 15);
        grupo.agregarEstudiante(estudiante);

        assertEquals(1, grupo.getEstudiantes().size());
        assertTrue(grupo.getEstudiantes().contains(estudiante));
    }

    @Test
    void agregarEstudiante_noDebeDuplicar() {
        Estudiante estudiante = new Estudiante("E001", "hash", "111", "Carlos", "Gomez", 15);
        grupo.agregarEstudiante(estudiante);
        grupo.agregarEstudiante(estudiante);

        assertEquals(1, grupo.getEstudiantes().size());
    }

    @Test
    void agregarEstudiante_debeLanzarExcepcionSiEstudianteNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> grupo.agregarEstudiante(null));
    }

    @Test
    void removerEstudiante_debeEliminarDeLista() {
        Estudiante e1 = new Estudiante("E001", "hash", "111", "Carlos", "Gomez", 15);
        Estudiante e2 = new Estudiante("E002", "hash", "222", "Maria", "Lopez", 16);
        grupo.agregarEstudiante(e1);
        grupo.agregarEstudiante(e2);

        grupo.removerEstudiante(e1);

        assertEquals(1, grupo.getEstudiantes().size());
        assertFalse(grupo.getEstudiantes().contains(e1));
        assertTrue(grupo.getEstudiantes().contains(e2));
    }

    @Test
    void setters_debenActualizarPropiedades() {
        grupo.setNombre("1B");
        Grado nuevoGrado = new Grado("2do Grado", "Primaria");
        grupo.setGrado(nuevoGrado);

        assertEquals("1B", grupo.getNombre());
        assertEquals(nuevoGrado, grupo.getGrado());
    }

    @Test
    void equals_debeCompararNombreYGrado() {
        Grupo g1 = new Grupo("1A", grado);
        Grupo g2 = new Grupo("1A", grado);

        assertEquals(g1, g2);
    }

    @Test
    void equals_debeRetornarFalsoSiDiferenteNombre() {
        Grupo g2 = new Grupo("1B", grado);
        assertNotEquals(grupo, g2);
    }

    @Test
    void equals_debeRetornarFalsoSiDiferenteGrado() {
        Grado otroGrado = new Grado("2do Grado", "Primaria");
        Grupo g2 = new Grupo("1A", otroGrado);
        assertNotEquals(grupo, g2);
    }

    @Test
    void equals_debeIgnorarCapitalizacion() {
        Grado grado = new Grado("1er Grado", "Primaria");
        Grupo g1 = new Grupo("1a", grado);
        Grupo g2 = new Grupo("1A", grado);
        assertEquals(g1, g2, "equals debe ser case-insensitive (ADR-002)");
        assertEquals(g2, g1);
    }

    @Test
    void constructor_debeAsignarModalidadPresencialPorDefecto() {
        assertEquals(Modalidad.PRESENCIAL, grupo.getModalidad());
    }

    @Test
    void constructor_debeAsignarModalidadVirtualExplicita() {
        Grupo g = new Grupo("1A", grado, Modalidad.VIRTUAL);
        assertEquals(Modalidad.VIRTUAL, g.getModalidad());
    }

    @Test
    void constructor_debeFallbackAPresencialSiModalidadNula() {
        Grupo g = new Grupo("1A", grado, null);
        assertEquals(Modalidad.PRESENCIAL, g.getModalidad());
    }

    @Test
    void setModalidad_debeActualizarValor() {
        grupo.setModalidad(Modalidad.REMOTO);
        assertEquals(Modalidad.REMOTO, grupo.getModalidad());
    }

    @Test
    void setModalidad_null_debeFallbackAPresencial() {
        grupo.setModalidad(Modalidad.VIRTUAL);
        grupo.setModalidad(null);
        assertEquals(Modalidad.PRESENCIAL, grupo.getModalidad());
    }

    @Test
    void equals_noDebeConsiderarModalidad() {
        Grado grado = new Grado("1er Grado", "Primaria");
        Grupo g1 = new Grupo("1A", grado, Modalidad.PRESENCIAL);
        Grupo g2 = new Grupo("1A", grado, Modalidad.VIRTUAL);
        assertEquals(g1, g2, "La modalidad no forma parte de la identidad del grupo");
    }

    @Test
    void hashCode_debeSerConsistenteConEquals() {
        Grado grado = new Grado("1er Grado", "Primaria");
        Grupo g1 = new Grupo("1a", grado);
        Grupo g2 = new Grupo("1A", grado);
        assertEquals(g1, g2);
        assertEquals(g1.hashCode(), g2.hashCode(),
                "Objetos iguales deben tener el mismo hashCode (contrato Object)");
    }
}
