/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.miareapersonal;

import dominio.modelo.*;
import dominio.puerto.repositorio.RepositorioClases;
import dominio.puerto.repositorio.RepositorioRecreos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author inici4rsesi0n
 */
class GestionHorarioTest {

    private RepositorioClases repoClases;
    private RepositorioRecreos repoRecreos;
    private GestionHorario casoUso;

    private Grupo grupo;
    private PeriodoAcademico periodo;
    private Asignatura asignatura;
    private Docente docente;
    private Aula aula;

    @BeforeEach
    void setUp() {
        repoClases = mock(RepositorioClases.class);
        repoRecreos = mock(RepositorioRecreos.class);
        casoUso = new GestionHorario(repoClases, repoRecreos);

        grupo = new Grupo("1A", new Grado("1er Grado", "Primaria"));
        periodo = new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", "Activo");
        asignatura = new Asignatura("Matemáticas");
        docente = new Docente("D001", "hash", "123", "Juan", "Perez", 30, "Mat", new ArrayList<>());
        aula = new Aula("Aula 101", 40, "Pabellón 1", "Teoría");
    }

    @Test
    void obtenerHorarioCompleto_debeLanzarExcepcionSiGrupoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.obtenerHorarioCompleto(null, periodo));
    }

    @Test
    void obtenerHorarioCompleto_debeLanzarExcepcionSiPeriodoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.obtenerHorarioCompleto(grupo, null));
    }

    @Test
    void obtenerHorarioCompleto_debeIncluirClasesYRecreosOrdenados() {
        FranjaHoraria franjaClase = new FranjaHoraria("Lunes", "08:00", "10:00");
        Clase clase = new Clase(franjaClase, periodo, asignatura, docente, grupo, aula);

        FranjaHoraria franjaRecreo = new FranjaHoraria("Lunes", "10:00", "10:30");
        Recreo recreo = new Recreo(franjaRecreo, "Recreo", List.of(periodo));

        when(repoClases.listarTodos()).thenReturn(List.of(clase));
        when(repoRecreos.listarTodos()).thenReturn(List.of(recreo));

        List<Evento> horario = casoUso.obtenerHorarioCompleto(grupo, periodo);

        assertEquals(2, horario.size());
        assertEquals(clase, horario.get(0));
        assertEquals(recreo, horario.get(1));
    }

    @Test
    void obtenerHorarioCompleto_debeFiltrarClasesPorGrupoYPeriodo() {
        Grupo otroGrupo = new Grupo("1B", new Grado("1er Grado", "Primaria"));
        PeriodoAcademico otroPeriodo = new PeriodoAcademico("2026-II", "2026-07-01", "2026-12-31", "Activo");

        FranjaHoraria franja = new FranjaHoraria("Lunes", "08:00", "10:00");
        Clase claseCorrecta = new Clase(franja, periodo, asignatura, docente, grupo, aula);
        Clase claseOtroGrupo = new Clase(franja, periodo, asignatura, docente, otroGrupo, aula);
        Clase claseOtroPeriodo = new Clase(franja, otroPeriodo, asignatura, docente, grupo, aula);

        when(repoClases.listarTodos()).thenReturn(List.of(claseCorrecta, claseOtroGrupo, claseOtroPeriodo));
        when(repoRecreos.listarTodos()).thenReturn(List.of());

        List<Evento> horario = casoUso.obtenerHorarioCompleto(grupo, periodo);

        assertEquals(1, horario.size());
        assertTrue(horario.contains(claseCorrecta));
    }

    @Test
    void obtenerHorarioCompleto_debeFiltrarRecreosPorPeriodo() {
        PeriodoAcademico otroPeriodo = new PeriodoAcademico("2026-II", "2026-07-01", "2026-12-31", "Activo");
        FranjaHoraria franja = new FranjaHoraria("Lunes", "10:00", "10:30");
        Recreo recreoCorrecto = new Recreo(franja, "Recreo", List.of(periodo));
        Recreo recreoOtroPeriodo = new Recreo(franja, "Recreo", List.of(otroPeriodo));

        when(repoClases.listarTodos()).thenReturn(List.of());
        when(repoRecreos.listarTodos()).thenReturn(List.of(recreoCorrecto, recreoOtroPeriodo));

        List<Evento> horario = casoUso.obtenerHorarioCompleto(grupo, periodo);

        assertEquals(1, horario.size());
        assertTrue(horario.contains(recreoCorrecto));
    }

    @Test
    void obtenerHorarioCompleto_debeOrdenarDiasPorOrdenSemanal() {
        FranjaHoraria franjaVie = new FranjaHoraria("Viernes", "08:00", "10:00");
        FranjaHoraria franjaLun = new FranjaHoraria("Lunes", "08:00", "10:00");
        FranjaHoraria franjaMie = new FranjaHoraria("Miércoles", "08:00", "10:00");
        Clase cVie = new Clase(franjaVie, periodo, asignatura, docente, grupo, aula);
        Clase cLun = new Clase(franjaLun, periodo, asignatura, docente, grupo, aula);
        Clase cMie = new Clase(franjaMie, periodo, asignatura, docente, grupo, aula);
        when(repoClases.listarTodos()).thenReturn(List.of(cVie, cLun, cMie));
        when(repoRecreos.listarTodos()).thenReturn(List.of());
        List<Evento> horario = casoUso.obtenerHorarioCompleto(grupo, periodo);
        assertEquals(3, horario.size());
        assertEquals("Lunes", horario.get(0).getDiaSemana());
        assertEquals("Miércoles", horario.get(1).getDiaSemana());
        assertEquals("Viernes", horario.get(2).getDiaSemana());
    }

    @Test
    void obtenerHorarioCompleto_debeColocarDiaDesconocidoAlFinal() {
        FranjaHoraria franjaLun = new FranjaHoraria("Lunes", "08:00", "10:00");
        FranjaHoraria franjaRara = new FranjaHoraria("Festivo", "08:00", "10:00");
        Clase cLun = new Clase(franjaLun, periodo, asignatura, docente, grupo, aula);
        Clase cRara = new Clase(franjaRara, periodo, asignatura, docente, grupo, aula);
        when(repoClases.listarTodos()).thenReturn(List.of(cRara, cLun));
        when(repoRecreos.listarTodos()).thenReturn(List.of());
        List<Evento> horario = casoUso.obtenerHorarioCompleto(grupo, periodo);
        assertEquals("Lunes", horario.get(0).getDiaSemana());
        assertEquals("Festivo", horario.get(1).getDiaSemana());
    }
}
