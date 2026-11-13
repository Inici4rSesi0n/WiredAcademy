/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.aplicacion.casosdeuso.portaldeingreso.publicaciones;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import aplicacion.casosdeuso.portaldeingreso.publicaciones.GestionPublicaciones;
import dominio.modelo.*;
import integration.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
public class GestionPublicacionesIT extends BaseIntegrationTest {
    @Autowired private GestionPublicaciones gestionPublicaciones;
    @Autowired private GestionAsignaturas gestionAsignaturas;
    @Autowired private GestionDocentes gestionDocentes;
    @Autowired private EntityManager em;
    private Asignatura crearAsignatura(String nombre) {
        if (gestionAsignaturas.buscarAsignatura(nombre) == null) gestionAsignaturas.crearAsignatura(nombre);
        return gestionAsignaturas.buscarAsignatura(nombre);
    }
    private Docente crearDocente(String codigo, String dni) {
        if (gestionDocentes.buscarPorCodigo(codigo) == null) {
            gestionDocentes.agregar(new Docente(codigo, "hash", dni, "Doc", "Pub", 30, "Mat", new java.util.ArrayList<>()));
        }
        Docente doc = gestionDocentes.buscarPorCodigo(codigo);
        assertNotNull(doc, "No se pudo crear el docente " + codigo);
        return doc;
    }
    @Test
    void testCrearPublicacionDeMaterial() {
        Asignatura asig = crearAsignatura("PUB-IT-ASIG");
        Docente autor = crearDocente("PUB-IT-DOC", "777111");
        gestionPublicaciones.crearPublicacion("Clase 1", "Material PDF", TipoPublicacion.MATERIAL, 1, null, "/url/file.pdf", asig, autor);
        em.flush(); em.clear();
        List<Publicacion> pubs = gestionPublicaciones.listarPorAsignatura("PUB-IT-ASIG");
        assertFalse(pubs.isEmpty(), "No se encontró ninguna publicación");
        assertEquals("Clase 1", pubs.get(0).getTitulo());
        assertEquals(TipoPublicacion.MATERIAL, pubs.get(0).getTipo());
        assertEquals(1, pubs.get(0).getNumeroSemana());
    }
    @Test
    void testCrearAnuncioGlobal() {
        Docente autor = crearDocente("PUB-IT-DOC", "777111");
        gestionPublicaciones.crearPublicacion("Suspension de clases", "Por huelga", TipoPublicacion.ANUNCIO, null, null, null, null, autor);
        em.flush(); em.clear();
        List<Publicacion> pubs = gestionPublicaciones.listarGlobales();
        assertTrue(pubs.stream().anyMatch(p -> p.getTitulo().equals("Suspension de clases")));
    }
    @Test
    void testEliminarPublicacion() {
        Asignatura asig = crearAsignatura("PUB-IT-ASIG");
        Docente autor = crearDocente("PUB-IT-DOC", "777111");
        gestionPublicaciones.crearPublicacion("A eliminar", "", TipoPublicacion.FORO, null, null, null, asig, autor);
        em.flush(); em.clear();
        List<Publicacion> pubs = gestionPublicaciones.listarPorAsignatura("PUB-IT-ASIG");
        assertFalse(pubs.isEmpty(), "No se encontró la publicación a eliminar");
        gestionPublicaciones.eliminarPublicacion(pubs.get(0));
        em.flush(); em.clear();
        assertTrue(gestionPublicaciones.listarPorAsignatura("PUB-IT-ASIG").isEmpty());
    }
}
