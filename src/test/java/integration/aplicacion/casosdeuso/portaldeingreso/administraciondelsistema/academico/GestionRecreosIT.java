package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionRecreos;
import dominio.modelo.FranjaHoraria;
import dominio.modelo.PeriodoAcademico;
import dominio.modelo.Recreo;
import dominio.puerto.repositorio.RepositorioPeriodos;
import integration.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
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
    @Autowired private EntityManager em;
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
    @Test
    public void testActualizarRecreo() {
        PeriodoAcademico periodo = new PeriodoAcademico("RECREO-IT-Periodo4", "2026-01-01", "2026-06-30", "Activo");
        repoPeriodos.agregar(periodo);
        FranjaHoraria franja = new FranjaHoraria("Jueves", "10:00", "10:30");
        gestionRecreos.crearRecreo(franja, "RECREO-IT-UP", List.of(periodo));
        Recreo original = gestionRecreos.buscarPorDescripcion("RECREO-IT-UP");
        FranjaHoraria nuevaFranja = new FranjaHoraria("Viernes", "11:00", "11:30");
        gestionRecreos.actualizarRecreo(original, nuevaFranja, "RECREO-IT-UP-NEW", List.of(periodo));
        em.flush(); em.clear();
        assertNull(gestionRecreos.buscarPorDescripcion("RECREO-IT-UP"));
        Recreo actualizado = gestionRecreos.buscarPorDescripcion("RECREO-IT-UP-NEW");
        assertNotNull(actualizado);
        assertEquals("Viernes", actualizado.getDiaSemana());
        assertEquals("11:00", actualizado.getHoraInicio());
    }
    @Test
    public void testEliminarRecreo() {
        PeriodoAcademico periodo = new PeriodoAcademico("RECREO-IT-Periodo5", "2026-01-01", "2026-06-30", "Activo");
        repoPeriodos.agregar(periodo);
        FranjaHoraria franja = new FranjaHoraria("Sábado", "10:00", "10:30");
        gestionRecreos.crearRecreo(franja, "RECREO-IT-DEL", List.of(periodo));
        Recreo r = gestionRecreos.buscarPorDescripcion("RECREO-IT-DEL");
        gestionRecreos.eliminarRecreo(r);
        em.flush(); em.clear();
        assertNull(gestionRecreos.buscarPorDescripcion("RECREO-IT-DEL"));
    }
}
