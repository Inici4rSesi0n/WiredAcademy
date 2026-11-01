package integration.aplicacion.casosdeuso.portaldeingreso.gestiondetramites;
import aplicacion.casosdeuso.portaldeingreso.gestiondetramites.GestionMatriculas;
import dominio.modelo.*;
import dominio.puerto.repositorio.*;
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
public class GestionMatriculasIT extends BaseIntegrationTest {
    @Autowired private GestionMatriculas gestionMatriculas;
    @Autowired private RepositorioGrados repoGrados;
    @Autowired private RepositorioGrupos repoGrupos;
    @Autowired private RepositorioPeriodos repoPeriodos;
    @Autowired private RepositorioEstudiantes repoEstudiantes;
    @Autowired private EntityManager em;
    private Grupo crearGrupo(String nombre, String grado) {
        Grado g = new Grado(grado, "Primaria");
        repoGrados.agregar(g);
        Grupo grupo = new Grupo(nombre, g);
        repoGrupos.agregar(grupo);
        return grupo;
    }
    private PeriodoAcademico crearPeriodo(String nombre) {
        PeriodoAcademico p = new PeriodoAcademico(nombre, "2026-01-01", "2026-06-30", "Activo");
        repoPeriodos.agregar(p);
        return p;
    }
    private Estudiante crearEstudiante(String codigo, String dni) {
        Estudiante e = new Estudiante(codigo, "hashIT", dni, "Est", "IT", 15);
        repoEstudiantes.agregar(e);
        return e;
    }
    @Test
    public void testMatricularEstudianteCorrectamente() {
        Grupo grupo = crearGrupo("2A IT", "2do Grado IT");
        PeriodoAcademico periodo = crearPeriodo("2026-I M");
        Estudiante estudiante = crearEstudiante("IT-EST-001", "55555555");
        gestionMatriculas.matricularEstudiante(estudiante, periodo, grupo, new ArrayList<>(), "2026-01-15", EstadoMatricula.ACTIVA);
        em.flush();
        assertFalse(gestionMatriculas.listarTodas().isEmpty());
        Matricula m = gestionMatriculas.buscarMatricula("IT-EST-001", "2026-I M");
        assertNotNull(m);
        assertEquals(EstadoMatricula.ACTIVA, m.getEstado());
    }
    @Test
    public void testMatriculaDuplicadaFalla() {
        Grupo grupo = crearGrupo("2B IT", "2do Grado IT2");
        PeriodoAcademico periodo = crearPeriodo("2026-I M2");
        Estudiante estudiante = crearEstudiante("IT-EST-002", "66666666");
        gestionMatriculas.matricularEstudiante(estudiante, periodo, grupo, new ArrayList<>(), "2026-01-15", EstadoMatricula.ACTIVA);
        em.flush();
        assertThrows(IllegalArgumentException.class, () ->
            gestionMatriculas.matricularEstudiante(estudiante, periodo, grupo, new ArrayList<>(), "2026-01-16", EstadoMatricula.ACTIVA));
    }
    @Test
    public void testListarPorGrupoRetornaMatriculas() {
        Grupo grupo = crearGrupo("3A IT", "3er Grado IT");
        PeriodoAcademico periodo = crearPeriodo("2026-I M3");
        Estudiante e1 = crearEstudiante("IT-EST-003", "77777777");
        Estudiante e2 = crearEstudiante("IT-EST-004", "88888888");
        gestionMatriculas.matricularEstudiante(e1, periodo, grupo, new ArrayList<>(), "2026-01-15", EstadoMatricula.ACTIVA);
        gestionMatriculas.matricularEstudiante(e2, periodo, grupo, new ArrayList<>(), "2026-01-15", EstadoMatricula.ACTIVA);
        em.flush();
        assertEquals(2, gestionMatriculas.listarPorGrupo("3A IT").size());
    }
}
