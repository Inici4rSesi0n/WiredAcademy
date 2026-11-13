/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.aplicacion.casosdeuso.portaldeingreso.miareapersonal;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionEstudiantes;
import aplicacion.casosdeuso.portaldeingreso.miareapersonal.GestionCalificaciones;
import aplicacion.casosdeuso.portaldeingreso.miareapersonal.GestionEvaluaciones;
import dominio.modelo.*;
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
public class GestionCalificacionesIT extends BaseIntegrationTest {
    @Autowired private GestionCalificaciones gestionCalificaciones;
    @Autowired private GestionEvaluaciones gestionEvaluaciones;
    @Autowired private GestionAsignaturas gestionAsignaturas;
    @Autowired private GestionEstudiantes gestionEstudiantes;
    @Autowired private EntityManager em;
    @Test
    public void testRegistrarNota() {
        gestionAsignaturas.crearAsignatura("CAL-IT-Asig");
        Asignatura asig = gestionAsignaturas.buscarAsignatura("CAL-IT-Asig");
        gestionEvaluaciones.crearEvaluacion("CAL-IT-PC1", 0.2, asig);
        Evaluacion ev = gestionEvaluaciones.listarPorAsignatura("CAL-IT-Asig").get(0);
        gestionEstudiantes.agregar(new Estudiante("CAL-IT-EST", "hash", "999111", "Test", "Cal", 15));
        Estudiante est = gestionEstudiantes.buscarPorCodigo("CAL-IT-EST");
        gestionCalificaciones.registrarNota(ev, est, 18.5);
        em.flush(); em.clear();
        List<Calificacion> cals = gestionCalificaciones.listarPorEstudiante("CAL-IT-EST");
        assertEquals(1, cals.size());
        assertEquals(18.5, cals.get(0).getNota());
    }
    @Test
    public void testActualizarNotaExistente() {
        gestionAsignaturas.crearAsignatura("CAL-IT-Asig2");
        Asignatura asig = gestionAsignaturas.buscarAsignatura("CAL-IT-Asig2");
        gestionEvaluaciones.crearEvaluacion("CAL-IT-PC2", 0.3, asig);
        Evaluacion ev = gestionEvaluaciones.listarPorAsignatura("CAL-IT-Asig2").get(0);
        gestionEstudiantes.agregar(new Estudiante("CAL-IT-EST2", "hash", "999222", "Test", "Cal", 15));
        Estudiante est = gestionEstudiantes.buscarPorCodigo("CAL-IT-EST2");
        gestionCalificaciones.registrarNota(ev, est, 10.0);
        em.flush(); em.clear();
        gestionCalificaciones.registrarNota(ev, est, 15.0);
        em.flush(); em.clear();
        List<Calificacion> cals = gestionCalificaciones.listarPorEstudiante("CAL-IT-EST2");
        assertEquals(1, cals.size());
        assertEquals(15.0, cals.get(0).getNota());
    }
}
