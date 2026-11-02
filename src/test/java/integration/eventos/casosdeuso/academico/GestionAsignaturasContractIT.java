package integration.eventos.casosdeuso.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import dominio.modelo.Asignatura;
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
class GestionAsignaturasContractIT extends BaseContractTest {
    @Autowired
    private GestionAsignaturas gestionAsignaturas;
    @Test
    void crearAsignatura_emiteASIGNATURAS() {
        gestionAsignaturas.crearAsignatura("CT-Asig-001");
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.ASIGNATURAS));
        assertTrue(recorder.contiene(TipoEvento.ASIGNATURAS));
    }
    @Test
    void actualizarAsignatura_emiteASIGNATURAS() {
        gestionAsignaturas.crearAsignatura("CT-Asig-002");
        Asignatura original = gestionAsignaturas.buscarAsignatura("CT-Asig-002");
        recorder.reset();
        gestionAsignaturas.actualizarAsignatura(original, "CT-Asig-002-UPD");
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.ASIGNATURAS));
        assertTrue(recorder.contiene(TipoEvento.ASIGNATURAS));
    }
    @Test
    void eliminarAsignatura_emiteASIGNATURAS() {
        gestionAsignaturas.crearAsignatura("CT-Asig-003");
        Asignatura asignatura = gestionAsignaturas.buscarAsignatura("CT-Asig-003");
        recorder.reset();
        gestionAsignaturas.eliminarAsignatura(asignatura);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.ASIGNATURAS));
        assertTrue(recorder.contiene(TipoEvento.ASIGNATURAS));
    }
}
