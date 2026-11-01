package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;

import dominio.modelo.PeriodoAcademico;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioPeriodos;
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
class GestionPeriodosTest {

    private RepositorioPeriodos repoMock;
    private LoggerPort loggerMock;
    private GestionPeriodos casoUso;

    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioPeriodos.class);
        loggerMock = mock(LoggerPort.class);
        casoUso = new GestionPeriodos(repoMock, loggerMock);
    }


    @Test
    void crearPeriodo_debeLanzarExcepcionCuandoYaExiste() {
        when(repoMock.buscarPorNombre("2026-I"))
                .thenReturn(Optional.of(new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", "Activo")));
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearPeriodo("2026-I", "2026-01-01", "2026-12-31", "Activo"));
        verify(repoMock, never()).agregar(any());
    }

    @Test
    void buscarPorNombre_debeRetornarPeriodoSiExiste() {
        PeriodoAcademico esperado = new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", "Activo");
        when(repoMock.buscarPorNombre("2026-I")).thenReturn(Optional.of(esperado));
        assertEquals(esperado, casoUso.buscarPorNombre("2026-I"));
    }

    @Test
    void buscarPorNombre_debeRetornarNullSiNoExiste() {
        when(repoMock.buscarPorNombre("2030-X")).thenReturn(Optional.empty());
        assertNull(casoUso.buscarPorNombre("2030-X"));
    }

    @Test
    void listarTodos_debeRetornarListaCompleta() {
        List<PeriodoAcademico> lista = List.of(
                new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", "Activo"),
                new PeriodoAcademico("2026-II", "2026-07-01", "2026-12-31", "Activo"));
        when(repoMock.listarTodos()).thenReturn(lista);
        assertEquals(2, casoUso.listarTodos().size());
        verify(repoMock).listarTodos();
    }

    @Test
    void actualizarPeriodo_debeActualizarConNuevosDatos() {
        PeriodoAcademico original = new PeriodoAcademico("2026-I", "2026-01-01", "2026-06-30", "Activo");
        casoUso.actualizarPeriodo(original, "2026-I-Mod", "2026-02-01", "2026-07-31", "Culminado");
        verify(repoMock).actualizar(eq(original), any(PeriodoAcademico.class));
    }


}
