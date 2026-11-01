package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;

import dominio.modelo.*;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioClases;
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
class GestionClasesTest {

    private RepositorioClases repoMock;
    private LoggerPort loggerMock;
    private dominio.puerto.externo.EventPublisher publisherMock;
    private GestionClases casoUso;

    private Asignatura asignatura;
    private Docente docente;
    private Grupo grupo;
    private Aula aula;
    private PeriodoAcademico periodo;

    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioClases.class);
        loggerMock = mock(LoggerPort.class);
        publisherMock = mock(dominio.puerto.externo.EventPublisher.class);
        casoUso = new GestionClases(repoMock, loggerMock, publisherMock);

        asignatura = new Asignatura("Matemáticas");
        docente = new Docente("D001", "hash", "123", "Juan", "Perez", 30, "Mat", new ArrayList<>());
        Grado grado = new Grado("1er Grado", "Primaria");
        grupo = new Grupo("1A", grado);
        aula = new Aula("Aula 101", 40, "Pabellón 1", "Teoría");
        periodo = new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", "Activo");
    }


    @Test
    void crearClase_debeLanzarExcepcionSiDiaVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearClase("", "08:00", "10:00", asignatura, docente, grupo, aula, periodo));
    }

    @Test
    void crearClase_debeLanzarExcepcionSiHoraInicioVacia() {
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearClase("Lunes", "", "10:00", asignatura, docente, grupo, aula, periodo));
    }

    @Test
    void crearClase_debeLanzarExcepcionSiHoraFinVacia() {
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearClase("Lunes", "08:00", "", asignatura, docente, grupo, aula, periodo));
    }

    @Test
    void crearClase_debeLanzarExcepcionSiAsignaturaNula() {
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearClase("Lunes", "08:00", "10:00", null, docente, grupo, aula, periodo));
    }

    @Test
    void crearClase_debeLanzarExcepcionSiDocenteNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearClase("Lunes", "08:00", "10:00", asignatura, null, grupo, aula, periodo));
    }

    @Test
    void crearClase_debeLanzarExcepcionSiGrupoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearClase("Lunes", "08:00", "10:00", asignatura, docente, null, aula, periodo));
    }

    @Test
    void crearClase_debeLanzarExcepcionSiAulaNula() {
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearClase("Lunes", "08:00", "10:00", asignatura, docente, grupo, null, periodo));
    }

    @Test
    void crearClase_debeLanzarExcepcionSiPeriodoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearClase("Lunes", "08:00", "10:00", asignatura, docente, grupo, aula, null));
    }

    @Test
    void crearClase_debeFallarSiDocenteTieneConflicto() {
        when(repoMock.existeConflictoDocente(eq("D001"), eq("Lunes"), eq("08:00"), eq("10:00"), isNull()))
                .thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearClase("Lunes", "08:00", "10:00", asignatura, docente, grupo, aula, periodo));

        assertTrue(ex.getMessage().contains("docente"),
                "El mensaje debe mencionar al docente. Fue: " + ex.getMessage());
        verify(repoMock, never()).agregar(any());
    }

    @Test
    void crearClase_debeFallarSiAulaTieneConflicto() {
        when(repoMock.existeConflictoAula(eq("Aula 101"), eq("Lunes"), eq("08:00"), eq("10:00"), isNull()))
                .thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearClase("Lunes", "08:00", "10:00", asignatura, docente, grupo, aula, periodo));

        assertTrue(ex.getMessage().contains("aula"),
                "El mensaje debe mencionar el aula. Fue: " + ex.getMessage());
        verify(repoMock, never()).agregar(any());
    }

    @Test
    void crearClase_debeFallarSiGrupoTieneConflicto() {
        when(repoMock.existeConflictoGrupo(eq("1A"), eq("1er Grado"), eq("Lunes"), eq("08:00"), eq("10:00"), isNull()))
                .thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> casoUso.crearClase("Lunes", "08:00", "10:00", asignatura, docente, grupo, aula, periodo));

        assertTrue(ex.getMessage().contains("grupo"),
                "El mensaje debe mencionar al grupo. Fue: " + ex.getMessage());
        verify(repoMock, never()).agregar(any());
    }

    @Test
    void listarClases_debeRetornarListaCompleta() {
        FranjaHoraria franja = new FranjaHoraria("Lunes", "08:00", "10:00");
        Clase clase = new Clase(franja, periodo, asignatura, docente, grupo, aula);
        when(repoMock.listarTodos()).thenReturn(List.of(clase));

        List<Clase> resultado = casoUso.listarClases();

        assertEquals(1, resultado.size());
        verify(repoMock).listarTodos();
    }

    @Test
    void buscarPorGrupoYGrado_debeRetornarListaFiltrada() {
        FranjaHoraria franja = new FranjaHoraria("Lunes", "08:00", "10:00");
        Clase clase = new Clase(franja, periodo, asignatura, docente, grupo, aula);
        when(repoMock.buscarPorGrupoYGrado("1A", "1er Grado")).thenReturn(List.of(clase));

        List<Clase> resultado = casoUso.buscarPorGrupoYGrado("1A", "1er Grado");

        assertEquals(1, resultado.size());
        verify(repoMock).buscarPorGrupoYGrado("1A", "1er Grado");
    }

    @Test
    void buscarClase_debeRetornarClaseSiExiste() {
        FranjaHoraria franja = new FranjaHoraria("Lunes", "08:00", "10:00");
        Clase esperada = new Clase(franja, periodo, asignatura, docente, grupo, aula);
        when(repoMock.buscarClase("1er Grado", "1A", "Lunes", "08:00", "10:00"))
                .thenReturn(Optional.of(esperada));

        Clase resultado = casoUso.buscarClase("1er Grado", "1A", "Lunes", "08:00", "10:00");

        assertEquals(esperada, resultado);
    }

    @Test
    void buscarClase_debeRetornarNullSiNoExiste() {
        when(repoMock.buscarClase("1er Grado", "1A", "Lunes", "08:00", "10:00"))
                .thenReturn(Optional.empty());

        Clase resultado = casoUso.buscarClase("1er Grado", "1A", "Lunes", "08:00", "10:00");

        assertNull(resultado);
    }

    @Test
    void actualizarClase_debeActualizarCorrectamente() {
        FranjaHoraria franjaOriginal = new FranjaHoraria("Lunes", "08:00", "10:00");
        Clase original = new Clase(franjaOriginal, periodo, asignatura, docente, grupo, aula);

        casoUso.actualizarClase(original, "Martes", "09:00", "11:00", asignatura, docente, grupo, aula, periodo);

        verify(repoMock).actualizar(eq(original), any(Clase.class));
    }

    @Test
    void actualizarClase_debeExcluirLaPropiaClaseDeLaValidacion() {
        FranjaHoraria franjaOriginal = new FranjaHoraria("Lunes", "08:00", "10:00");
        Clase original = new Clase(franjaOriginal, periodo, asignatura, docente, grupo, aula);

        casoUso.actualizarClase(original, "Martes", "09:00", "11:00", asignatura, docente, grupo, aula, periodo);

        verify(repoMock).existeConflictoDocente(eq("D001"), eq("Martes"), eq("09:00"), eq("11:00"), eq(original));
        verify(repoMock).existeConflictoAula(eq("Aula 101"), eq("Martes"), eq("09:00"), eq("11:00"), eq(original));
        verify(repoMock).existeConflictoGrupo(eq("1A"), eq("1er Grado"), eq("Martes"), eq("09:00"), eq("11:00"), eq(original));
    }

    @Test
    void actualizarClase_debeFallarSiHayConflictoConOtraClase() {
        FranjaHoraria franjaOriginal = new FranjaHoraria("Lunes", "08:00", "10:00");
        Clase original = new Clase(franjaOriginal, periodo, asignatura, docente, grupo, aula);

        when(repoMock.existeConflictoDocente(eq("D001"), eq("Martes"), eq("09:00"), eq("11:00"), eq(original)))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> casoUso.actualizarClase(original, "Martes", "09:00", "11:00", asignatura, docente, grupo, aula, periodo));

        verify(repoMock, never()).actualizar(any(), any());
    }

}
