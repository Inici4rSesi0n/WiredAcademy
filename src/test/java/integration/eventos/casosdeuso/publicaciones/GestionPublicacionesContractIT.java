/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.eventos.casosdeuso.publicaciones;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import aplicacion.casosdeuso.portaldeingreso.publicaciones.GestionPublicaciones;
import dominio.modelo.*;
import integration.eventos.BaseContractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
class GestionPublicacionesContractIT extends BaseContractTest {
    @Autowired private GestionPublicaciones gestionPublicaciones;
    @Autowired private GestionAsignaturas gestionAsignaturas;
    @Autowired private GestionDocentes gestionDocentes;
    private Asignatura asignatura;
    private Docente autor;
    @BeforeEach
    void setUpDatos() {
        if (gestionAsignaturas.buscarAsignatura("CONTR-PUB-ASIG") == null) {
            gestionAsignaturas.crearAsignatura("CONTR-PUB-ASIG");
        }
        asignatura = gestionAsignaturas.buscarAsignatura("CONTR-PUB-ASIG");
        assertNotNull(asignatura, "La asignatura CONTR-PUB-ASIG no fue creada");
        if (gestionDocentes.buscarPorCodigo("CONTR-PUB-DOC") == null) {
            gestionDocentes.agregar(new Docente("CONTR-PUB-DOC", "hash", "777222", "Contr", "IT", 30, "Mat", new java.util.ArrayList<>()));
        }
        autor = gestionDocentes.buscarPorCodigo("CONTR-PUB-DOC");
        assertNotNull(autor, "El docente CONTR-PUB-DOC no fue creado");
        recorder.reset();
    }
    @Test
    void crearPublicacion_emitePUBLICACIONES() {
        gestionPublicaciones.crearPublicacion("Contr Test", "desc", TipoPublicacion.MATERIAL, 1, null, null, asignatura, autor);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.PUBLICACIONES));
        assertTrue(recorder.contiene(TipoEvento.PUBLICACIONES));
    }
    @Test
    void eliminarPublicacion_emitePUBLICACIONES() {
        gestionPublicaciones.crearPublicacion("Contr Test 2", "desc", TipoPublicacion.ANUNCIO, null, null, null, null, autor);
        Publicacion pub = gestionPublicaciones.listarGlobales().stream().filter(p -> p.getTitulo().equals("Contr Test 2")).findFirst().orElseThrow();
        recorder.reset();
        gestionPublicaciones.eliminarPublicacion(pub);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.PUBLICACIONES));
        assertTrue(recorder.contiene(TipoEvento.PUBLICACIONES));
    }
}
