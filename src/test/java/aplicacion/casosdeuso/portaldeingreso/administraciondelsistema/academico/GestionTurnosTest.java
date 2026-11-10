/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;

import dominio.modelo.Turno;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioTurnos;
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
class GestionTurnosTest {

    private RepositorioTurnos repoMock;
    private LoggerPort loggerMock;
    private dominio.puerto.externo.EventPublisher publisherMock;
    private GestionTurnos casoUso;

    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioTurnos.class);
        loggerMock = mock(LoggerPort.class);
        publisherMock = mock(dominio.puerto.externo.EventPublisher.class);
        casoUso = new GestionTurnos(repoMock, loggerMock, publisherMock);
    }


    @Test
    void crearTurno_debeLanzarExcepcionCuandoYaExiste() {
        when(repoMock.buscarPorNombre("Mañana")).thenReturn(Optional.of(new Turno("Mañana")));
        assertThrows(IllegalArgumentException.class, () -> casoUso.crearTurno("Mañana"));
        verify(repoMock, never()).agregar(any());
    }

    @Test
    void listarTodos_debeRetornarListaCompleta() {
        List<Turno> lista = List.of(new Turno("Mañana"), new Turno("Tarde"));
        when(repoMock.listarTodos()).thenReturn(lista);
        assertEquals(2, casoUso.listarTodos().size());
        verify(repoMock).listarTodos();
    }

    @Test
    void buscarPorNombre_debeRetornarTurnoSiExiste() {
        Turno esperado = new Turno("Noche");
        when(repoMock.buscarPorNombre("Noche")).thenReturn(Optional.of(esperado));
        assertEquals(esperado, casoUso.buscarPorNombre("Noche"));
    }

    @Test
    void buscarPorNombre_debeRetornarNullSiNoExiste() {
        when(repoMock.buscarPorNombre("Inexistente")).thenReturn(Optional.empty());
        assertNull(casoUso.buscarPorNombre("Inexistente"));
    }

    @Test
    void actualizarTurno_debeActualizarConNuevoNombre() {
        Turno original = new Turno("Mañana");
        casoUso.actualizarTurno(original, "Matutino");
        verify(repoMock).actualizar(eq(original), any(Turno.class));
    }


}
