/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.miareapersonal;
import dominio.modelo.Asignatura;
import dominio.modelo.Evaluacion;
import dominio.modelo.TipoEvento;
import dominio.puerto.externo.EventPublisher;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioEvaluaciones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/**
 *
 * @author inici4rsesi0n
 */
class GestionEvaluacionesTest {
    private RepositorioEvaluaciones repoMock;
    private LoggerPort loggerMock;
    private EventPublisher publisherMock;
    private GestionEvaluaciones casoUso;
    private Asignatura asignatura;
    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioEvaluaciones.class);
        loggerMock = mock(LoggerPort.class);
        publisherMock = mock(EventPublisher.class);
        casoUso = new GestionEvaluaciones(repoMock, loggerMock, publisherMock);
        asignatura = new Asignatura("Matemáticas");
    }
    @Test
    void crearEvaluacion_debeFallarSiNombreVacio() {
        assertThrows(IllegalArgumentException.class, () -> casoUso.crearEvaluacion("", 0.2, asignatura));
        verify(repoMock, never()).agregar(any());
    }
    @Test
    void crearEvaluacion_debeFallarSiPesoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> casoUso.crearEvaluacion("PC1", 0.0, asignatura));
        assertThrows(IllegalArgumentException.class, () -> casoUso.crearEvaluacion("PC1", 1.5, asignatura));
        verify(repoMock, never()).agregar(any());
    }
    @Test
    void crearEvaluacion_debeFallarSiAsignaturaNula() {
        assertThrows(IllegalArgumentException.class, () -> casoUso.crearEvaluacion("PC1", 0.2, null));
        verify(repoMock, never()).agregar(any());
    }
    @Test
    void crearEvaluacion_debeAgregarYNotificar() {
        casoUso.crearEvaluacion("PC1", 0.2, asignatura);
        verify(repoMock).agregar(any(Evaluacion.class));
        verify(publisherMock).notificar(TipoEvento.EVALUACIONES);
        verify(loggerMock).audit(contains("op=CREATE entity=EVALUACION"));
    }
    @Test
    void listarPorAsignatura_debeRetornarLista() {
        casoUso.listarPorAsignatura("Matemáticas");
        verify(repoMock).buscarPorAsignatura("Matemáticas");
    }
    @Test
    void eliminarEvaluacion_debeEliminarYNotificar() {
        Evaluacion ev = new Evaluacion(1L, "PC1", 0.2, asignatura);
        casoUso.eliminarEvaluacion(ev);
        verify(repoMock).eliminar(ev);
        verify(publisherMock).notificar(TipoEvento.EVALUACIONES);
    }
}
