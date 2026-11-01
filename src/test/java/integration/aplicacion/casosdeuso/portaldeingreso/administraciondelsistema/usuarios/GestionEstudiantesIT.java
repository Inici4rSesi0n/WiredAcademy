package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionEstudiantes;
import dominio.modelo.Estudiante;
import integration.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionEstudiantesIT extends BaseIntegrationTest {
    @Autowired private GestionEstudiantes gestionEstudiantes;
    @Autowired private EntityManager em;
    @Test
    public void testActualizarEstudiante() {
        gestionEstudiantes.agregar(new Estudiante("IT-EST-UP", "hashIT", "84000001", "Est", "UP", 15));
        Estudiante original = gestionEstudiantes.buscarPorCodigo("IT-EST-UP");
        Estudiante actualizado = new Estudiante("IT-EST-UP", "hashIT2", "84000001", "Est", "UPD", 16);
        gestionEstudiantes.actualizar(original, actualizado);
        em.flush(); em.clear();
        Estudiante recargado = gestionEstudiantes.buscarPorCodigo("IT-EST-UP");
        assertEquals("UPD", recargado.getApellido());
        assertEquals(16, recargado.getEdad());
    }
    @Test
    public void testEliminarEstudiante() {
        gestionEstudiantes.agregar(new Estudiante("IT-EST-DEL", "hashIT", "84000002", "A", "B", 15));
        Estudiante e = gestionEstudiantes.buscarPorCodigo("IT-EST-DEL");
        gestionEstudiantes.eliminar(e);
        em.flush(); em.clear();
        assertNull(gestionEstudiantes.buscarPorCodigo("IT-EST-DEL"));
    }
}
