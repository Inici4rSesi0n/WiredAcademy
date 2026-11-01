package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionCoordinadores;
import dominio.modelo.CoordinadorAcademico;
import integration.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionCoordinadoresIT extends BaseIntegrationTest {
    @Autowired private GestionCoordinadores gestionCoordinadores;
    @Autowired private EntityManager em;
    @Test
    public void testActualizarCoordinador() {
        gestionCoordinadores.agregar(new CoordinadorAcademico("IT-COORD-UP", "hashIT", "82000001", "Coord", "UP", 40));
        CoordinadorAcademico original = gestionCoordinadores.buscarPorCodigo("IT-COORD-UP");
        CoordinadorAcademico actualizado = new CoordinadorAcademico("IT-COORD-UP", "hashIT2", "82000001", "Coord", "UPD", 45);
        gestionCoordinadores.actualizar(original, actualizado);
        em.flush(); em.clear();
        CoordinadorAcademico recargado = gestionCoordinadores.buscarPorCodigo("IT-COORD-UP");
        assertEquals("UPD", recargado.getApellido());
        assertEquals(45, recargado.getEdad());
    }
    @Test
    public void testEliminarCoordinador() {
        gestionCoordinadores.agregar(new CoordinadorAcademico("IT-COORD-DEL", "hashIT", "82000002", "A", "B", 40));
        CoordinadorAcademico c = gestionCoordinadores.buscarPorCodigo("IT-COORD-DEL");
        gestionCoordinadores.eliminar(c);
        em.flush(); em.clear();
        assertNull(gestionCoordinadores.buscarPorCodigo("IT-COORD-DEL"));
    }
}
