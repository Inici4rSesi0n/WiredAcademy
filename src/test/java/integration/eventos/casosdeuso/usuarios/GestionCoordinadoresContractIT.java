package integration.eventos.casosdeuso.usuarios;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionCoordinadores;
import dominio.modelo.CoordinadorAcademico;
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
class GestionCoordinadoresContractIT extends BaseContractTest {
    @Autowired private GestionCoordinadores gestionCoordinadores;
    @Autowired private GestionGrados gestionGrados;
    private CoordinadorAcademico crearCoordinador(String codigo, String dni) {
        CoordinadorAcademico coord = new CoordinadorAcademico(codigo, "hash", dni, "Coord", "CT", 40);
        gestionCoordinadores.agregar(coord);
        return gestionCoordinadores.buscarPorCodigo(codigo);
    }
    private Grado crearGrado(String nombre) {
        gestionGrados.crearGrado(nombre, "Primaria");
        return gestionGrados.buscarPorNombreYNivel(nombre, "Primaria");
    }
    @Test
    void agregarGrado_emiteCOORDINADORES() {
        crearCoordinador("CT-Coord-001", "94000001");
        Grado grado = crearGrado("CT-Coord-G1");
        recorder.reset();
        gestionCoordinadores.agregarGrado("CT-Coord-001", grado);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.COORDINADORES));
        assertTrue(recorder.contiene(TipoEvento.COORDINADORES));
    }
    @Test
    void agregarGrado_emiteGRADOS() {
        crearCoordinador("CT-Coord-002", "94000002");
        Grado grado = crearGrado("CT-Coord-G2");
        recorder.reset();
        gestionCoordinadores.agregarGrado("CT-Coord-002", grado);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.GRADOS));
        assertTrue(recorder.contiene(TipoEvento.GRADOS));
    }
    @Test
    void removerGrado_emiteCOORDINADORES() {
        crearCoordinador("CT-Coord-003", "94000003");
        Grado grado = crearGrado("CT-Coord-G3");
        gestionCoordinadores.agregarGrado("CT-Coord-003", grado);
        recorder.reset();
        gestionCoordinadores.removerGrado("CT-Coord-003", grado);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.COORDINADORES));
        assertTrue(recorder.contiene(TipoEvento.COORDINADORES));
    }
    @Test
    void removerGrado_emiteGRADOS() {
        crearCoordinador("CT-Coord-004", "94000004");
        Grado grado = crearGrado("CT-Coord-G4");
        gestionCoordinadores.agregarGrado("CT-Coord-004", grado);
        recorder.reset();
        gestionCoordinadores.removerGrado("CT-Coord-004", grado);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.GRADOS));
        assertTrue(recorder.contiene(TipoEvento.GRADOS));
    }
}
