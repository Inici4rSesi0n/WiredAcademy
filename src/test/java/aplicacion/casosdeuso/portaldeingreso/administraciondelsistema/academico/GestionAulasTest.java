/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;

import dominio.modelo.Aula;
import dominio.modelo.ModalidadAula;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioAulas;
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
class GestionAulasTest {

    private RepositorioAulas repoMock;
    private LoggerPort loggerMock;
    private dominio.puerto.externo.EventPublisher publisherMock;
    private GestionAulas casoUso;

    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioAulas.class);
        loggerMock = mock(LoggerPort.class);
        publisherMock = mock(dominio.puerto.externo.EventPublisher.class);
        casoUso = new GestionAulas(repoMock, loggerMock, publisherMock);
    }


    @Test
    void crearAula_debeFallarSiCapacidadInvalida() {
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearAula("Aula 101", 0, "Pabellón A", "Teoría", ModalidadAula.PRESENCIAL));
    }

    @Test
    void crearAula_debeFallarSiNombreVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearAula("", 40, "Pabellón A", "Teoría", ModalidadAula.PRESENCIAL));
    }

    @Test
    void crearAula_debeFallarSiDuplicado() {
        when(repoMock.buscarPorNombre("Aula 101")).thenReturn(Optional.of(new Aula("Aula 101", 40, "X", "Y")));
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearAula("Aula 101", 40, "Pabellón A", "Teoría", ModalidadAula.PRESENCIAL));
    }

    @Test
    void listarTodos_debeRetornarListaCompleta() {
        List<Aula> lista = List.of(
                new Aula("A101", 40, "P1", "Teoría"),
                new Aula("A102", 30, "P2", "Lab"));
        when(repoMock.listarTodos()).thenReturn(lista);
        assertEquals(2, casoUso.listarTodos().size());
    }

    @Test
    void buscarAula_debeRetornarSiExiste() {
        Aula esperada = new Aula("Aula 101", 40, "P1", "Teoría");
        when(repoMock.buscarPorNombre("Aula 101")).thenReturn(Optional.of(esperada));
        assertEquals(esperada, casoUso.buscarAula("Aula 101"));
    }


    @Test
    void actualizarAula_debeFallarSiCapacidadInvalida() {
        Aula original = new Aula("Aula 101", 40, "P1", "Teoría");
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.actualizarAula(original, "Aula 102", 0, "P2", "Lab", ModalidadAula.PRESENCIAL));
    }

}
