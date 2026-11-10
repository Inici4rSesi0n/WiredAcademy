/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import dominio.modelo.Grado;
import integration.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionGradosIT extends BaseIntegrationTest {
    @Autowired private GestionGrados gestionGrados;
    @Autowired private EntityManager em;
    @Test
    public void testActualizarGrado() {
        gestionGrados.crearGrado("IT-Grado-UP", "Primaria");
        Grado original = gestionGrados.buscarPorNombreYNivel("IT-Grado-UP", "Primaria");
        gestionGrados.actualizarGrado(original, "IT-Grado-UP-NEW", "Secundaria");
        em.flush(); em.clear();
        assertNull(gestionGrados.buscarPorNombreYNivel("IT-Grado-UP", "Primaria"));
        Grado actualizado = gestionGrados.buscarPorNombreYNivel("IT-Grado-UP-NEW", "Secundaria");
        assertNotNull(actualizado);
        assertEquals("Secundaria", actualizado.getNivel());
    }
    @Test
    public void testEliminarGrado() {
        gestionGrados.crearGrado("IT-Grado-DEL", "Primaria");
        Grado g = gestionGrados.buscarPorNombreYNivel("IT-Grado-DEL", "Primaria");
        gestionGrados.eliminarGrado(g);
        em.flush(); em.clear();
        assertNull(gestionGrados.buscarPorNombreYNivel("IT-Grado-DEL", "Primaria"));
    }
    @Test
    public void testCrearGradoConCapitalizacionDistintaDebeLanzarIllegalArgumentException() {
        gestionGrados.crearGrado("CI-Grado-MiXeD", "Primaria");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> gestionGrados.crearGrado("ci-grado-mixed", "primaria"));
        assertTrue(ex.getMessage().contains("Ya existe"),
                "Esperaba IllegalArgumentException de negocio, obtuve: " + ex.getMessage());
    }
}
