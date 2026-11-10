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
class DocenteTest {

    private Docente docente;
    private Asignatura asignatura;

    @BeforeEach
    void setUp() {
        List<Asignatura> lista = new ArrayList<>();
        lista.add(new Asignatura("Matemáticas"));
        docente = new Docente("D001", "hash", "111", "Juan", "Perez", 30, "Matemáticas", lista);
        asignatura = new Asignatura("Física");
    }

    @Test
    void constructor_debeCrearDocenteConDatosCorrectos() {
        assertEquals("D001", docente.getCodigo());
        assertEquals("Juan", docente.getNombre());
        assertEquals("Perez", docente.getApellido());
        assertEquals("Matemáticas", docente.getEspecialidad());
        assertEquals(Usuario.Rol.DOCENTE, docente.getRol());
        assertEquals(1, docente.getAsignaturas().size());
    }

    @Test
    void constructor_debeLanzarExcepcionSiEspecialidadNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new Docente("D001", "hash", "111", "Juan", "Perez", 30, null, new ArrayList<>()));
    }

    @Test
    void agregarAsignatura_debeAgregarALista() {
        docente.agregarAsignatura(asignatura);

        assertEquals(2, docente.getAsignaturas().size());
        assertTrue(docente.getAsignaturas().contains(asignatura));
    }

    @Test
    void agregarAsignatura_noDebeDuplicar() {
        Asignatura repetida = new Asignatura("Matemáticas");
        docente.agregarAsignatura(repetida);

        assertEquals(1, docente.getAsignaturas().size());
    }

    @Test
    void removerAsignatura_debeEliminarDeLista() {
        docente.removerAsignatura(new Asignatura("Matemáticas"));

        assertEquals(0, docente.getAsignaturas().size());
    }

    @Test
    void setTutoria_debeAsignarGrupo() {
        Grupo grupo = new Grupo("1A", new Grado("1er Grado", "Primaria"));
        docente.setTutoria(grupo);

        assertEquals(grupo, docente.getTutoria());
    }

    @Test
    void getNombreCompleto_debeRetornarNombreYApellido() {
        assertEquals("Juan Perez", docente.getNombreCompleto());
    }

    @Test
    void setEspecialidad_debeActualizarEspecialidad() {
        docente.setEspecialidad("Física");
        assertEquals("Física", docente.getEspecialidad());
    }
}