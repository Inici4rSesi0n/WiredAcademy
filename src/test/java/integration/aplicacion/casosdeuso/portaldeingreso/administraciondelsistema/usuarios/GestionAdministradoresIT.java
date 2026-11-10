/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionAdministradores;
import dominio.modelo.Administrador;
import integration.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionAdministradoresIT extends BaseIntegrationTest {
    @Autowired private GestionAdministradores gestionAdministradores;
    @Autowired private EntityManager em;
    @Test
    public void testActualizarAdministrador() {
        gestionAdministradores.agregar(new Administrador("IT-ADM-UP", "hashIT", "81000001", "Adm", "UP", 30));
        Administrador original = gestionAdministradores.buscarPorCodigo("IT-ADM-UP");
        Administrador actualizado = new Administrador("IT-ADM-UP", "hashIT2", "81000001", "Adm", "UPD", 35);
        gestionAdministradores.actualizar(original, actualizado);
        em.flush(); em.clear();
        Administrador recargado = gestionAdministradores.buscarPorCodigo("IT-ADM-UP");
        assertEquals("UPD", recargado.getApellido());
        assertEquals(35, recargado.getEdad());
    }
    @Test
    public void testEliminarAdministrador() {
        gestionAdministradores.agregar(new Administrador("IT-ADM-DEL", "hashIT", "81000002", "A", "B", 30));
        Administrador a = gestionAdministradores.buscarPorCodigo("IT-ADM-DEL");
        gestionAdministradores.eliminar(a);
        em.flush(); em.clear();
        assertNull(gestionAdministradores.buscarPorCodigo("IT-ADM-DEL"));
    }
}
