/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.*;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import dominio.modelo.*;
import dominio.puerto.repositorio.RepositorioGrados;
import integration.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionClasesIT extends BaseIntegrationTest {
    @Autowired private GestionClases gestionClases;
    @Autowired private GestionGrados gestionGrados;
    @Autowired private GestionGrupos gestionGrupos;
    @Autowired private GestionAulas gestionAulas;
    @Autowired private GestionAsignaturas gestionAsignaturas;
    @Autowired private GestionPeriodos gestionPeriodos;
    @Autowired private GestionDocentes gestionDocentes;
    @Autowired private RepositorioGrados repoGrados;
    @Autowired private EntityManager em;
    private Asignatura crearAsignatura(String nombre) {
        gestionAsignaturas.crearAsignatura(nombre);
        return gestionAsignaturas.buscarAsignatura(nombre);
    }
    private PeriodoAcademico crearPeriodo(String nombre) {
        gestionPeriodos.crearPeriodo(nombre, "2026-01-01", "2026-06-30", "Activo");
        return gestionPeriodos.buscarPorNombre(nombre);
    }
    private Aula crearAula(String nombre) {
        gestionAulas.crearAula(nombre, 30, "Piso 1", "Teoría");
        return gestionAulas.buscarAula(nombre);
    }
    private Docente crearDocente(String codigo, String dni) {
        Docente d = new Docente(codigo, "hashIT", dni, "Doc", "IT", 35, "Mat", new ArrayList<>());
        gestionDocentes.agregar(d);
        return gestionDocentes.buscarPorCodigo(codigo);
    }
    private Grupo crearGrupo(String nombre, String grado) {
        Grado g = new Grado(grado, "Primaria");
        repoGrados.agregar(g);
        gestionGrupos.crearGrupo(nombre, g);
        return gestionGrupos.buscarPorNombre(nombre);
    }
    @Test
    public void testCrearClaseCorrectamente() {
        Asignatura asig = crearAsignatura("Mat-IT");
        PeriodoAcademico periodo = crearPeriodo("2026-I-IT");
        Aula aula = crearAula("Aula-IT-101");
        Docente docente = crearDocente("IT-DOC-001", "11111111");
        Grupo grupo = crearGrupo("1A-IT", "1er Grado IT");
        gestionClases.crearClase("Lunes", "08:00", "10:00", asig, docente, grupo, aula, periodo);
        assertFalse(gestionClases.listarClases().isEmpty());
    }
    @Test
    public void testConflictoDeDocenteDetectado() {
        Asignatura asig = crearAsignatura("Mat-C");
        PeriodoAcademico periodo = crearPeriodo("2026-I-C");
        Aula aula1 = crearAula("Aula-C-1");
        Aula aula2 = crearAula("Aula-C-2");
        Docente docente = crearDocente("IT-DOC-002", "22222222");
        Grupo grupo1 = crearGrupo("1B-C", "1er Grado C");
        Grupo grupo2 = crearGrupo("1C-C", "1er Grado C2");
        gestionClases.crearClase("Lunes", "08:00", "10:00", asig, docente, grupo1, aula1, periodo);
        assertThrows(IllegalArgumentException.class, () ->
            gestionClases.crearClase("Lunes", "09:00", "11:00", asig, docente, grupo2, aula2, periodo));
    }
    @Test
    public void testConflictoDeAulaDetectado() {
        Asignatura asig = crearAsignatura("Fis-C");
        PeriodoAcademico periodo = crearPeriodo("2026-I-A");
        Aula aula = crearAula("Aula-A-CONFLICTO");
        Docente docente1 = crearDocente("IT-DOC-003", "33333333");
        Docente docente2 = crearDocente("IT-DOC-004", "44444444");
        Grupo grupo1 = crearGrupo("2A-C", "2do Grado C");
        Grupo grupo2 = crearGrupo("2B-C", "2do Grado C2");
        gestionClases.crearClase("Martes", "08:00", "10:00", asig, docente1, grupo1, aula, periodo);
        assertThrows(IllegalArgumentException.class, () ->
            gestionClases.crearClase("Martes", "09:00", "11:00", asig, docente2, grupo2, aula, periodo));
    }
    @Test
    public void testActualizarClase() {
        Asignatura asig = crearAsignatura("Mat-UP");
        PeriodoAcademico periodo = crearPeriodo("2026-I-UP");
        Aula aula = crearAula("Aula-UP-101");
        Docente docente = crearDocente("IT-DOC-UP", "55555555");
        Grupo grupo = crearGrupo("UP-1A", "UP-Grado");
        gestionClases.crearClase("Lunes", "08:00", "10:00", asig, docente, grupo, aula, periodo);
        em.flush(); em.clear();
        Clase original = gestionClases.buscarClase("UP-Grado", "UP-1A", "Lunes", "08:00", "10:00");
        assertNotNull(original);
        gestionClases.actualizarClase(original, "Martes", "09:00", "11:00", asig, docente, grupo, aula, periodo);
        em.flush(); em.clear();
        assertNull(gestionClases.buscarClase("UP-Grado", "UP-1A", "Lunes", "08:00", "10:00"));
        assertNotNull(gestionClases.buscarClase("UP-Grado", "UP-1A", "Martes", "09:00", "11:00"));
    }
    @Test
    public void testEliminarClase() {
        Asignatura asig = crearAsignatura("Mat-DEL");
        PeriodoAcademico periodo = crearPeriodo("2026-I-DEL");
        Aula aula = crearAula("Aula-DEL-101");
        Docente docente = crearDocente("IT-DOC-DEL", "66666666");
        Grupo grupo = crearGrupo("DEL-1A", "DEL-Grado");
        gestionClases.crearClase("Miércoles", "08:00", "10:00", asig, docente, grupo, aula, periodo);
        em.flush(); em.clear();
        Clase c = gestionClases.buscarClase("DEL-Grado", "DEL-1A", "Miércoles", "08:00", "10:00");
        assertNotNull(c);
        gestionClases.eliminarClase(c);
        em.flush(); em.clear();
        assertNull(gestionClases.buscarClase("DEL-Grado", "DEL-1A", "Miércoles", "08:00", "10:00"));
    }
}
