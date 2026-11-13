/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.publicaciones;
import dominio.modelo.*;
import dominio.puerto.externo.EventPublisher;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioPublicaciones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class GestionPublicacionesTest {
    private RepositorioPublicaciones repoMock;
    private LoggerPort loggerMock;
    private EventPublisher publisherMock;
    private GestionPublicaciones casoUso;
    private Asignatura asignatura;
    private Usuario autor;
    @BeforeEach
    void setUp() {
        repoMock = mock(RepositorioPublicaciones.class);
        loggerMock = mock(LoggerPort.class);
        publisherMock = mock(EventPublisher.class);
        casoUso = new GestionPublicaciones(repoMock, loggerMock, publisherMock);
        asignatura = new Asignatura("Matemáticas");
        autor = new Docente("D001", "hash", "111", "Juan", "Perez", 30, "Mat", new java.util.ArrayList<>());
    }
    @Test
    void crearPublicacion_debeFallarSiTituloVacio() {
        assertThrows(IllegalArgumentException.class, () -> casoUso.crearPublicacion(
                "", "desc", TipoPublicacion.MATERIAL, 1, null, null, asignatura, autor));
        verify(repoMock, never()).agregar(any());
    }
    @Test
    void crearPublicacion_debeFallarSiTipoOAutorNulo() {
        assertThrows(IllegalArgumentException.class, () -> casoUso.crearPublicacion(
                "Titulo", "desc", null, 1, null, null, asignatura, autor));
        assertThrows(IllegalArgumentException.class, () -> casoUso.crearPublicacion(
                "Titulo", "desc", TipoPublicacion.ANUNCIO, null, null, null, null, null));
        verify(repoMock, never()).agregar(any());
    }
    @Test
    void crearPublicacion_debeFallarSiRequiereAsignaturaYNoLaTiene() {
        assertThrows(IllegalArgumentException.class, () -> casoUso.crearPublicacion(
                "Titulo", "desc", TipoPublicacion.TAREA, 1, LocalDateTime.now(), null, null, autor));
        assertThrows(IllegalArgumentException.class, () -> casoUso.crearPublicacion(
                "Titulo", "desc", TipoPublicacion.EVALUACION, null, LocalDateTime.now(), null, null, autor));
        verify(repoMock, never()).agregar(any());
    }
    @Test
    void crearPublicacion_debeAgregarYNotificar() {
        casoUso.crearPublicacion("Titulo", "desc", TipoPublicacion.MATERIAL, 1, null, "url", asignatura, autor);
        verify(repoMock).agregar(any(Publicacion.class));
        verify(publisherMock).notificar(TipoEvento.PUBLICACIONES);
        verify(loggerMock).audit(contains("op=PUBLISH entity="));
    }
    @Test
    void crearAnuncioGlobal_noDebeRequerirAsignatura() {
        casoUso.crearPublicacion("Anuncio Global", "desc", TipoPublicacion.ANUNCIO, null, null, null, null, autor);
        verify(repoMock).agregar(any(Publicacion.class));
        verify(publisherMock).notificar(TipoEvento.PUBLICACIONES);
    }
    @Test
    void listarTodos_debeRetornarLista() {
        when(repoMock.listarTodos()).thenReturn(List.of(new Publicacion(1L, "T", "D", TipoPublicacion.ANUNCIO, null, null, null, null, autor)));
        assertEquals(1, casoUso.listarTodos().size());
    }
    @Test
    void listarPorAsignatura_debeRetornarLista() {
        casoUso.listarPorAsignatura("Matemáticas");
        verify(repoMock).buscarPorAsignatura("Matemáticas");
    }
    @Test
    void eliminarPublicacion_debeEliminarYNotificar() {
        Publicacion pub = new Publicacion(1L, "T", "D", TipoPublicacion.ANUNCIO, null, null, null, null, autor);
        casoUso.eliminarPublicacion(pub);
        verify(repoMock).eliminar(pub);
        verify(publisherMock).notificar(TipoEvento.PUBLICACIONES);
    }
}
