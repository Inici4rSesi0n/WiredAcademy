package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionEstudiantes;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionPadres;
import dominio.modelo.Estudiante;
import dominio.modelo.Padre;
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
public class GestionPadresIT extends BaseIntegrationTest {
    @Autowired private GestionPadres gestionPadres;
    @Autowired private GestionEstudiantes gestionEstudiantes;
    @Autowired private EntityManager em;
    @Test
    public void testVincularEstudianteSeReflejaEnLectura() {
        Estudiante estudiante = new Estudiante("PAD-IT-EST1", "hashIT", "77000001", "Hijo", "IT", 15);
        gestionEstudiantes.agregar(estudiante);
        Padre padre = new Padre("PAD-IT-001", "hashIT", "77000010", "Padre", "IT", 40, new ArrayList<>());
        gestionPadres.agregar(padre);
        Estudiante estudianteRecargado = gestionEstudiantes.buscarPorCodigo("PAD-IT-EST1");
        Padre padreRecargado = gestionPadres.buscarPorCodigo("PAD-IT-001");
        gestionPadres.vincularEstudiante(padreRecargado, estudianteRecargado);
        em.flush(); em.clear();
        Padre padrePersistido = gestionPadres.buscarPorCodigo("PAD-IT-001");
        assertNotNull(padrePersistido);
        assertEquals(1, padrePersistido.getEstudiantes().size());
        assertEquals("PAD-IT-EST1", padrePersistido.getEstudiantes().get(0).getCodigo());
    }
    @Test
    public void testDesvincularEstudianteSeReflejaEnLectura() {
        Estudiante estudiante = new Estudiante("PAD-IT-EST2", "hashIT", "77000002", "Hijo2", "IT", 15);
        gestionEstudiantes.agregar(estudiante);
        Padre padre = new Padre("PAD-IT-002", "hashIT", "77000011", "Padre2", "IT", 40, new ArrayList<>());
        gestionPadres.agregar(padre);
        Padre padreRecargado = gestionPadres.buscarPorCodigo("PAD-IT-002");
        Estudiante estudianteRecargado = gestionEstudiantes.buscarPorCodigo("PAD-IT-EST2");
        gestionPadres.vincularEstudiante(padreRecargado, estudianteRecargado);
        em.flush(); em.clear();
        Padre padreConHijo = gestionPadres.buscarPorCodigo("PAD-IT-002");
        Estudiante estudianteConPadre = gestionEstudiantes.buscarPorCodigo("PAD-IT-EST2");
        gestionPadres.desvincularEstudiante(padreConHijo, estudianteConPadre);
        em.flush(); em.clear();
        Padre padreFinal = gestionPadres.buscarPorCodigo("PAD-IT-002");
        assertTrue(padreFinal.getEstudiantes().isEmpty());
    }
    @Test
    public void testActualizarPadre() {
        Padre padre = new Padre("PAD-IT-UPD", "hashIT", "77000020", "Padre", "Old", 40, new ArrayList<>());
        gestionPadres.agregar(padre);
        Padre original = gestionPadres.buscarPorCodigo("PAD-IT-UPD");
        Padre actualizado = new Padre("PAD-IT-UPD", "hashIT2", "77000020", "Padre", "New", 45, new ArrayList<>());
        gestionPadres.actualizar(original, actualizado);
        em.flush(); em.clear();
        Padre recargado = gestionPadres.buscarPorCodigo("PAD-IT-UPD");
        assertEquals("New", recargado.getApellido());
        assertEquals(45, recargado.getEdad());
    }
    @Test
    public void testEliminarPadre() {
        Padre padre = new Padre("PAD-IT-DEL", "hashIT", "77000021", "Padre", "DEL", 40, new ArrayList<>());
        gestionPadres.agregar(padre);
        Padre p = gestionPadres.buscarPorCodigo("PAD-IT-DEL");
        gestionPadres.eliminar(p);
        em.flush(); em.clear();
        assertNull(gestionPadres.buscarPorCodigo("PAD-IT-DEL"));
    }
}
