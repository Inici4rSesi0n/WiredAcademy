/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionSecretarios;
import dominio.modelo.Secretario;
import integration.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionSecretariosIT extends BaseIntegrationTest {
    @Autowired private GestionSecretarios gestionSecretarios;
    @Autowired private EntityManager em;
    @Test
    public void testActualizarSecretario() {
        gestionSecretarios.agregar(new Secretario("IT-SEC-UP", "hashIT", "85000001", "Sec", "UP", 35));
        Secretario original = gestionSecretarios.buscarPorCodigo("IT-SEC-UP");
        Secretario actualizado = new Secretario("IT-SEC-UP", "hashIT2", "85000001", "Sec", "UPD", 40);
        gestionSecretarios.actualizar(original, actualizado);
        em.flush(); em.clear();
        Secretario recargado = gestionSecretarios.buscarPorCodigo("IT-SEC-UP");
        assertEquals("UPD", recargado.getApellido());
        assertEquals(40, recargado.getEdad());
    }
    @Test
    public void testEliminarSecretario() {
        gestionSecretarios.agregar(new Secretario("IT-SEC-DEL", "hashIT", "85000002", "A", "B", 35));
        Secretario s = gestionSecretarios.buscarPorCodigo("IT-SEC-DEL");
        gestionSecretarios.eliminar(s);
        em.flush(); em.clear();
        assertNull(gestionSecretarios.buscarPorCodigo("IT-SEC-DEL"));
    }
}
