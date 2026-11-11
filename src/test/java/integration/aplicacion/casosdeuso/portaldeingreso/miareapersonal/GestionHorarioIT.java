/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.aplicacion.casosdeuso.portaldeingreso.miareapersonal;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.*;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import aplicacion.casosdeuso.portaldeingreso.miareapersonal.GestionHorario;
import dominio.modelo.*;
import dominio.puerto.repositorio.RepositorioGrados;
import integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionHorarioIT extends BaseIntegrationTest {
    @Autowired private GestionHorario gestionHorario;
    @Autowired private GestionGrados gestionGrados;
    @Autowired private GestionGrupos gestionGrupos;
    @Autowired private GestionPeriodos gestionPeriodos;
    @Autowired private GestionAsignaturas gestionAsignaturas;
    @Autowired private GestionAulas gestionAulas;
    @Autowired private GestionDocentes gestionDocentes;
    @Autowired private GestionClases gestionClases;
    @Autowired private GestionRecreos gestionRecreos;
    @Autowired private RepositorioGrados repoGrados;
    @Test
    public void testHorarioCombinaClasesYRecreos() {
        Grado grado = new Grado("HORARIO-IT-Grado", "Primaria");
        repoGrados.agregar(grado);
        gestionGrupos.crearGrupo("HORARIO-IT-A", grado);
        gestionPeriodos.crearPeriodo("HORARIO-IT-Periodo", "2026-01-01", "2026-12-31", "Activo");
        gestionAsignaturas.crearAsignatura("HORARIO-IT-Asig");
        gestionAulas.crearAula("HORARIO-IT-Aula", 30, "Piso 1", "Teoría", ModalidadAula.PRESENCIAL);
        Docente docente = new Docente("HORARIO-IT-DOC", "hashIT", "99000020", "Hora", "IT", 35, "Mat", new ArrayList<>());
        gestionDocentes.agregar(docente);
        Grupo grupo = gestionGrupos.buscarPorNombre("HORARIO-IT-A");
        PeriodoAcademico periodo = gestionPeriodos.buscarPorNombre("HORARIO-IT-Periodo");
        Asignatura asig = gestionAsignaturas.buscarAsignatura("HORARIO-IT-Asig");
        Aula aula = gestionAulas.buscarAula("HORARIO-IT-Aula");
        gestionClases.crearClase("Lunes", "08:00", "10:00", asig, docente, grupo, aula, periodo);
        FranjaHoraria franjaRecreo = new FranjaHoraria("Lunes", "10:00", "10:30");
        gestionRecreos.crearRecreo(franjaRecreo, "HORARIO-IT-Recreo", List.of(periodo));
        List<Evento> eventos = gestionHorario.obtenerHorarioCompleto(grupo, periodo);
        assertFalse(eventos.isEmpty());
        long clases = eventos.stream().filter(e -> e instanceof Clase).count();
        long recreos = eventos.stream().filter(e -> e instanceof Recreo).count();
        assertEquals(1, clases);
        assertEquals(1, recreos);
    }
    @Test
    public void testHorarioAulaCombinaClasesYRecreos() {
        Grado grado = new Grado("AULA-IT-Grado", "Primaria");
        repoGrados.agregar(grado);
        gestionGrupos.crearGrupo("AULA-IT-Grupo", grado);
        gestionPeriodos.crearPeriodo("AULA-IT-Periodo", "2026-01-01", "2026-12-31", "Activo");
        gestionAsignaturas.crearAsignatura("AULA-IT-Asig");
        gestionAulas.crearAula("AULA-IT-Aula", 30, "Piso 1", "Teoría", ModalidadAula.PRESENCIAL);
        Docente docente = new Docente("AULA-IT-DOC", "hashIT", "99000021", "Aula", "IT", 35, "Mat", new ArrayList<>());
        gestionDocentes.agregar(docente);
        Grupo grupo = gestionGrupos.buscarPorNombre("AULA-IT-Grupo");
        PeriodoAcademico periodo = gestionPeriodos.buscarPorNombre("AULA-IT-Periodo");
        Asignatura asig = gestionAsignaturas.buscarAsignatura("AULA-IT-Asig");
        Aula aula = gestionAulas.buscarAula("AULA-IT-Aula");
        gestionClases.crearClase("Martes", "08:00", "10:00", asig, docente, grupo, aula, periodo);
        FranjaHoraria franjaRecreo = new FranjaHoraria("Martes", "10:00", "10:30");
        gestionRecreos.crearRecreo(franjaRecreo, "AULA-IT-Recreo", List.of(periodo));
        List<Evento> eventos = gestionHorario.obtenerHorarioAula("AULA-IT-Aula", periodo);
        assertFalse(eventos.isEmpty());
        long clases = eventos.stream().filter(e -> e instanceof Clase).count();
        long recreos = eventos.stream().filter(e -> e instanceof Recreo).count();
        assertEquals(1, clases);
        assertEquals(1, recreos);
    }
}
