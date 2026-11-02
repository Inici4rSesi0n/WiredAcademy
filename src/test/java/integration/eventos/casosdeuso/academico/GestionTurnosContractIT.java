package integration.eventos.casosdeuso.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionTurnos;
import dominio.modelo.TipoEvento;
import dominio.modelo.Turno;
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
class GestionTurnosContractIT extends BaseContractTest {
    @Autowired
    private GestionTurnos gestionTurnos;
    @Test
    void crearTurno_emiteTURNOS() {
        gestionTurnos.crearTurno("CT-001");
        await().atMost(2, TimeUnit.SECONDS)
                .until(() -> recorder.contiene(TipoEvento.TURNOS));
        assertTrue(recorder.contiene(TipoEvento.TURNOS));
    }
    @Test
    void actualizarTurno_emiteTURNOS() {
        gestionTurnos.crearTurno("CT-002");
        Turno original = gestionTurnos.buscarPorNombre("CT-002");
        recorder.reset();
        gestionTurnos.actualizarTurno(original, "CT-002-UPD");
        await().atMost(2, TimeUnit.SECONDS)
                .until(() -> recorder.contiene(TipoEvento.TURNOS));
        assertTrue(recorder.contiene(TipoEvento.TURNOS));
    }
    @Test
    void eliminarTurno_emiteTURNOS() {
        gestionTurnos.crearTurno("CT-003");
        Turno turno = gestionTurnos.buscarPorNombre("CT-003");
        recorder.reset();
        gestionTurnos.eliminarTurno(turno);
        await().atMost(2, TimeUnit.SECONDS)
                .until(() -> recorder.contiene(TipoEvento.TURNOS));
        assertTrue(recorder.contiene(TipoEvento.TURNOS));
    }
}
