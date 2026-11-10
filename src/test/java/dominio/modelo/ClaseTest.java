/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author inici4rsesi0n
 */
class ClaseTest {

    private FranjaHoraria franja;
    private PeriodoAcademico periodo;
    private Asignatura asignatura;
    private Docente docente;
    private Grupo grupo;
    private Aula aula;

    @BeforeEach
    void setUp() {
        franja = new FranjaHoraria("Lunes", "08:00", "10:00");
        periodo = new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", "Activo");
        asignatura = new Asignatura("Matemáticas");
        docente = new Docente("D001", "hash", "111", "Juan", "Perez", 30, "Mat", new ArrayList<>());
        grupo = new Grupo("1A", new Grado("1er Grado", "Primaria"));
        aula = new Aula("Aula 101", 40, "Pabellón 1", "Teoría");
    }

    @Test
    void constructor_debeCrearClaseCorrectamente() {
        Clase clase = new Clase(franja, periodo, asignatura, docente, grupo, aula);

        assertEquals(asignatura, clase.getAsignatura());
        assertEquals(docente, clase.getDocente());
        assertEquals(grupo, clase.getGrupo());
        assertEquals(aula, clase.getAula());
        assertEquals("Lunes", clase.getDiaSemana());
        assertEquals("08:00", clase.getHoraInicio());
        assertEquals("10:00", clase.getHoraFin());
    }

    @Test
    void constructor_debeLanzarExcepcionSiAsignaturaNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new Clase(franja, periodo, null, docente, grupo, aula));
    }

    @Test
    void constructor_debeLanzarExcepcionSiDocenteNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Clase(franja, periodo, asignatura, null, grupo, aula));
    }

    @Test
    void constructor_debeLanzarExcepcionSiGrupoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Clase(franja, periodo, asignatura, docente, null, aula));
    }

    @Test
    void constructor_debeLanzarExcepcionSiAulaNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new Clase(franja, periodo, asignatura, docente, grupo, null));
    }

    @Test
    void getDescripcion_debeRetornarFormatoCorrecto() {
        Clase clase = new Clase(franja, periodo, asignatura, docente, grupo, aula);

        String descripcion = clase.getDescripcion();

        assertTrue(descripcion.contains("Matemáticas"));
        assertTrue(descripcion.contains("Juan Perez"));
    }

    @Test
    void getDescripcion_debeManejarAsignaturaNula() {
        Clase clase = new Clase(franja, periodo, asignatura, docente, grupo, aula);
        clase.setAsignatura(null);

        String descripcion = clase.getDescripcion();
        assertTrue(descripcion.contains("Sin asignatura"));
    }

    @Test
    void getDescripcion_debeManejarDocenteNulo() {
        Clase clase = new Clase(franja, periodo, asignatura, docente, grupo, aula);
        clase.setDocente(null);

        String descripcion = clase.getDescripcion();
        assertTrue(descripcion.contains("Sin docente"));
    }

    @Test
    void equals_debeSerSimetrico() {
        Clase c1 = new Clase(franja, periodo, asignatura, docente, grupo, aula);
        Clase c2 = new Clase(franja, periodo, asignatura, docente, grupo, aula);

        assertEquals(c1, c2);
    }

    @Test
    void equals_debeRetornarFalsoSiFranjaDiferente() {
        Clase c1 = new Clase(franja, periodo, asignatura, docente, grupo, aula);
        FranjaHoraria otraFranja = new FranjaHoraria("Martes", "08:00", "10:00");
        Clase c2 = new Clase(otraFranja, periodo, asignatura, docente, grupo, aula);

        assertNotEquals(c1, c2);
    }

    @Test
    void setters_debenActualizarPropiedades() {
        Clase clase = new Clase(franja, periodo, asignatura, docente, grupo, aula);
        Asignatura nuevaAsignatura = new Asignatura("Física");
        Docente nuevoDocente = new Docente("D002", "hash2", "222", "Ana", "Lopez", 28, "Ing", new ArrayList<>());
        Grupo nuevoGrupo = new Grupo("1B", new Grado("1er Grado", "Primaria"));
        Aula nuevaAula = new Aula("Lab Física", 25, "Pabellón 2", "Laboratorio");

        clase.setAsignatura(nuevaAsignatura);
        clase.setDocente(nuevoDocente);
        clase.setGrupo(nuevoGrupo);
        clase.setAula(nuevaAula);

        assertEquals(nuevaAsignatura, clase.getAsignatura());
        assertEquals(nuevoDocente, clase.getDocente());
        assertEquals(nuevoGrupo, clase.getGrupo());
        assertEquals(nuevaAula, clase.getAula());
    }
}