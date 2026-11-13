/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.miareapersonal;
import dominio.modelo.*;
import dominio.puerto.externo.EventPublisher;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioCalificaciones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/**
 *
 * @author inici4rsesi0n
 */
class GestionCalificacionesTest {
    private RepositorioCalificaciones repoMock;
    private LoggerPort loggerMock;
    private EventPublisher publisherMock;
    private GestionCalificaciones casoUso;
    private Evaluacion evaluacion;
    private Estudiante estudiante;
    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioCalificaciones.class);
        loggerMock = mock(LoggerPort.class);
        publisherMock = mock(EventPublisher.class);
        casoUso = new GestionCalificaciones(repoMock, loggerMock, publisherMock);
        evaluacion = new Evaluacion(1L, "PC1", 0.2, new Asignatura("Matemáticas"));
        estudiante = new Estudiante("E001", "hash", "111", "Carlos", "Gomez", 15);
    }
    @Test
    void registrarNota_debeFallarSiEvaluacionNula() {
        assertThrows(IllegalArgumentException.class, () -> casoUso.registrarNota(null, estudiante, 15.0));
        verify(repoMock, never()).agregar(any());
    }
    @Test
    void registrarNota_debeFallarSiEstudianteNulo() {
        assertThrows(IllegalArgumentException.class, () -> casoUso.registrarNota(evaluacion, null, 15.0));
        verify(repoMock, never()).agregar(any());
    }
    @Test
    void registrarNota_debeFallarSiNotaFueraDeRango() {
        assertThrows(IllegalArgumentException.class, () -> casoUso.registrarNota(evaluacion, estudiante, -1.0));
        assertThrows(IllegalArgumentException.class, () -> casoUso.registrarNota(evaluacion, estudiante, 21.0));
        verify(repoMock, never()).agregar(any());
    }
    @Test
    void registrarNota_debeAgregarYNotificar() {
        casoUso.registrarNota(evaluacion, estudiante, 15.0);
        verify(repoMock).agregar(any(Calificacion.class));
        verify(publisherMock).notificar(TipoEvento.CALIFICACIONES);
        verify(loggerMock).audit(contains("op=CREATE entity=CALIFICACION"));
    }
    @Test
    void calcularPromedioPonderado_debeCalcularCorrectamente() {
        Evaluacion pc1 = new Evaluacion(1L, "PC1", 0.2, new Asignatura("Mat"));
        Evaluacion exfn = new Evaluacion(2L, "EXFN", 0.6, new Asignatura("Mat"));
        List<Evaluacion> evaluaciones = List.of(pc1, exfn);
        Calificacion c1 = new Calificacion(1L, pc1, estudiante, 10.0);
        Calificacion c2 = new Calificacion(2L, exfn, estudiante, 15.0);
        when(repoMock.buscarPorEstudiante("E001")).thenReturn(List.of(c1, c2));
        double pp = casoUso.calcularPromedioPonderado("E001", evaluaciones);
        assertEquals(13.75, pp, 0.01);
    }
    @Test
    void calcularPromedioPonderado_debeRetornarCeroSiNoHayNotas() {
        when(repoMock.buscarPorEstudiante("E001")).thenReturn(new ArrayList<>());
        double pp = casoUso.calcularPromedioPonderado("E001", List.of(evaluacion));
        assertEquals(0.0, pp);
    }
}
