package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;

import dominio.modelo.Administrador;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioAdministradores;
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
class GestionAdministradoresTest {

    private RepositorioAdministradores repoMock;
    private LoggerPort loggerMock;
    private GestionAdministradores casoUso;

    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioAdministradores.class);
        loggerMock = mock(LoggerPort.class);
        casoUso = new GestionAdministradores(repoMock, loggerMock);
    }

    @Test
    void agregar_debeLanzarExcepcionSiNulo() {
        assertThrows(IllegalArgumentException.class, () -> casoUso.agregar(null));
        verify(repoMock, never()).agregar(any());
    }


    @Test
    void agregar_debeLanzarExcepcionSiCodigoDuplicado() {
        Administrador existente = new Administrador("A001", "hash", "111", "Admin", "Uno", 30);
        Administrador nuevo = new Administrador("A001", "hash2", "222", "Otro", "Dos", 35);
        when(repoMock.buscarPorCodigo("A001")).thenReturn(Optional.of(existente));

        assertThrows(IllegalArgumentException.class, () -> casoUso.agregar(nuevo));
        verify(repoMock, never()).agregar(any());
    }

    @Test
    void buscarPorCodigo_debeRetornarSiExiste() {
        Administrador esperado = new Administrador("A001", "hash", "111", "Admin", "Uno", 30);
        when(repoMock.buscarPorCodigo("A001")).thenReturn(Optional.of(esperado));
        assertEquals(esperado, casoUso.buscarPorCodigo("A001"));
    }

    @Test
    void buscarPorCodigo_debeRetornarNullSiNoExiste() {
        when(repoMock.buscarPorCodigo("X")).thenReturn(Optional.empty());
        assertNull(casoUso.buscarPorCodigo("X"));
    }

    @Test
    void listarTodos_debeRetornarListaCompleta() {
        List<Administrador> lista = List.of(
                new Administrador("A001", "hash", "111", "A", "Uno", 30),
                new Administrador("A002", "hash", "222", "B", "Dos", 40));
        when(repoMock.listarTodos()).thenReturn(lista);
        assertEquals(2, casoUso.listarTodos().size());
    }


}
