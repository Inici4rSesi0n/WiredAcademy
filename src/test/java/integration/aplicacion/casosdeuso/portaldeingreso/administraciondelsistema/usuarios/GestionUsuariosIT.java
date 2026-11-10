/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionUsuarios;
import aplicacion.dto.CrearUsuarioCommand;
import dominio.modelo.Usuario;
import integration.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionUsuariosIT extends BaseIntegrationTest {
    @Autowired private GestionUsuarios gestionUsuarios;
    @Autowired private EntityManager em;
    @Test
    public void testCrearUsuarioCorrectamente() {
        CrearUsuarioCommand cmd = new CrearUsuarioCommand("IT-USR-001", "hashTest", "80000001", "Test", "User", 30, Usuario.Rol.ADMINISTRADOR, null);
        gestionUsuarios.agregarUsuario(cmd);
        Usuario usuario = gestionUsuarios.buscarPorCodigo("IT-USR-001");
        assertNotNull(usuario);
        assertEquals("Test", usuario.getNombre());
        assertEquals(Usuario.Rol.ADMINISTRADOR, usuario.getRol());
    }
    @Test
    public void testCrearUsuarioConDniDuplicadoFalla() {
        gestionUsuarios.agregarUsuario(new CrearUsuarioCommand("IT-USR-002", "hashTest", "80000002", "A", "B", 30, Usuario.Rol.ADMINISTRADOR, null));
        CrearUsuarioCommand duplicado = new CrearUsuarioCommand("IT-USR-003", "hashTest", "80000002", "C", "D", 25, Usuario.Rol.DIRECTOR, null);
        assertThrows(IllegalArgumentException.class, () -> gestionUsuarios.agregarUsuario(duplicado));
    }
    @Test
    public void testActualizarUsuario() {
        gestionUsuarios.agregarUsuario(new CrearUsuarioCommand("IT-USR-UP", "hashTest", "80000010", "Test", "Old", 30, Usuario.Rol.ADMINISTRADOR, null));
        Usuario original = gestionUsuarios.buscarPorCodigo("IT-USR-UP");
        Usuario actualizado = new dominio.modelo.Administrador("IT-USR-UP", "hashTest", "80000010", "Test", "New", 35);
        gestionUsuarios.actualizarUsuario(actualizado);
        em.flush(); em.clear();
        Usuario recargado = gestionUsuarios.buscarPorCodigo("IT-USR-UP");
        assertEquals("New", recargado.getApellido());
        assertEquals(35, recargado.getEdad());
    }
    @Test
    public void testEliminarUsuario() {
        gestionUsuarios.agregarUsuario(new CrearUsuarioCommand("IT-USR-DEL", "hashTest", "80000011", "A", "B", 30, Usuario.Rol.ADMINISTRADOR, null));
        Usuario u = gestionUsuarios.buscarPorCodigo("IT-USR-DEL");
        gestionUsuarios.eliminarUsuario(u);
        em.flush(); em.clear();
        assertNull(gestionUsuarios.buscarPorCodigo("IT-USR-DEL"));
    }
}
