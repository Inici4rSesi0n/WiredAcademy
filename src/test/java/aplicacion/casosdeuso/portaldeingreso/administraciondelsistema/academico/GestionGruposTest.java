package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;

import dominio.modelo.Docente;
import dominio.modelo.Estudiante;
import dominio.modelo.Grado;
import dominio.modelo.Grupo;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioDocentes;
import dominio.puerto.repositorio.RepositorioGrupos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author inici4rsesi0n
 */
class GestionGruposTest {

    private RepositorioGrupos repoMock;
    private RepositorioDocentes repoDocentesMock;
    private LoggerPort loggerMock;
    private dominio.puerto.externo.EventPublisher publisherMock;
    private GestionGrupos casoUso;
    private Grado grado;

    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioGrupos.class);
        repoDocentesMock = mock(RepositorioDocentes.class);
        loggerMock = mock(LoggerPort.class);
        publisherMock = mock(dominio.puerto.externo.EventPublisher.class);
        casoUso = new GestionGrupos(repoMock, repoDocentesMock, loggerMock, publisherMock);
        grado = new Grado("1er Grado", "Primaria");
    }


    @Test
    void crearGrupo_debeLanzarExcepcionCuandoYaExiste() {
        when(repoMock.buscarPorNombre("1A")).thenReturn(Optional.of(new Grupo("1A", grado)));
        assertThrows(IllegalArgumentException.class, () -> casoUso.crearGrupo("1A", grado));
        verify(repoMock, never()).agregar(any());
    }

    @Test
    void buscarPorNombre_debeRetornarGrupoSiExiste() {
        Grupo esperado = new Grupo("1A", grado);
        when(repoMock.buscarPorNombre("1A")).thenReturn(Optional.of(esperado));
        assertEquals(esperado, casoUso.buscarPorNombre("1A"));
    }

    @Test
    void buscarPorNombre_debeRetornarNullSiNoExiste() {
        when(repoMock.buscarPorNombre("Inexistente")).thenReturn(Optional.empty());
        assertNull(casoUso.buscarPorNombre("Inexistente"));
    }

    @Test
    void listarTodos_debeRetornarListaCompleta() {
        List<Grupo> lista = List.of(new Grupo("1A", grado), new Grupo("1B", grado));
        when(repoMock.listarTodos()).thenReturn(lista);
        assertEquals(2, casoUso.listarTodos().size());
    }

    @Test
    void buscarPorNombreYGrado_debeRetornarFiltrados() {
        List<Grupo> filtrados = List.of(new Grupo("1A", grado));
        when(repoMock.buscarPorNombreYGrado("1A", "1er Grado")).thenReturn(filtrados);
        assertEquals(1, casoUso.buscarPorNombreYGrado("1A", "1er Grado").size());
    }

    @Test
    void actualizarGrupo_debeActualizarConNuevosDatos() {
        Grupo original = new Grupo("Viejo", grado);
        Grado nuevoGrado = new Grado("2do Grado", "Primaria");
        casoUso.actualizarGrupo(original, "Nuevo", nuevoGrado);
        verify(repoMock).actualizar(eq(original), any(Grupo.class));
    }

    @Test
    void actualizarGrupo_debePreservarTutorYEstudiantesAlPersistir() {
        Grupo original = new Grupo("1A", grado);
        Docente tutor = new Docente("D001", "hash", "111", "Juan", "Perez", 30, "Mat", new ArrayList<>());
        Estudiante estudiante = new Estudiante("E001", "hash", "222", "Ana", "Gomez", 10);
        original.asignarTutor(tutor);
        original.agregarEstudiante(estudiante);

        Grado nuevoGrado = new Grado("2do Grado", "Primaria");
        casoUso.actualizarGrupo(original, "1B", nuevoGrado);

        ArgumentCaptor<Grupo> captor = ArgumentCaptor.forClass(Grupo.class);
        verify(repoMock).actualizar(eq(original), captor.capture());
        Grupo persistido = captor.getValue();

        assertEquals("1B", persistido.getNombre());
        assertEquals(nuevoGrado, persistido.getGrado());
        assertEquals(tutor, persistido.getTutor(), "El tutor debe preservarse al actualizar");
        assertTrue(persistido.getEstudiantes().contains(estudiante),
                "Los estudiantes deben preservarse al actualizar");
    }


    @Test
    void asignarTutor_debeAsignarDocenteYActualizarAmbos() {
        Grupo grupo = new Grupo("1A", grado);
        Docente tutor = new Docente("D001", "hash", "111", "Juan", "Perez", 30, "Mat", new ArrayList<>());

        casoUso.asignarTutor(grupo, tutor);

        assertEquals(tutor, grupo.getTutor());
        assertEquals(grupo, tutor.getTutoria());
        verify(repoMock).actualizar(grupo, grupo);
        verify(repoDocentesMock).actualizar(tutor, tutor);
    }

    @Test
    void asignarTutor_debePersistirTutorAnteriorAlSerReemplazado() {
        Grupo grupo = new Grupo("1A", grado);
        Docente tutorAnterior = new Docente("D001", "hash", "111", "Juan", "Perez", 30, "Mat", new ArrayList<>());
        Docente tutorNuevo = new Docente("D002", "hash", "222", "Maria", "Lopez", 35, "Ing", new ArrayList<>());
        grupo.asignarTutor(tutorAnterior);

        casoUso.asignarTutor(grupo, tutorNuevo);

        assertNull(tutorAnterior.getTutoria(), "El tutor anterior debe quedar sin tutoría");
        assertEquals(grupo, tutorNuevo.getTutoria());
        verify(repoDocentesMock).actualizar(tutorAnterior, tutorAnterior);
        verify(repoDocentesMock).actualizar(tutorNuevo, tutorNuevo);
        verify(repoMock).actualizar(grupo, grupo);
    }

    @Test
    void removerTutor_debeQuitarDocenteYActualizarAmbos() {
        Grupo grupo = new Grupo("1A", grado);
        Docente tutor = new Docente("D001", "hash", "111", "Juan", "Perez", 30, "Mat", new ArrayList<>());
        grupo.asignarTutor(tutor);

        casoUso.removerTutor(grupo);

        assertNull(grupo.getTutor());
        assertNull(tutor.getTutoria());
        verify(repoMock).actualizar(grupo, grupo);
        verify(repoDocentesMock).actualizar(tutor, tutor);
    }

    @Test
    void removerTutor_noDebeHacerNadaSiNoTieneTutor() {
        Grupo grupo = new Grupo("1A", grado);
        casoUso.removerTutor(grupo);
        verify(repoMock, never()).actualizar(any(), any());
        verify(repoDocentesMock, never()).actualizar(any(), any());
    }
}
