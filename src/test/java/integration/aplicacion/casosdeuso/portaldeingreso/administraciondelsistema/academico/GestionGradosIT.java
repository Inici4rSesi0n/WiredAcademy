package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import dominio.modelo.Grado;
import integration.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionGradosIT extends BaseIntegrationTest {
    @Autowired private GestionGrados gestionGrados;
    @Autowired private EntityManager em;
    @Test
    public void testActualizarGrado() {
        gestionGrados.crearGrado("IT-Grado-UP", "Primaria");
        Grado original = gestionGrados.buscarPorNombreYNivel("IT-Grado-UP", "Primaria");
        gestionGrados.actualizarGrado(original, "IT-Grado-UP-NEW", "Secundaria");
        em.flush(); em.clear();
        assertNull(gestionGrados.buscarPorNombreYNivel("IT-Grado-UP", "Primaria"));
        Grado actualizado = gestionGrados.buscarPorNombreYNivel("IT-Grado-UP-NEW", "Secundaria");
        assertNotNull(actualizado);
        assertEquals("Secundaria", actualizado.getNivel());
    }
    @Test
    public void testEliminarGrado() {
        gestionGrados.crearGrado("IT-Grado-DEL", "Primaria");
        Grado g = gestionGrados.buscarPorNombreYNivel("IT-Grado-DEL", "Primaria");
        gestionGrados.eliminarGrado(g);
        em.flush(); em.clear();
        assertNull(gestionGrados.buscarPorNombreYNivel("IT-Grado-DEL", "Primaria"));
    }
}
