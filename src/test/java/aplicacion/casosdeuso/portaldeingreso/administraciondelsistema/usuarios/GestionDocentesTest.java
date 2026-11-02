package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;

import dominio.modelo.Asignatura;
import dominio.modelo.Docente;
import dominio.modelo.Grado;
import dominio.modelo.Grupo;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioDocentes;
import dominio.puerto.repositorio.RepositorioGrupos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.lang.reflect.Method;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author inici4rsesi0n
 */
class GestionDocentesTest {

    private RepositorioDocentes repoDocentes;
    private RepositorioGrupos repoGrupos;
    private LoggerPort loggerMock;
    private dominio.puerto.externo.EventPublisher publisherMock;
    private GestionDocentes casoUso;

    @BeforeEach
    void setUp() {
        repoDocentes = mock(RepositorioDocentes.class);
        repoGrupos = mock(RepositorioGrupos.class);
        loggerMock = mock(LoggerPort.class);
        publisherMock = mock(dominio.puerto.externo.EventPublisher.class);
        casoUso = new GestionDocentes(repoDocentes, repoGrupos, loggerMock, publisherMock);
    }


    @Test
    void agregar_debeLanzarExcepcionSiCodigoDuplicado() {
        Docente existente = new Docente("D001", "hash", "111", "Juan", "Perez", 30, "Mat", new ArrayList<>());
        Docente nuevo = new Docente("D001", "hash2", "222", "Ana", "Lopez", 28, "Ing", new ArrayList<>());
        when(repoDocentes.buscarPorCodigo("D001")).thenReturn(Optional.of(existente));

        assertThrows(IllegalArgumentException.class, () -> casoUso.agregar(nuevo));
        verify(repoDocentes, never()).agregar(any());
    }

    @Test
    void buscarPorCodigo_debeRetornarDocenteSiExiste() {
        Docente esperado = new Docente("D001", "hash", "111", "Juan", "Perez", 30, "Mat", new ArrayList<>());
        when(repoDocentes.buscarPorCodigo("D001")).thenReturn(Optional.of(esperado));

        assertEquals(esperado, casoUso.buscarPorCodigo("D001"));
    }

    @Test
    void buscarPorDni_debeRetornarDocenteSiExiste() {
        Docente esperado = new Docente("D001", "hash", "111", "Juan", "Perez", 30, "Mat", new ArrayList<>());
        when(repoDocentes.buscarPorDni("111")).thenReturn(Optional.of(esperado));

        assertEquals(esperado, casoUso.buscarPorDni("111"));
    }

    @Test
    void agregarAsignatura_debeAgregarlaYActualizar() {
        Docente docente = new Docente("D001", "hash", "111", "Juan", "Perez", 30, "Mat", new ArrayList<>());
        Asignatura asig = new Asignatura("Física");

        casoUso.agregarAsignatura(docente, asig);

        assertTrue(docente.getAsignaturas().contains(asig));
        verify(repoDocentes).actualizar(docente, docente);
    }

    @Test
    void removerAsignatura_debeRemoverlaYActualizar() {
        List<Asignatura> lista = new ArrayList<>();
        Asignatura asig = new Asignatura("Química");
        lista.add(asig);
        Docente docente = new Docente("D001", "hash", "111", "Juan", "Perez", 30, "Mat", lista);

        casoUso.removerAsignatura(docente, asig);

        assertFalse(docente.getAsignaturas().contains(asig));
        verify(repoDocentes).actualizar(docente, docente);
    }

    @Test
    void asignarTutoria_debeAsignarTutorYPersistirAmbos() {
        Docente docente = new Docente("D001", "hash", "111", "Juan", "Perez", 30, "Mat", new ArrayList<>());
        Grado grado = new Grado("1er Grado", "Primaria");
        Grupo grupo = new Grupo("1A", grado);

        casoUso.asignarTutoria(docente, grupo);

        assertEquals(docente, grupo.getTutor());
        assertEquals(grupo, docente.getTutoria());
        verify(repoGrupos).actualizar(grupo, grupo);
        verify(repoDocentes).actualizar(docente, docente);
    }

    @Test
    void asignarTutoria_debeSerTransactional() throws NoSuchMethodException {
        Method m = GestionDocentes.class.getMethod("asignarTutoria", dominio.modelo.Docente.class, dominio.modelo.Grupo.class);
        assertTrue(m.isAnnotationPresent(Transactional.class),
                "asignarTutoria debe ser @Transactional para garantizar atomicidad (B3)");
    }
}
