package integration.eventos.casosdeuso.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionRecreos;
import dominio.modelo.FranjaHoraria;
import dominio.modelo.PeriodoAcademico;
import dominio.modelo.Recreo;
import dominio.modelo.TipoEvento;
import integration.eventos.BaseContractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
class GestionRecreosContractIT extends BaseContractTest {
    @Autowired private GestionRecreos gestionRecreos;
    @Autowired private GestionPeriodos gestionPeriodos;
    private PeriodoAcademico periodoA;
    private PeriodoAcademico periodoB;
    @BeforeEach
    void setUpDatos() {
        gestionPeriodos.crearPeriodo("CT-Rec-PerA", "2026-01-01", "2026-06-30", "Activo");
        gestionPeriodos.crearPeriodo("CT-Rec-PerB", "2026-07-01", "2026-12-31", "Activo");
        periodoA = gestionPeriodos.buscarPorNombre("CT-Rec-PerA");
        periodoB = gestionPeriodos.buscarPorNombre("CT-Rec-PerB");
        recorder.reset();
    }
    @Test
    void crearRecreo_emiteRECREOS() {
        FranjaHoraria franja = new FranjaHoraria("Lunes", "10:00", "10:30");
        gestionRecreos.crearRecreo(franja, "CT-Rec-001", List.of(periodoA));
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.RECREOS));
        assertTrue(recorder.contiene(TipoEvento.RECREOS));
    }
    @Test
    void actualizarRecreo_emiteRECREOS() {
        FranjaHoraria franja = new FranjaHoraria("Martes", "10:00", "10:30");
        gestionRecreos.crearRecreo(franja, "CT-Rec-002", List.of(periodoA));
        Recreo original = gestionRecreos.buscarPorDescripcion("CT-Rec-002");
        FranjaHoraria nueva = new FranjaHoraria("Miércoles", "11:00", "11:30");
        recorder.reset();
        gestionRecreos.actualizarRecreo(original, nueva, "CT-Rec-002-UPD", List.of(periodoA));
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.RECREOS));
        assertTrue(recorder.contiene(TipoEvento.RECREOS));
    }
    @Test
    void aplicarA_emiteRECREOS() {
        FranjaHoraria franja = new FranjaHoraria("Jueves", "10:00", "10:30");
        gestionRecreos.crearRecreo(franja, "CT-Rec-003", List.of(periodoA));
        Recreo recreo = gestionRecreos.buscarPorDescripcion("CT-Rec-003");
        recorder.reset();
        gestionRecreos.aplicarA(recreo, periodoB);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.RECREOS));
        assertTrue(recorder.contiene(TipoEvento.RECREOS));
    }
    @Test
    void removerDe_emiteRECREOS() {
        FranjaHoraria franja = new FranjaHoraria("Viernes", "10:00", "10:30");
        gestionRecreos.crearRecreo(franja, "CT-Rec-004", List.of(periodoA, periodoB));
        Recreo recreo = gestionRecreos.buscarPorDescripcion("CT-Rec-004");
        recorder.reset();
        gestionRecreos.removerDe(recreo, periodoA);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.RECREOS));
        assertTrue(recorder.contiene(TipoEvento.RECREOS));
    }
    @Test
    void eliminarRecreo_emiteRECREOS() {
        FranjaHoraria franja = new FranjaHoraria("Sábado", "10:00", "10:30");
        gestionRecreos.crearRecreo(franja, "CT-Rec-005", List.of(periodoA));
        Recreo recreo = gestionRecreos.buscarPorDescripcion("CT-Rec-005");
        recorder.reset();
        gestionRecreos.eliminarRecreo(recreo);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.RECREOS));
        assertTrue(recorder.contiene(TipoEvento.RECREOS));
    }
}
