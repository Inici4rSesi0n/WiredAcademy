package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;
import dominio.modelo.CoordinadorAcademico;
import dominio.modelo.Grado;
import dominio.modelo.TipoEvento;
import dominio.puerto.externo.EventPublisher;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioCoordinadores;
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
class GestionCoordinadoresTest {
    private RepositorioCoordinadores repoMock;
    private LoggerPort loggerMock;
    private EventPublisher publisherMock;
    private GestionCoordinadores casoUso;
    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioCoordinadores.class);
        loggerMock = mock(LoggerPort.class);
        publisherMock = mock(EventPublisher.class);
        casoUso = new GestionCoordinadores(repoMock, loggerMock, publisherMock);
    }
    @Test
    void agregar_debeLanzarExcepcionSiNulo() {
        assertThrows(IllegalArgumentException.class, () -> casoUso.agregar(null));
        verify(repoMock, never()).agregar(any());
    }
    @Test
    void agregar_debeLanzarExcepcionSiCodigoDuplicado() {
        CoordinadorAcademico existente = new CoordinadorAcademico("COORD001", "hash", "111", "Coord", "Uno", 40);
        CoordinadorAcademico nuevo = new CoordinadorAcademico("COORD001", "hash2", "222", "Otro", "Dos", 45);
        when(repoMock.buscarPorCodigo("COORD001")).thenReturn(Optional.of(existente));
        assertThrows(IllegalArgumentException.class, () -> casoUso.agregar(nuevo));
        verify(repoMock, never()).agregar(any());
    }
    @Test
    void buscarPorCodigo_debeRetornarSiExiste() {
        CoordinadorAcademico esperado = new CoordinadorAcademico("COORD001", "hash", "111", "Coord", "Uno", 40);
        when(repoMock.buscarPorCodigo("COORD001")).thenReturn(Optional.of(esperado));
        assertEquals(esperado, casoUso.buscarPorCodigo("COORD001"));
    }
    @Test
    void buscarPorCodigo_debeRetornarNullSiNoExiste() {
        when(repoMock.buscarPorCodigo("X")).thenReturn(Optional.empty());
        assertNull(casoUso.buscarPorCodigo("X"));
    }
    @Test
    void listarTodos_debeRetornarListaCompleta() {
        List<CoordinadorAcademico> lista = List.of(
                new CoordinadorAcademico("COORD001", "hash", "111", "A", "Uno", 40),
                new CoordinadorAcademico("COORD002", "hash", "222", "B", "Dos", 45));
        when(repoMock.listarTodos()).thenReturn(lista);
        assertEquals(2, casoUso.listarTodos().size());
    }
    @Test
    void agregarGrado_notificaCoordinadoresYGrados() {
        CoordinadorAcademico coord = new CoordinadorAcademico("COORD100", "hash", "900", "C", "Test", 40);
        Grado grado = new Grado("1er Grado", "Primaria");
        when(repoMock.buscarPorCodigo("COORD100")).thenReturn(Optional.of(coord));
        casoUso.agregarGrado("COORD100", grado);
        assertTrue(coord.getGradosSupervisados().contains(grado));
        verify(repoMock).actualizar(coord, coord);
        verify(publisherMock, times(1)).notificar(TipoEvento.COORDINADORES);
        verify(publisherMock, times(1)).notificar(TipoEvento.GRADOS);
        verifyNoMoreInteractions(publisherMock);
    }
    @Test
    void removerGrado_notificaCoordinadoresYGrados() {
        CoordinadorAcademico coord = new CoordinadorAcademico("COORD200", "hash", "901", "C", "Test", 40);
        Grado grado = new Grado("2do Grado", "Primaria");
        coord.agregarGrado(grado);
        when(repoMock.buscarPorCodigo("COORD200")).thenReturn(Optional.of(coord));
        casoUso.removerGrado("COORD200", grado);
        assertFalse(coord.getGradosSupervisados().contains(grado));
        verify(repoMock).actualizar(coord, coord);
        verify(publisherMock, times(1)).notificar(TipoEvento.COORDINADORES);
        verify(publisherMock, times(1)).notificar(TipoEvento.GRADOS);
        verifyNoMoreInteractions(publisherMock);
    }
}
