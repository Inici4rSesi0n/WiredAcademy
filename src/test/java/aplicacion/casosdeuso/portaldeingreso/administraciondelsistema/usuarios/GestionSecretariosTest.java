package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;

import dominio.modelo.Secretario;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioSecretarios;
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
class GestionSecretariosTest {

    private RepositorioSecretarios repoMock;
    private LoggerPort loggerMock;
    private GestionSecretarios casoUso;

    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioSecretarios.class);
        loggerMock = mock(LoggerPort.class);
        casoUso = new GestionSecretarios(repoMock, loggerMock);
    }

    @Test
    void agregar_debeLanzarExcepcionSiNulo() {
        assertThrows(IllegalArgumentException.class, () -> casoUso.agregar(null));
        verify(repoMock, never()).agregar(any());
    }


    @Test
    void agregar_debeLanzarExcepcionSiCodigoDuplicado() {
        Secretario existente = new Secretario("SEC001", "hash", "111", "Sec", "Uno", 30);
        Secretario nuevo = new Secretario("SEC001", "hash2", "222", "Otro", "Dos", 35);
        when(repoMock.buscarPorCodigo("SEC001")).thenReturn(Optional.of(existente));
        assertThrows(IllegalArgumentException.class, () -> casoUso.agregar(nuevo));
        verify(repoMock, never()).agregar(any());
    }

    @Test
    void buscarPorCodigo_debeRetornarSiExiste() {
        Secretario esperado = new Secretario("SEC001", "hash", "111", "Sec", "Uno", 30);
        when(repoMock.buscarPorCodigo("SEC001")).thenReturn(Optional.of(esperado));
        assertEquals(esperado, casoUso.buscarPorCodigo("SEC001"));
    }

    @Test
    void buscarPorCodigo_debeRetornarNullSiNoExiste() {
        when(repoMock.buscarPorCodigo("X")).thenReturn(Optional.empty());
        assertNull(casoUso.buscarPorCodigo("X"));
    }

    @Test
    void listarTodos_debeRetornarListaCompleta() {
        List<Secretario> lista = List.of(
                new Secretario("SEC001", "hash", "111", "A", "Uno", 30),
                new Secretario("SEC002", "hash", "222", "B", "Dos", 35));
        when(repoMock.listarTodos()).thenReturn(lista);
        assertEquals(2, casoUso.listarTodos().size());
    }


}
