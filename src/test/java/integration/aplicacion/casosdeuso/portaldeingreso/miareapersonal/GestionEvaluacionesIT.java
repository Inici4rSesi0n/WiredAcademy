/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.aplicacion.casosdeuso.portaldeingreso.miareapersonal;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import aplicacion.casosdeuso.portaldeingreso.miareapersonal.GestionEvaluaciones;
import dominio.modelo.Asignatura;
import dominio.modelo.Evaluacion;
import integration.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionEvaluacionesIT extends BaseIntegrationTest {
    @Autowired private GestionEvaluaciones gestionEvaluaciones;
    @Autowired private GestionAsignaturas gestionAsignaturas;
    @Autowired private EntityManager em;
    @Test
    public void testCrearEvaluacion() {
        gestionAsignaturas.crearAsignatura("EVAL-IT-Asig");
        Asignatura asig = gestionAsignaturas.buscarAsignatura("EVAL-IT-Asig");
        gestionEvaluaciones.crearEvaluacion("EVAL-IT-PC1", 0.3, asig);
        em.flush(); em.clear();
        List<Evaluacion> evs = gestionEvaluaciones.listarPorAsignatura("EVAL-IT-Asig");
        assertEquals(1, evs.size());
        assertEquals("EVAL-IT-PC1", evs.get(0).getNombre());
        assertEquals(0.3, evs.get(0).getPeso());
    }
    @Test
    public void testEliminarEvaluacion() {
        gestionAsignaturas.crearAsignatura("EVAL-IT-Asig2");
        Asignatura asig = gestionAsignaturas.buscarAsignatura("EVAL-IT-Asig2");
        gestionEvaluaciones.crearEvaluacion("EVAL-IT-PC2", 0.5, asig);
        em.flush(); em.clear();
        List<Evaluacion> evs = gestionEvaluaciones.listarPorAsignatura("EVAL-IT-Asig2");
        gestionEvaluaciones.eliminarEvaluacion(evs.get(0));
        em.flush(); em.clear();
        assertTrue(gestionEvaluaciones.listarPorAsignatura("EVAL-IT-Asig2").isEmpty());
    }
}
