package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionClases;
import dominio.modelo.*;
import dominio.puerto.repositorio.*;
import integration.BaseIntegrationTest;
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
    @Autowired private RepositorioGrados repoGrados;
    @Autowired private RepositorioGrupos repoGrupos;
    @Autowired private RepositorioAulas repoAulas;
    @Autowired private RepositorioAsignaturas repoAsignaturas;
    @Autowired private RepositorioPeriodos repoPeriodos;
    @Autowired private RepositorioDocentes repoDocentes;
    private Asignatura crearAsignatura(String nombre) {
        Asignatura a = new Asignatura(nombre);
        repoAsignaturas.agregar(a);
        return a;
    }
    private PeriodoAcademico crearPeriodo(String nombre) {
        PeriodoAcademico p = new PeriodoAcademico(nombre, "2026-01-01", "2026-06-30", "Activo");
        repoPeriodos.agregar(p);
        return p;
    }
    private Aula crearAula(String nombre) {
        Aula a = new Aula(nombre, 30, "Piso 1", "Teoría", ModalidadAula.PRESENCIAL);
        repoAulas.agregar(a);
        return a;
    }
    private Docente crearDocente(String codigo, String dni) {
        Docente d = new Docente(codigo, "hashIT", dni, "Doc", "IT", 35, "Mat", new ArrayList<>());
        repoDocentes.agregar(d);
        return d;
    }
    private Grupo crearGrupo(String nombre, String grado) {
        Grado g = new Grado(grado, "Primaria");
        repoGrados.agregar(g);
        Grupo grupo = new Grupo(nombre, g);
        repoGrupos.agregar(grupo);
        return grupo;
    }
    @Test
    public void testCrearClaseCorrectamente() {
        Asignatura asig = crearAsignatura("Matemáticas IT");
        PeriodoAcademico periodo = crearPeriodo("2026-I IT");
        Aula aula = crearAula("Aula IT-101");
        Docente docente = crearDocente("IT-DOC-001", "11111111");
        Grupo grupo = crearGrupo("1A IT", "1er Grado IT");
        gestionClases.crearClase("Lunes", "08:00", "10:00", asig, docente, grupo, aula, periodo);
        assertFalse(gestionClases.listarClases().isEmpty());
    }
    @Test
    public void testConflictoDeDocenteDetectado() {
        Asignatura asig = crearAsignatura("Matemáticas C");
        PeriodoAcademico periodo = crearPeriodo("2026-I C");
        Aula aula1 = crearAula("Aula C-1");
        Aula aula2 = crearAula("Aula C-2");
        Docente docente = crearDocente("IT-DOC-002", "22222222");
        Grupo grupo1 = crearGrupo("1B C", "1er Grado C");
        Grupo grupo2 = crearGrupo("1C C", "1er Grado C2");
        gestionClases.crearClase("Lunes", "08:00", "10:00", asig, docente, grupo1, aula1, periodo);
        assertThrows(IllegalArgumentException.class, () ->
            gestionClases.crearClase("Lunes", "09:00", "11:00", asig, docente, grupo2, aula2, periodo));
    }
    @Test
    public void testConflictoDeAulaDetectado() {
        Asignatura asig = crearAsignatura("Física C");
        PeriodoAcademico periodo = crearPeriodo("2026-I A");
        Aula aula = crearAula("Aula A-CONFLICTO");
        Docente docente1 = crearDocente("IT-DOC-003", "33333333");
        Docente docente2 = crearDocente("IT-DOC-004", "44444444");
        Grupo grupo1 = crearGrupo("2A C", "2do Grado C");
        Grupo grupo2 = crearGrupo("2B C", "2do Grado C2");
        gestionClases.crearClase("Martes", "08:00", "10:00", asig, docente1, grupo1, aula, periodo);
        assertThrows(IllegalArgumentException.class, () ->
            gestionClases.crearClase("Martes", "09:00", "11:00", asig, docente2, grupo2, aula, periodo));
    }
}
