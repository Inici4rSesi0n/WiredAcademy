package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;

import dominio.modelo.Director;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioDirectores;
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
class GestionDirectoresTest {

    private RepositorioDirectores repoMock;
    private LoggerPort loggerMock;
    private GestionDirectores casoUso;

    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioDirectores.class);
        loggerMock = mock(LoggerPort.class);
        casoUso = new GestionDirectores(repoMock, loggerMock);
    }

    @Test
    void agregar_debeLanzarExcepcionSiNulo() {
        assertThrows(IllegalArgumentException.class, () -> casoUso.agregar(null));
        verify(repoMock, never()).agregar(any());
    }


    @Test
    void agregar_debeLanzarExcepcionSiCodigoDuplicado() {
        Director existente = new Director("DIR001", "hash", "111", "Dir", "Uno", 40);
        Director nuevo = new Director("DIR001", "hash2", "222", "Otro", "Dos", 45);
        when(repoMock.buscarPorCodigo("DIR001")).thenReturn(Optional.of(existente));
        assertThrows(IllegalArgumentException.class, () -> casoUso.agregar(nuevo));
        verify(repoMock, never()).agregar(any());
    }

    @Test
    void buscarPorCodigo_debeRetornarSiExiste() {
        Director esperado = new Director("DIR001", "hash", "111", "Dir", "Uno", 40);
        when(repoMock.buscarPorCodigo("DIR001")).thenReturn(Optional.of(esperado));
        assertEquals(esperado, casoUso.buscarPorCodigo("DIR001"));
    }

    @Test
    void buscarPorCodigo_debeRetornarNullSiNoExiste() {
        when(repoMock.buscarPorCodigo("X")).thenReturn(Optional.empty());
        assertNull(casoUso.buscarPorCodigo("X"));
    }

    @Test
    void listarTodos_debeRetornarListaCompleta() {
        List<Director> lista = List.of(
                new Director("DIR001", "hash", "111", "A", "Uno", 40),
                new Director("DIR002", "hash", "222", "B", "Dos", 45));
        when(repoMock.listarTodos()).thenReturn(lista);
        assertEquals(2, casoUso.listarTodos().size());
    }


}
