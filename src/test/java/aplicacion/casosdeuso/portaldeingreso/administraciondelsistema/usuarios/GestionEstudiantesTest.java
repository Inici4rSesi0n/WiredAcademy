package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;

import dominio.modelo.Estudiante;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioEstudiantes;
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
class GestionEstudiantesTest {

    private RepositorioEstudiantes repoMock;
    private LoggerPort loggerMock;
    private GestionEstudiantes casoUso;

    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioEstudiantes.class);
        loggerMock = mock(LoggerPort.class);
        casoUso = new GestionEstudiantes(repoMock, loggerMock);
    }

    @Test
    void agregar_debeLanzarExcepcionSiNulo() {
        assertThrows(IllegalArgumentException.class, () -> casoUso.agregar(null));
        verify(repoMock, never()).agregar(any());
    }


    @Test
    void agregar_debeLanzarExcepcionSiCodigoDuplicado() {
        Estudiante existente = new Estudiante("E001", "hash", "111", "Ana", "Gomez", 12);
        Estudiante nuevo = new Estudiante("E001", "hash2", "222", "Otro", "Uno", 13);
        when(repoMock.buscarPorCodigo("E001")).thenReturn(Optional.of(existente));
        assertThrows(IllegalArgumentException.class, () -> casoUso.agregar(nuevo));
        verify(repoMock, never()).agregar(any());
    }

    @Test
    void agregar_debeLanzarExcepcionSiDniDuplicado() {
        Estudiante existente = new Estudiante("E001", "hash", "111", "Ana", "Gomez", 12);
        Estudiante nuevo = new Estudiante("E002", "hash2", "111", "Otro", "Uno", 13);
        when(repoMock.buscarPorCodigo("E002")).thenReturn(Optional.empty());
        when(repoMock.buscarPorDni("111")).thenReturn(Optional.of(existente));
        assertThrows(IllegalArgumentException.class, () -> casoUso.agregar(nuevo));
        verify(repoMock, never()).agregar(any());
    }

    @Test
    void buscarPorCodigo_debeRetornarSiExiste() {
        Estudiante esperado = new Estudiante("E001", "hash", "111", "Ana", "Gomez", 12);
        when(repoMock.buscarPorCodigo("E001")).thenReturn(Optional.of(esperado));
        assertEquals(esperado, casoUso.buscarPorCodigo("E001"));
    }

    @Test
    void buscarPorCodigo_debeRetornarNullSiNoExiste() {
        when(repoMock.buscarPorCodigo("X")).thenReturn(Optional.empty());
        assertNull(casoUso.buscarPorCodigo("X"));
    }

    @Test
    void buscarPorDni_debeRetornarSiExiste() {
        Estudiante esperado = new Estudiante("E001", "hash", "111", "Ana", "Gomez", 12);
        when(repoMock.buscarPorDni("111")).thenReturn(Optional.of(esperado));
        assertEquals(esperado, casoUso.buscarPorDni("111"));
    }

    @Test
    void listarTodos_debeRetornarListaCompleta() {
        List<Estudiante> lista = List.of(
                new Estudiante("E001", "hash", "111", "Ana", "Gomez", 12),
                new Estudiante("E002", "hash", "222", "Luis", "Perez", 13));
        when(repoMock.listarTodos()).thenReturn(lista);
        assertEquals(2, casoUso.listarTodos().size());
    }


}
