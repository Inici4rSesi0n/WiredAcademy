package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionTurnos;
import dominio.modelo.Turno;
import integration.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionTurnosIT extends BaseIntegrationTest {
    @Autowired private GestionTurnos gestionTurnos;
    @Autowired private EntityManager em;
    @Test
    public void testActualizarTurno() {
        gestionTurnos.crearTurno("IT-Tur-UP");
        Turno original = gestionTurnos.buscarPorNombre("IT-Tur-UP");
        gestionTurnos.actualizarTurno(original, "IT-Tur-UP-NEW");
        em.flush(); em.clear();
        assertNull(gestionTurnos.buscarPorNombre("IT-Tur-UP"));
        assertNotNull(gestionTurnos.buscarPorNombre("IT-Tur-UP-NEW"));
    }
    @Test
    public void testEliminarTurno() {
        gestionTurnos.crearTurno("IT-Tur-DEL");
        Turno t = gestionTurnos.buscarPorNombre("IT-Tur-DEL");
        gestionTurnos.eliminarTurno(t);
        em.flush(); em.clear();
        assertNull(gestionTurnos.buscarPorNombre("IT-Tur-DEL"));
    }
    @Test
    public void testCrearTurnoConCapitalizacionDistintaDebeLanzarIllegalArgumentException() {
        gestionTurnos.crearTurno("CI-Turno-MiXeD");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> gestionTurnos.crearTurno("ci-turno-mixed"));
        assertTrue(ex.getMessage().contains("Ya existe"),
                "Esperaba IllegalArgumentException de negocio, obtuve: " + ex.getMessage());
    }
}
