/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAulas;
import dominio.modelo.Aula;
import dominio.modelo.ModalidadAula;
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
public class GestionAulasIT extends BaseIntegrationTest {
    @Autowired private GestionAulas gestionAulas;
    @Autowired private EntityManager em;
    @Test
    public void testCrearYListarAula() {
        gestionAulas.crearAula("Aula IT", 30, "Piso 3", "Laboratorio", ModalidadAula.VIRTUAL);
        List<Aula> aulas = gestionAulas.listarTodos();
        assertFalse(aulas.isEmpty());
        Aula aulaCreada = aulas.stream().filter(a -> a.getNombre().equals("Aula IT")).findFirst().orElse(null);
        assertNotNull(aulaCreada);
        assertEquals(30, aulaCreada.getCapacidad());
        assertEquals("Piso 3", aulaCreada.getUbicacion());
        assertEquals(ModalidadAula.VIRTUAL, aulaCreada.getModalidad());
    }
    @Test
    public void testActualizarAula() {
        gestionAulas.crearAula("IT-Aula-UP", 30, "Piso 1", "Teoría", ModalidadAula.PRESENCIAL);
        Aula original = gestionAulas.buscarAula("IT-Aula-UP");
        gestionAulas.actualizarAula(original, "IT-Aula-UP-NEW", 50, "Piso 5", "Laboratorio", ModalidadAula.VIRTUAL);
        em.flush(); em.clear();
        assertNull(gestionAulas.buscarAula("IT-Aula-UP"));
        Aula actualizada = gestionAulas.buscarAula("IT-Aula-UP-NEW");
        assertNotNull(actualizada);
        assertEquals(50, actualizada.getCapacidad());
        assertEquals("Piso 5", actualizada.getUbicacion());
        assertEquals(ModalidadAula.VIRTUAL, actualizada.getModalidad());
    }
    @Test
    public void testEliminarAula() {
        gestionAulas.crearAula("IT-Aula-DEL", 30, "Piso 1", "Teoría", ModalidadAula.PRESENCIAL);
        Aula a = gestionAulas.buscarAula("IT-Aula-DEL");
        gestionAulas.eliminarAula(a);
        em.flush(); em.clear();
        assertNull(gestionAulas.buscarAula("IT-Aula-DEL"));
    }
    @Test
    public void testCrearAulaConCapitalizacionDistintaDebeLanzarIllegalArgumentException() {
        gestionAulas.crearAula("CI-Aula-MiXeD", 30, "Piso", "Tipo", ModalidadAula.PRESENCIAL);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> gestionAulas.crearAula("ci-aula-mixed", 30, "Piso", "Tipo", ModalidadAula.PRESENCIAL));
        assertTrue(ex.getMessage().contains("Ya existe"),
                "Esperaba IllegalArgumentException de negocio, obtuve: " + ex.getMessage());
    }
}
