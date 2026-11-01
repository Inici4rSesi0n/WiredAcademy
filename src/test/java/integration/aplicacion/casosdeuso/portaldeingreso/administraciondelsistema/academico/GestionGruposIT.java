package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
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
public class GestionGruposIT extends BaseIntegrationTest {
    @Autowired private GestionGrupos gestionGrupos;
    @Autowired private GestionDocentes gestionDocentes;
    @Autowired private RepositorioGrados repoGrados;
    @Autowired private EntityManager em;
    @Test
    public void testAsignarTutorBidireccional() {
        Grado grado = new Grado("GRUPO-IT-Grado", "Primaria");
        repoGrados.agregar(grado);
        gestionGrupos.crearGrupo("GRUPO-IT-A", grado);
        Docente docente = new Docente("GRUPO-IT-DOC", "hashIT", "99000010", "Tutor", "IT", 35, "Mat", new ArrayList<>());
        gestionDocentes.agregar(docente);
        Grupo grupo = gestionGrupos.buscarPorNombre("GRUPO-IT-A");
        gestionGrupos.asignarTutor(grupo, docente);
        em.flush(); em.clear();
        Grupo persistido = gestionGrupos.buscarPorNombre("GRUPO-IT-A");
        assertNotNull(persistido.getTutor());
        assertEquals("GRUPO-IT-DOC", persistido.getTutor().getCodigo());
        Docente docentePersistido = gestionDocentes.buscarPorCodigo("GRUPO-IT-DOC");
        assertNotNull(docentePersistido);
        assertEquals("Tutor", docentePersistido.getNombre());
    }
    @Test
    public void testRemoverTutorLimpiaAmbosLados() {
        Grado grado = new Grado("GRUPO-IT-Grado2", "Primaria");
        repoGrados.agregar(grado);
        gestionGrupos.crearGrupo("GRUPO-IT-B", grado);
        Docente docente = new Docente("GRUPO-IT-DOC2", "hashIT", "99000011", "Tutor", "IT", 35, "Mat", new ArrayList<>());
        gestionDocentes.agregar(docente);
        Grupo grupo = gestionGrupos.buscarPorNombre("GRUPO-IT-B");
        gestionGrupos.asignarTutor(grupo, docente);
        em.flush(); em.clear();
        Grupo grupoRecargado = gestionGrupos.buscarPorNombre("GRUPO-IT-B");
        gestionGrupos.removerTutor(grupoRecargado);
        em.flush(); em.clear();
        Grupo persistido = gestionGrupos.buscarPorNombre("GRUPO-IT-B");
        assertNull(persistido.getTutor());
    }
    @Test
    public void testActualizarGrupo() {
        Grado gradoA = new Grado("IT-GRP-GA", "Primaria");
        Grado gradoB = new Grado("IT-GRP-GB", "Secundaria");
        repoGrados.agregar(gradoA);
        repoGrados.agregar(gradoB);
        gestionGrupos.crearGrupo("IT-GRP-UP", gradoA);
        Grupo original = gestionGrupos.buscarPorNombre("IT-GRP-UP");
        gestionGrupos.actualizarGrupo(original, "IT-GRP-UP-NEW", gradoB);
        em.flush(); em.clear();
        assertNull(gestionGrupos.buscarPorNombre("IT-GRP-UP"));
        Grupo actualizado = gestionGrupos.buscarPorNombre("IT-GRP-UP-NEW");
        assertNotNull(actualizado);
        assertEquals("IT-GRP-GB", actualizado.getGrado().getNombre());
    }
    @Test
    public void testEliminarGrupo() {
        Grado grado = new Grado("IT-GRP-DEL-G", "Primaria");
        repoGrados.agregar(grado);
        gestionGrupos.crearGrupo("IT-GRP-DEL", grado);
        Grupo g = gestionGrupos.buscarPorNombre("IT-GRP-DEL");
        gestionGrupos.eliminarGrupo(g);
        em.flush(); em.clear();
        assertNull(gestionGrupos.buscarPorNombre("IT-GRP-DEL"));
    }
}
