package integration.eventos.casosdeuso.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAulas;
import dominio.modelo.Aula;
import dominio.modelo.ModalidadAula;
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
class GestionAulasContractIT extends BaseContractTest {
    @Autowired
    private GestionAulas gestionAulas;
    @Test
    void crearAula_emiteAULAS() {
        gestionAulas.crearAula("CT-Aula-001", 30, "Piso CT", "Teoría", ModalidadAula.PRESENCIAL);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.AULAS));
        assertTrue(recorder.contiene(TipoEvento.AULAS));
    }
    @Test
    void actualizarAula_emiteAULAS() {
        gestionAulas.crearAula("CT-Aula-002", 30, "Piso CT", "Teoría", ModalidadAula.PRESENCIAL);
        Aula original = gestionAulas.buscarAula("CT-Aula-002");
        recorder.reset();
        gestionAulas.actualizarAula(original, "CT-Aula-002-UPD", 40, "Piso CT 2", "Laboratorio", ModalidadAula.VIRTUAL);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.AULAS));
        assertTrue(recorder.contiene(TipoEvento.AULAS));
    }
    @Test
    void eliminarAula_emiteAULAS() {
        gestionAulas.crearAula("CT-Aula-003", 30, "Piso CT", "Teoría", ModalidadAula.PRESENCIAL);
        Aula aula = gestionAulas.buscarAula("CT-Aula-003");
        recorder.reset();
        gestionAulas.eliminarAula(aula);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.AULAS));
        assertTrue(recorder.contiene(TipoEvento.AULAS));
    }
}
