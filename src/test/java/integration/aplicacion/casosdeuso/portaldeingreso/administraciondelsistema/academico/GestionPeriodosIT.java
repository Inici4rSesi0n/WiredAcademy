package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import dominio.modelo.PeriodoAcademico;
import integration.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionPeriodosIT extends BaseIntegrationTest {
    @Autowired private GestionPeriodos gestionPeriodos;
    @Autowired private EntityManager em;
    @Test
    public void testActualizarPeriodo() {
        gestionPeriodos.crearPeriodo("IT-Per-UP", "2026-01-01", "2026-06-30", "Activo");
        PeriodoAcademico original = gestionPeriodos.buscarPorNombre("IT-Per-UP");
        gestionPeriodos.actualizarPeriodo(original, "IT-Per-UP-NEW", "2026-02-01", "2026-07-31", "Culminado");
        em.flush(); em.clear();
        assertNull(gestionPeriodos.buscarPorNombre("IT-Per-UP"));
        PeriodoAcademico actualizado = gestionPeriodos.buscarPorNombre("IT-Per-UP-NEW");
        assertNotNull(actualizado);
        assertEquals("2026-02-01", actualizado.getFechaInicio());
        assertEquals("Culminado", actualizado.getEstado());
    }
    @Test
    public void testEliminarPeriodo() {
        gestionPeriodos.crearPeriodo("IT-Per-DEL", "2026-01-01", "2026-12-31", "Activo");
        PeriodoAcademico p = gestionPeriodos.buscarPorNombre("IT-Per-DEL");
        gestionPeriodos.eliminarPeriodo(p);
        em.flush(); em.clear();
        assertNull(gestionPeriodos.buscarPorNombre("IT-Per-DEL"));
    }
}
