package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import dominio.modelo.FranjaHoraria;
import dominio.modelo.PeriodoAcademico;
import dominio.modelo.Recreo;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioRecreos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author inici4rsesi0n
 */
class GestionRecreosTest {

    private RepositorioRecreos repoMock;
    private LoggerPort loggerMock;
    private GestionRecreos casoUso;
    private FranjaHoraria franja;
    private PeriodoAcademico periodoA;
    private PeriodoAcademico periodoB;

    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioRecreos.class);
        loggerMock = mock(LoggerPort.class);
        casoUso = new GestionRecreos(repoMock, loggerMock);
        franja = new FranjaHoraria("Lunes", "10:00", "10:30");
        periodoA = new PeriodoAcademico("2026-I", "2026-01-01", "2026-06-30", "Activo");
        periodoB = new PeriodoAcademico("2026-II", "2026-07-01", "2026-12-31", "Activo");
    }



    @Test
    void crearRecreo_debeFallarSiFranjaNula() {
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearRecreo(null, "Recreo", List.of(periodoA)));
    }

    @Test
    void crearRecreo_debeFallarSiListaVacia() {
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearRecreo(franja, "Recreo", List.of()));
    }

    @Test
    void buscarPorDescripcion_debeRetornarSiExiste() {
        Recreo esperado = new Recreo(franja, "Recreo", List.of(periodoA));
        when(repoMock.buscarPorDescripcion("Recreo")).thenReturn(Optional.of(esperado));
        assertEquals(esperado, casoUso.buscarPorDescripcion("Recreo"));
    }

    @Test
    void buscarPorDescripcion_debeRetornarNullSiNoExiste() {
        when(repoMock.buscarPorDescripcion("X")).thenReturn(Optional.empty());
        assertNull(casoUso.buscarPorDescripcion("X"));
    }

    @Test
    void listarTodos_debeRetornarListaCompleta() {
        List<Recreo> lista = List.of(
                new Recreo(franja, "Recreo A", List.of(periodoA)),
                new Recreo(new FranjaHoraria("Martes", "10:00", "10:30"), "Recreo B", List.of(periodoA)));
        when(repoMock.listarTodos()).thenReturn(lista);
        assertEquals(2, casoUso.listarTodos().size());
        verify(repoMock).listarTodos();
    }


    @Test
    void aplicarA_debeAsociarPeriodoYActualizar() {
        Recreo recreo = new Recreo(franja, "Recreo", List.of(periodoA));
        casoUso.aplicarA(recreo, periodoB);
        assertTrue(recreo.aplicaEn(periodoB));
        verify(repoMock).actualizar(recreo, recreo);
    }

    @Test
    void removerDe_debeQuitarPeriodoYActualizar() {
        Recreo recreo = new Recreo(franja, "Recreo", List.of(periodoA, periodoB));
        casoUso.removerDe(recreo, periodoA);
        assertFalse(recreo.aplicaEn(periodoA));
        verify(repoMock).actualizar(recreo, recreo);
    }

    @Test
    void crearRecreo_debeFallarSiFranjaDescripcionYPeriodoSolapan() {
        Recreo existente = new Recreo(franja, "Recreo", List.of(periodoA));
        when(repoMock.listarTodos()).thenReturn(List.of(existente));

        assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearRecreo(franja, "Recreo", List.of(periodoA)));
        verify(repoMock, never()).agregar(any());
    }

    @Test
    void crearRecreo_debeFallarSiComparteAlMenosUnPeriodoDeVarios() {
        Recreo existente = new Recreo(franja, "Recreo", List.of(periodoA, periodoB));
        when(repoMock.listarTodos()).thenReturn(List.of(existente));

        assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearRecreo(franja, "Recreo", List.of(periodoA)));
        verify(repoMock, never()).agregar(any());
    }

    @Test
    void crearRecreo_debePermitirSiNoHayOverlapDePeriodos() {
        Recreo existente = new Recreo(franja, "Recreo", List.of(periodoA));
        when(repoMock.listarTodos()).thenReturn(List.of(existente));

        casoUso.crearRecreo(franja, "Recreo", List.of(periodoB));

        verify(repoMock).agregar(any(Recreo.class));
    }

    @Test
    void actualizarRecreo_debeFallarSiSolapaConOtro() {
        Recreo original = new Recreo(franja, "Recreo", List.of(periodoA));
        Recreo otro = new Recreo(franja, "Recreo", List.of(periodoB));
        when(repoMock.listarTodos()).thenReturn(List.of(original, otro));

        assertThrows(IllegalArgumentException.class,
                () -> casoUso.actualizarRecreo(original, franja, "Recreo", List.of(periodoB)));
        verify(repoMock, never()).actualizar(any(), any());
    }

    @Test
    void actualizarRecreo_debePermitirSiElUnicoOverlapEsElOriginal() {
        Recreo original = new Recreo(franja, "Recreo", List.of(periodoA, periodoB));
        when(repoMock.listarTodos()).thenReturn(List.of(original));

        casoUso.actualizarRecreo(original, franja, "Recreo", List.of(periodoA));

        verify(repoMock).actualizar(original, original);
    }
}
