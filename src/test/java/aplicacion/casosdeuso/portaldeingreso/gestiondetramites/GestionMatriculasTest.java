package aplicacion.casosdeuso.portaldeingreso.gestiondetramites;
import dominio.modelo.*;
import dominio.puerto.externo.EventPublisher;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioMatriculas;
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
class GestionMatriculasTest {
    private RepositorioMatriculas repoMock;
    private LoggerPort loggerMock;
    private EventPublisher publisherMock;
    private GestionMatriculas casoUso;
    private Estudiante estudiante;
    private PeriodoAcademico periodo;
    private Grupo grupo;
    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioMatriculas.class);
        loggerMock = mock(LoggerPort.class);
        publisherMock = mock(EventPublisher.class);
        casoUso = new GestionMatriculas(repoMock, loggerMock, publisherMock);
        estudiante = new Estudiante("E001", "hash", "111", "Est", "Uno", 15);
        periodo = new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", "Activo");
        grupo = new Grupo("1A", new Grado("1er Grado", "Primaria"));
    }
    @Test
    void matricularEstudiante_debeLanzarExcepcionSiYaMatriculado() {
        Matricula existente = new Matricula(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        when(repoMock.buscarPorEstudianteYPeriodo("E001", "2026-I")).thenReturn(Optional.of(existente));
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.matricularEstudiante(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA));
        verify(repoMock, never()).agregar(any());
        verify(publisherMock, never()).notificar(any(TipoEvento.class));
    }
    @Test
    void listarTodas_debeRetornarTodasLasMatriculas() {
        List<Matricula> lista = List.of(
                new Matricula(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA));
        when(repoMock.listarTodos()).thenReturn(lista);
        List<Matricula> resultado = casoUso.listarTodas();
        assertEquals(1, resultado.size());
        verify(repoMock).listarTodos();
    }
    @Test
    void buscarMatricula_debeRetornarMatriculaSiExiste() {
        Matricula esperada = new Matricula(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        when(repoMock.buscarPorEstudianteYPeriodo("E001", "2026-I")).thenReturn(Optional.of(esperada));
        Matricula resultado = casoUso.buscarMatricula("E001", "2026-I");
        assertEquals(esperada, resultado);
    }
    @Test
    void buscarMatricula_debeRetornarNullSiNoExiste() {
        when(repoMock.buscarPorEstudianteYPeriodo("E999", "2026-I")).thenReturn(Optional.empty());
        Matricula resultado = casoUso.buscarMatricula("E999", "2026-I");
        assertNull(resultado);
    }
    @Test
    void listarPorGrupo_debeRetornarListaFiltrada() {
        List<Matricula> filtradas = List.of(
                new Matricula(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA));
        when(repoMock.buscarPorGrupo("1A")).thenReturn(filtradas);
        List<Matricula> resultado = casoUso.listarPorGrupo("1A");
        assertEquals(1, resultado.size());
        verify(repoMock).buscarPorGrupo("1A");
    }
    @Test
    void actualizarEstado_debeModificarEstadoYPersistir() {
        Matricula matricula = new Matricula(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        casoUso.actualizarEstado(matricula, EstadoMatricula.RETIRADA);
        assertEquals(EstadoMatricula.RETIRADA, matricula.getEstado());
        verify(repoMock).actualizar(matricula, matricula);
    }
    @Test
    void matricularEstudiante_notificaMatriculasExactamenteUnaVez() {
        casoUso.matricularEstudiante(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        verify(publisherMock, times(1)).notificar(TipoEvento.MATRICULAS);
        verifyNoMoreInteractions(publisherMock);
    }
    @Test
    void actualizar_notificaMatriculasExactamenteUnaVez() {
        Matricula matricula = new Matricula(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        casoUso.actualizar(matricula);
        verify(publisherMock, times(1)).notificar(TipoEvento.MATRICULAS);
        verifyNoMoreInteractions(publisherMock);
    }
    @Test
    void actualizarEstado_notificaMatriculasExactamenteUnaVez() {
        Matricula matricula = new Matricula(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        casoUso.actualizarEstado(matricula, EstadoMatricula.CULMINADA);
        verify(publisherMock, times(1)).notificar(TipoEvento.MATRICULAS);
        verifyNoMoreInteractions(publisherMock);
    }
    @Test
    void eliminarMatricula_notificaMatriculasExactamenteUnaVez() {
        Matricula matricula = new Matricula(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        casoUso.eliminarMatricula(matricula);
        verify(publisherMock, times(1)).notificar(TipoEvento.MATRICULAS);
        verifyNoMoreInteractions(publisherMock);
    }
}
