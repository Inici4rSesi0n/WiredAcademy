package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionRecreos;
import dominio.modelo.FranjaHoraria;
import dominio.modelo.PeriodoAcademico;
import dominio.modelo.Recreo;
import dominio.puerto.repositorio.RepositorioPeriodos;
import integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionRecreosIT extends BaseIntegrationTest {
    @Autowired private GestionRecreos gestionRecreos;
    @Autowired private RepositorioPeriodos repoPeriodos;
    @Test
    public void testCrearRecreoCorrectamente() {
        PeriodoAcademico periodo = new PeriodoAcademico("RECREO-IT-Periodo", "2026-01-01", "2026-06-30", "Activo");
        repoPeriodos.agregar(periodo);
        FranjaHoraria franja = new FranjaHoraria("Lunes", "10:00", "10:30");
        gestionRecreos.crearRecreo(franja, "RECREO-IT-Descripcion", List.of(periodo));
        Recreo recreo = gestionRecreos.buscarPorDescripcion("RECREO-IT-Descripcion");
        assertNotNull(recreo);
        assertEquals("Lunes", recreo.getDiaSemana());
        assertEquals(1, recreo.getPeriodos().size());
    }
    @Test
    public void testCrearRecreoDuplicadoFalla() {
        PeriodoAcademico periodo = new PeriodoAcademico("RECREO-IT-Periodo2", "2026-01-01", "2026-06-30", "Activo");
        repoPeriodos.agregar(periodo);
        FranjaHoraria franja = new FranjaHoraria("Martes", "10:00", "10:30");
        gestionRecreos.crearRecreo(franja, "RECREO-IT-Dup", List.of(periodo));
        assertThrows(IllegalArgumentException.class, () ->
            gestionRecreos.crearRecreo(franja, "RECREO-IT-Dup", List.of(periodo)));
    }
    @Test
    public void testAplicarYRemoverPeriodo() {
        PeriodoAcademico periodo = new PeriodoAcademico("RECREO-IT-Periodo3", "2026-01-01", "2026-06-30", "Activo");
        repoPeriodos.agregar(periodo);
        FranjaHoraria franja = new FranjaHoraria("Miércoles", "10:00", "10:30");
        gestionRecreos.crearRecreo(franja, "RECREO-IT-Aplicar", List.of(periodo));
        Recreo recreo = gestionRecreos.buscarPorDescripcion("RECREO-IT-Aplicar");
        gestionRecreos.removerDe(recreo, periodo);
        Recreo actualizado = gestionRecreos.buscarPorDescripcion("RECREO-IT-Aplicar");
        assertTrue(actualizado.getPeriodos().isEmpty());
    }
}
