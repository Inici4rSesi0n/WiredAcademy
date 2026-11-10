/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.aplicacion.casosdeuso.portaldeingreso.sistema;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionUsuarios;
import aplicacion.casosdeuso.portaldeingreso.sistema.AutenticarUsuario;
import aplicacion.dto.CrearUsuarioCommand;
import dominio.modelo.Usuario;
import dominio.puerto.externo.HashProvider;
import integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class AutenticarUsuarioIT extends BaseIntegrationTest {
    @Autowired private AutenticarUsuario autenticarUsuario;
    @Autowired private GestionUsuarios gestionUsuarios;
    @Autowired private HashProvider hashProvider;
    @Test
    public void testLoginConCredencialesCorrectas() {
        String password = "secret123";
        String hash = hashProvider.generarHash(password.toCharArray());
        gestionUsuarios.agregarUsuario(new CrearUsuarioCommand("IT-AUTH-001", hash, "70000001", "Auth", "User", 30, Usuario.Rol.ADMINISTRADOR, null));
        Usuario usuario = autenticarUsuario.ejecutar("IT-AUTH-001", password.toCharArray());
        assertNotNull(usuario);
        assertEquals("IT-AUTH-001", usuario.getCodigo());
    }
    @Test
    public void testLoginConContrasenaIncorrectaRetornaNull() {
        String hash = hashProvider.generarHash("correcta".toCharArray());
        gestionUsuarios.agregarUsuario(new CrearUsuarioCommand("IT-AUTH-002", hash, "70000002", "Auth", "User", 30, Usuario.Rol.ADMINISTRADOR, null));
        Usuario usuario = autenticarUsuario.ejecutar("IT-AUTH-002", "incorrecta".toCharArray());
        assertNull(usuario);
    }
}
