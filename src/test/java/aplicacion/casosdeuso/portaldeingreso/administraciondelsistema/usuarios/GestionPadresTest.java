package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;

import dominio.modelo.Estudiante;
import dominio.modelo.Padre;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioPadres;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author inici4rsesi0n
 */
class GestionPadresTest {

    private RepositorioPadres repoMock;
    private LoggerPort loggerMock;
    private GestionPadres casoUso;

    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioPadres.class);
        loggerMock = mock(LoggerPort.class);
        casoUso = new GestionPadres(repoMock, loggerMock);
    }


    @Test
    void agregar_debeLanzarExcepcionSiCodigoDuplicado() {
        Padre existente = new Padre("P001", "hash", "111", "Padre", "Uno", 40, new ArrayList<>());
        Padre nuevo = new Padre("P001", "hash2", "222", "Padre", "Dos", 45, new ArrayList<>());
        when(repoMock.buscarPorCodigo("P001")).thenReturn(Optional.of(existente));

        assertThrows(IllegalArgumentException.class, () -> casoUso.agregar(nuevo));
        verify(repoMock, never()).agregar(any());
    }

    @Test
    void agregar_debeLanzarExcepcionSiPadreNulo() {
        assertThrows(IllegalArgumentException.class, () -> casoUso.agregar(null));
    }

    @Test
    void buscarPorCodigo_debeRetornarPadreSiExiste() {
        Padre esperado = new Padre("P001", "hash", "111", "Padre", "Uno", 40, new ArrayList<>());
        when(repoMock.buscarPorCodigo("P001")).thenReturn(Optional.of(esperado));

        Padre resultado = casoUso.buscarPorCodigo("P001");
        assertEquals(esperado, resultado);
    }

    @Test
    void buscarPorCodigo_debeRetornarNullSiNoExiste() {
        when(repoMock.buscarPorCodigo("P999")).thenReturn(Optional.empty());

        Padre resultado = casoUso.buscarPorCodigo("P999");
        assertNull(resultado);
    }

    @Test
    void listarTodos_debeRetornarListaCompleta() {
        List<Padre> lista = List.of(
                new Padre("P001", "hash1", "111", "Padre1", "Uno", 40, new ArrayList<>()),
                new Padre("P002", "hash2", "222", "Padre2", "Dos", 45, new ArrayList<>())
        );
        when(repoMock.listarTodos()).thenReturn(lista);

        List<Padre> resultado = casoUso.listarTodos();
        assertEquals(2, resultado.size());
        verify(repoMock).listarTodos();
    }

    @Test
    void actualizar_debeActualizarPadre() {
        Padre original = new Padre("P001", "hash", "111", "Padre", "Uno", 40, new ArrayList<>());
        Padre actualizado = new Padre("P001", "hashNuevo", "999", "PadreMod", "UnoMod", 50, new ArrayList<>());

        casoUso.actualizar(original, actualizado);

        verify(repoMock).actualizar(original, actualizado);
    }


    @Test
    void vincularEstudiante_debeAgregarHijoYActualizar() {
        Padre padre = new Padre("P001", "hash", "111", "Padre", "Uno", 40, new ArrayList<>());
        Estudiante hijo = new Estudiante("E001", "hash", "111", "Hijo", "Uno", 10);

        casoUso.vincularEstudiante(padre, hijo);

        assertTrue(padre.getEstudiantes().contains(hijo));
        verify(repoMock).actualizar(padre, padre);
    }

    @Test
    void desvincularEstudiante_debeRemoverHijoYActualizar() {
        Estudiante hijo = new Estudiante("E001", "hash", "111", "Hijo", "Uno", 10);
        List<Estudiante> hijos = new ArrayList<>();
        hijos.add(hijo);
        Padre padre = new Padre("P001", "hash", "111", "Padre", "Uno", 40, hijos);

        casoUso.desvincularEstudiante(padre, hijo);

        assertFalse(padre.getEstudiantes().contains(hijo));
        verify(repoMock).actualizar(padre, padre);
    }
}
