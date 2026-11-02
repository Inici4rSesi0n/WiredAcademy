package integration.eventos.casosdeuso.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import dominio.modelo.Grado;
import dominio.modelo.TipoEvento;
import integration.eventos.BaseContractTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
class GestionGradosContractIT extends BaseContractTest {
    @Autowired
    private GestionGrados gestionGrados;
    @Test
    void crearGrado_emiteGRADOS() {
        gestionGrados.crearGrado("CT-Grado-001", "Primaria");
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.GRADOS));
        assertTrue(recorder.contiene(TipoEvento.GRADOS));
    }
    @Test
    void actualizarGrado_emiteGRADOS() {
        gestionGrados.crearGrado("CT-Grado-002", "Primaria");
        Grado original = gestionGrados.buscarPorNombreYNivel("CT-Grado-002", "Primaria");
        recorder.reset();
        gestionGrados.actualizarGrado(original, "CT-Grado-002-UPD", "Secundaria");
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.GRADOS));
        assertTrue(recorder.contiene(TipoEvento.GRADOS));
    }
    @Test
    void eliminarGrado_emiteGRADOS() {
        gestionGrados.crearGrado("CT-Grado-003", "Primaria");
        Grado grado = gestionGrados.buscarPorNombreYNivel("CT-Grado-003", "Primaria");
        recorder.reset();
        gestionGrados.eliminarGrado(grado);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.GRADOS));
        assertTrue(recorder.contiene(TipoEvento.GRADOS));
    }
}
