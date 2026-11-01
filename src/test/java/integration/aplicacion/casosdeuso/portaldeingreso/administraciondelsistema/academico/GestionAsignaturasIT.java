package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import dominio.modelo.Asignatura;
import integration.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionAsignaturasIT extends BaseIntegrationTest {
    @Autowired private GestionAsignaturas gestionAsignaturas;
    @Autowired private EntityManager em;
    @Test
    public void testActualizarAsignatura() {
        gestionAsignaturas.crearAsignatura("IT-Asig-UP");
        Asignatura original = gestionAsignaturas.buscarAsignatura("IT-Asig-UP");
        gestionAsignaturas.actualizarAsignatura(original, "IT-Asig-UP-NEW");
        em.flush(); em.clear();
        assertNull(gestionAsignaturas.buscarAsignatura("IT-Asig-UP"));
        assertNotNull(gestionAsignaturas.buscarAsignatura("IT-Asig-UP-NEW"));
    }
    @Test
    public void testEliminarAsignatura() {
        gestionAsignaturas.crearAsignatura("IT-Asig-DEL");
        Asignatura a = gestionAsignaturas.buscarAsignatura("IT-Asig-DEL");
        gestionAsignaturas.eliminarAsignatura(a);
        em.flush(); em.clear();
        assertNull(gestionAsignaturas.buscarAsignatura("IT-Asig-DEL"));
    }
}
