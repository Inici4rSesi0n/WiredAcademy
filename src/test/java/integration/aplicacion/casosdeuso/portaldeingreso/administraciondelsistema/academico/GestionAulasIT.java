package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAulas;
import dominio.modelo.Aula;
import dominio.modelo.ModalidadAula;
import integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionAulasIT extends BaseIntegrationTest {
    @Autowired
    private GestionAulas gestionAulas;
    @Test
    public void testCrearYListarAula() {
        gestionAulas.crearAula("Aula IT", 30, "Piso 3", "Laboratorio", ModalidadAula.VIRTUAL);
        List<Aula> aulas = gestionAulas.listarTodos();
        assertFalse(aulas.isEmpty());
        Aula aulaCreada = aulas.stream().filter(a -> a.getNombre().equals("Aula IT")).findFirst().orElse(null);
        assertNotNull(aulaCreada);
        assertEquals(30, aulaCreada.getCapacidad());
        assertEquals("Piso 3", aulaCreada.getUbicacion());
        assertEquals(ModalidadAula.VIRTUAL, aulaCreada.getModalidad());
    }
}
