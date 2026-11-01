package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDirectores;
import dominio.modelo.Director;
import integration.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionDirectoresIT extends BaseIntegrationTest {
    @Autowired private GestionDirectores gestionDirectores;
    @Autowired private EntityManager em;
    @Test
    public void testActualizarDirector() {
        gestionDirectores.agregar(new Director("IT-DIR-UP", "hashIT", "83000001", "Dir", "UP", 45));
        Director original = gestionDirectores.buscarPorCodigo("IT-DIR-UP");
        Director actualizado = new Director("IT-DIR-UP", "hashIT2", "83000001", "Dir", "UPD", 50);
        gestionDirectores.actualizar(original, actualizado);
        em.flush(); em.clear();
        Director recargado = gestionDirectores.buscarPorCodigo("IT-DIR-UP");
        assertEquals("UPD", recargado.getApellido());
        assertEquals(50, recargado.getEdad());
    }
    @Test
    public void testEliminarDirector() {
        gestionDirectores.agregar(new Director("IT-DIR-DEL", "hashIT", "83000002", "A", "B", 45));
        Director d = gestionDirectores.buscarPorCodigo("IT-DIR-DEL");
        gestionDirectores.eliminar(d);
        em.flush(); em.clear();
        assertNull(gestionDirectores.buscarPorCodigo("IT-DIR-DEL"));
    }
}
