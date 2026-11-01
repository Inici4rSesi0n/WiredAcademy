package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;

import dominio.modelo.Asignatura;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioAsignaturas;
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
class GestionAsignaturasTest {

    private RepositorioAsignaturas repoMock;
    private LoggerPort loggerMock;
    private dominio.puerto.externo.EventPublisher publisherMock;
    private GestionAsignaturas casoUso;

    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioAsignaturas.class);
        loggerMock = mock(LoggerPort.class);
        publisherMock = mock(dominio.puerto.externo.EventPublisher.class);
        casoUso = new GestionAsignaturas(repoMock, loggerMock, publisherMock);
    }


    @Test
    void crearAsignatura_debeFallarSiNombreVacio() {
        assertThrows(IllegalArgumentException.class, () -> casoUso.crearAsignatura(""));
        verify(repoMock, never()).agregar(any());
    }

    @Test
    void crearAsignatura_debeFallarSiDuplicado() {
        when(repoMock.buscarPorNombre("Matemáticas")).thenReturn(Optional.of(new Asignatura("Matemáticas")));
        assertThrows(IllegalArgumentException.class, () -> casoUso.crearAsignatura("Matemáticas"));
        verify(repoMock, never()).agregar(any());
    }

    @Test
    void listarTodos_debeRetornarListaCompleta() {
        List<Asignatura> lista = List.of(new Asignatura("Mat"), new Asignatura("Fís"));
        when(repoMock.listarTodos()).thenReturn(lista);
        assertEquals(2, casoUso.listarTodos().size());
    }

    @Test
    void buscarAsignatura_debeRetornarSiExiste() {
        Asignatura esperada = new Asignatura("Matemáticas");
        when(repoMock.buscarPorNombre("Matemáticas")).thenReturn(Optional.of(esperada));
        assertEquals(esperada, casoUso.buscarAsignatura("Matemáticas"));
    }

    @Test
    void buscarAsignatura_debeRetornarNullSiNoExiste() {
        when(repoMock.buscarPorNombre("X")).thenReturn(Optional.empty());
        assertNull(casoUso.buscarAsignatura("X"));
    }


    @Test
    void actualizarAsignatura_debeFallarSiNombreVacio() {
        Asignatura original = new Asignatura("Matemáticas");
        assertThrows(IllegalArgumentException.class, () -> casoUso.actualizarAsignatura(original, ""));
    }

}
