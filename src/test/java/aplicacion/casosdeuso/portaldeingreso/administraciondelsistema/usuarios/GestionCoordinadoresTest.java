package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;

import dominio.modelo.CoordinadorAcademico;
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
    private GestionCoordinadores casoUso;

    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioCoordinadores.class);
        loggerMock = mock(LoggerPort.class);
        casoUso = new GestionCoordinadores(repoMock, loggerMock);
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


}
