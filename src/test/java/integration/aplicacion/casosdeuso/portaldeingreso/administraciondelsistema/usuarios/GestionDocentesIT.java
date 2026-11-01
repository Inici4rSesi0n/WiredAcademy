package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import dominio.modelo.Docente;
import dominio.modelo.Grado;
import dominio.modelo.Grupo;
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
public class GestionDocentesIT extends BaseIntegrationTest {
    @Autowired private GestionDocentes gestionDocentes;
    @Autowired private GestionGrupos gestionGrupos;
    @Autowired private RepositorioGrados repoGrados;
    @Autowired private EntityManager em;
    @Test
    public void testAsignarTutoriaDesdeDocente() {
        Grado grado = new Grado("DOC-IT-Grado", "Primaria");
        repoGrados.agregar(grado);
        gestionGrupos.crearGrupo("DOC-IT-Grupo", grado);
        Docente docente = new Docente("DOC-IT-001", "hashIT", "88000001", "Doc", "IT", 35, "Mat", new ArrayList<>());
        gestionDocentes.agregar(docente);
        Grupo grupo = gestionGrupos.buscarPorNombre("DOC-IT-Grupo");
        Docente docenteRecargado = gestionDocentes.buscarPorCodigo("DOC-IT-001");
        gestionDocentes.asignarTutoria(docenteRecargado, grupo);
        em.flush();
        em.clear();
        Grupo grupoPersistido = gestionGrupos.buscarPorNombre("DOC-IT-Grupo");
        assertNotNull(grupoPersistido.getTutor());
        assertEquals("DOC-IT-001", grupoPersistido.getTutor().getCodigo());
        Docente docentePersistido = gestionDocentes.buscarPorCodigo("DOC-IT-001");
        assertNotNull(docentePersistido.getTutoria());
        assertEquals("DOC-IT-Grupo", docentePersistido.getTutoria().getNombre());
    }
    @Test
    public void testAsignarTutoriaReemplazaTutorAnterior() {
        Grado grado = new Grado("DOC-IT-Grado2", "Primaria");
        repoGrados.agregar(grado);
        gestionGrupos.crearGrupo("DOC-IT-Grupo2", grado);
        Docente tutor1 = new Docente("DOC-IT-T1", "hashIT", "88000002", "T1", "IT", 35, "Mat", new ArrayList<>());
        Docente tutor2 = new Docente("DOC-IT-T2", "hashIT", "88000003", "T2", "IT", 35, "Mat", new ArrayList<>());
        gestionDocentes.agregar(tutor1);
        gestionDocentes.agregar(tutor2);
        Grupo grupo = gestionGrupos.buscarPorNombre("DOC-IT-Grupo2");
        gestionDocentes.asignarTutoria(gestionDocentes.buscarPorCodigo("DOC-IT-T1"), grupo);
        em.flush();
        em.clear();
        Grupo grupoRecargado = gestionGrupos.buscarPorNombre("DOC-IT-Grupo2");
        gestionDocentes.asignarTutoria(gestionDocentes.buscarPorCodigo("DOC-IT-T2"), grupoRecargado);
        em.flush();
        em.clear();
        Grupo persistido = gestionGrupos.buscarPorNombre("DOC-IT-Grupo2");
        assertEquals("DOC-IT-T2", persistido.getTutor().getCodigo());
    }
}
