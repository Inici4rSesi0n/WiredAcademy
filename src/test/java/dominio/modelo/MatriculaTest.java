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
class MatriculaTest {
    private Estudiante estudiante;
    private PeriodoAcademico periodo;
    private Grupo grupo;
    private List<Asignatura> asignaturas;
    @BeforeEach
    void setUp() {
        estudiante = new Estudiante("E001", "hash", "111", "Carlos", "Gomez", 15);
        periodo = new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", "Activo");
        grupo = new Grupo("1A", new Grado("1er Grado", "Primaria"));
        asignaturas = new ArrayList<>();
        asignaturas.add(new Asignatura("Matemáticas"));
        asignaturas.add(new Asignatura("Física"));
    }
    @Test
    void constructor_debeCrearMatriculaConDatosCorrectos() {
        Matricula matricula = new Matricula(estudiante, periodo, grupo, asignaturas, "2026-01-10", EstadoMatricula.ACTIVA);
        assertEquals(estudiante, matricula.getEstudiante());
        assertEquals(periodo, matricula.getPeriodo());
        assertEquals(grupo, matricula.getGrupo());
        assertEquals(2, matricula.getInscripciones().size());
        assertEquals("2026-01-10", matricula.getFecha());
        assertEquals(EstadoMatricula.ACTIVA, matricula.getEstado());
    }
    @Test
    void constructor_legacy_debeAsignarGrupoAInscripciones() {
        Matricula matricula = new Matricula(estudiante, periodo, grupo, asignaturas, "2026-01-10", EstadoMatricula.ACTIVA);
        for (InscripcionAsignatura ins : matricula.getInscripciones()) {
            assertEquals(grupo, ins.getGrupo());
        }
    }
    @Test
    void constructor_debeAsignarFechaVaciaPorDefecto() {
        Matricula matricula = new Matricula(estudiante, periodo, grupo, null, null, EstadoMatricula.ACTIVA);
        assertEquals("", matricula.getFecha());
    }
    @Test
    void constructor_debeAsignarActivaSiEstadoNulo() {
        Matricula matricula = new Matricula(estudiante, periodo, grupo, null, "2026-01-10", null);
        assertEquals(EstadoMatricula.ACTIVA, matricula.getEstado());
    }
    @Test
    void constructor_debeLanzarExcepcionSiEstudianteNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Matricula(null, periodo, grupo, asignaturas, "2026-01-10", EstadoMatricula.ACTIVA));
    }
    @Test
    void constructor_debeLanzarExcepcionSiPeriodoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Matricula(estudiante, null, grupo, asignaturas, "2026-01-10", EstadoMatricula.ACTIVA));
    }
    @Test
    void constructor_debeLanzarExcepcionSiGrupoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Matricula(estudiante, periodo, null, asignaturas, "2026-01-10", EstadoMatricula.ACTIVA));
    }
    @Test
    void agregarInscripcion_debeAgregarALista() {
        Matricula matricula = new Matricula(estudiante, periodo, grupo, new ArrayList<>(), "2026-01-10", EstadoMatricula.ACTIVA);
        Asignatura nueva = new Asignatura("Química");
        matricula.agregarInscripcion(new InscripcionAsignatura(nueva, grupo));
        assertEquals(1, matricula.getInscripciones().size());
        assertEquals(nueva, matricula.getInscripciones().get(0).getAsignatura());
    }
    @Test
    void agregarInscripcion_noDebeDuplicar() {
        Matricula matricula = new Matricula(estudiante, periodo, grupo, asignaturas, "2026-01-10", EstadoMatricula.ACTIVA);
        Asignatura duplicada = new Asignatura("Matemáticas");
        matricula.agregarInscripcion(new InscripcionAsignatura(duplicada, grupo));
        assertEquals(2, matricula.getInscripciones().size());
    }
    @Test
    void agregarInscripcion_debeLanzarExcepcionSiEsNula() {
        Matricula matricula = new Matricula(estudiante, periodo, grupo, new ArrayList<>(), "2026-01-10", EstadoMatricula.ACTIVA);
        assertThrows(IllegalArgumentException.class,
                () -> matricula.agregarInscripcion(null));
    }
    @Test
    void removerInscripcion_debeEliminarDeLista() {
        Matricula matricula = new Matricula(estudiante, periodo, grupo, asignaturas, "2026-01-10", EstadoMatricula.ACTIVA);
        InscripcionAsignatura ins = matricula.getInscripciones().get(0);
        matricula.removerInscripcion(ins);
        assertEquals(1, matricula.getInscripciones().size());
        assertFalse(matricula.getInscripciones().contains(ins));
    }
    @Test
    void setEstado_debeActualizarEstado() {
        Matricula matricula = new Matricula(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        matricula.setEstado(EstadoMatricula.RETIRADA);
        assertEquals(EstadoMatricula.RETIRADA, matricula.getEstado());
    }
    @Test
    void equals_debeSerSimetrico() {
        Matricula m1 = new Matricula(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        Matricula m2 = new Matricula(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        assertEquals(m1, m2);
    }
    @Test
    void equals_debeRetornarFalsoSiDiferenteEstudiante() {
        Estudiante otro = new Estudiante("E002", "hash2", "222", "Maria", "Lopez", 16);
        Matricula m1 = new Matricula(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        Matricula m2 = new Matricula(otro, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        assertNotEquals(m1, m2);
    }
    @Test
    void hashCode_debeSerConsistente() {
        Matricula m1 = new Matricula(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        Matricula m2 = new Matricula(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        assertEquals(m1.hashCode(), m2.hashCode());
    }
    @Test
    void toString_debeContenerCodigoEstudianteYEstado() {
        Matricula matricula = new Matricula(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        String texto = matricula.toString();
        assertTrue(texto.contains("E001"));
        assertTrue(texto.contains("ACTIVA"));
    }
}
