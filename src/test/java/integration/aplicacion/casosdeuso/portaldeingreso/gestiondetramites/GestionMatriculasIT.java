package integration.aplicacion.casosdeuso.portaldeingreso.gestiondetramites;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionEstudiantes;
import aplicacion.casosdeuso.portaldeingreso.gestiondetramites.GestionMatriculas;
import dominio.modelo.*;
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
    @Autowired private GestionGrados gestionGrados;
    @Autowired private GestionGrupos gestionGrupos;
    @Autowired private GestionPeriodos gestionPeriodos;
    @Autowired private GestionEstudiantes gestionEstudiantes;
    @Autowired private EntityManager em;
    private Grupo crearGrupo(String nombre, String grado) {
        gestionGrados.crearGrado(grado, "Primaria");
        Grado g = gestionGrados.buscarPorNombreYNivel(grado, "Primaria");
        gestionGrupos.crearGrupo(nombre, g);
        return gestionGrupos.buscarPorNombre(nombre);
    }
    private PeriodoAcademico crearPeriodo(String nombre) {
        gestionPeriodos.crearPeriodo(nombre, "2026-01-01", "2026-06-30", "Activo");
        return gestionPeriodos.buscarPorNombre(nombre);
    }
    private Estudiante crearEstudiante(String codigo, String dni) {
        gestionEstudiantes.agregar(new Estudiante(codigo, "hashIT", dni, "Est", "IT", 15));
        return gestionEstudiantes.buscarPorCodigo(codigo);
    }
    @Test
    public void testMatricularEstudianteCorrectamente() {
        Grupo grupo = crearGrupo("2A-IT", "2do Grado IT");
        PeriodoAcademico periodo = crearPeriodo("2026-I-M");
        Estudiante estudiante = crearEstudiante("IT-EST-001", "88555555");
        gestionMatriculas.matricularEstudiante(estudiante, periodo, grupo, new ArrayList<>(), "2026-01-15", EstadoMatricula.ACTIVA);
        em.flush(); em.clear();
        assertFalse(gestionMatriculas.listarTodas().isEmpty());
        Matricula m = gestionMatriculas.buscarMatricula("IT-EST-001", "2026-I-M");
        assertNotNull(m);
        assertEquals(EstadoMatricula.ACTIVA, m.getEstado());
    }
    @Test
    public void testMatriculaDuplicadaFalla() {
        Grupo grupo = crearGrupo("2B-IT", "2do Grado IT2");
        PeriodoAcademico periodo = crearPeriodo("2026-I-M2");
        Estudiante estudiante = crearEstudiante("IT-EST-002", "88666666");
        gestionMatriculas.matricularEstudiante(estudiante, periodo, grupo, new ArrayList<>(), "2026-01-15", EstadoMatricula.ACTIVA);
        em.flush(); em.clear();
        assertThrows(IllegalArgumentException.class, () ->
            gestionMatriculas.matricularEstudiante(estudiante, periodo, grupo, new ArrayList<>(), "2026-01-16", EstadoMatricula.ACTIVA));
    }
    @Test
    public void testListarPorGrupoRetornaMatriculas() {
        Grupo grupo = crearGrupo("3A-IT", "3er Grado IT");
        PeriodoAcademico periodo = crearPeriodo("2026-I-M3");
        Estudiante e1 = crearEstudiante("IT-EST-003", "88777777");
        Estudiante e2 = crearEstudiante("IT-EST-004", "88888801");
        gestionMatriculas.matricularEstudiante(e1, periodo, grupo, new ArrayList<>(), "2026-01-15", EstadoMatricula.ACTIVA);
        gestionMatriculas.matricularEstudiante(e2, periodo, grupo, new ArrayList<>(), "2026-01-15", EstadoMatricula.ACTIVA);
        em.flush(); em.clear();
        assertEquals(2, gestionMatriculas.listarPorGrupo("3A-IT").size());
    }
    @Test
    public void testActualizarEstadoMatricula() {
        Grupo grupo = crearGrupo("4A-IT", "4to Grado IT");
        PeriodoAcademico periodo = crearPeriodo("2026-I-M4");
        Estudiante estudiante = crearEstudiante("IT-EST-005", "88999901");
        gestionMatriculas.matricularEstudiante(estudiante, periodo, grupo, new ArrayList<>(), "2026-01-15", EstadoMatricula.ACTIVA);
        em.flush(); em.clear();
        Matricula m = gestionMatriculas.buscarMatricula("IT-EST-005", "2026-I-M4");
        assertNotNull(m);
        gestionMatriculas.actualizarEstado(m, EstadoMatricula.RETIRADA);
        em.flush(); em.clear();
        Matricula persistida = gestionMatriculas.buscarMatricula("IT-EST-005", "2026-I-M4");
        assertEquals(EstadoMatricula.RETIRADA, persistida.getEstado());
    }
    @Test
    public void testEliminarMatricula() {
        Grupo grupo = crearGrupo("5A-IT", "5to Grado IT");
        PeriodoAcademico periodo = crearPeriodo("2026-I-M5");
        Estudiante estudiante = crearEstudiante("IT-EST-006", "88999902");
        gestionMatriculas.matricularEstudiante(estudiante, periodo, grupo, new ArrayList<>(), "2026-01-15", EstadoMatricula.ACTIVA);
        em.flush(); em.clear();
        Matricula m = gestionMatriculas.buscarMatricula("IT-EST-006", "2026-I-M5");
        assertNotNull(m);
        gestionMatriculas.eliminarMatricula(m);
        em.flush(); em.clear();
        assertNull(gestionMatriculas.buscarMatricula("IT-EST-006", "2026-I-M5"));
    }
}
