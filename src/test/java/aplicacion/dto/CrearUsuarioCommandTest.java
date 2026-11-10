/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.dto;
import dominio.modelo.Usuario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
class CrearUsuarioCommandTest {
    private CrearUsuarioCommand commandValido(String dni) {
        return new CrearUsuarioCommand("U001", "hash", dni, "Test", "User", 25, Usuario.Rol.ESTUDIANTE, null);
    }
    @Test
    void dniCortoDebeLanzarExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> commandValido("123"));
        assertTrue(ex.getMessage().contains("DNI"));
    }
    @Test
    void dni8DigitosDebeSerAceptado() {
        CrearUsuarioCommand cmd = commandValido("12345678");
        assertEquals("12345678", cmd.dni());
    }
    @Test
    void dniInternacionalChilenoDebeSerAceptado() {
        CrearUsuarioCommand cmd = commandValido("12345678K");
        assertEquals("12345678K", cmd.dni());
    }
    @Test
    void dniCurpMexicanoDebeSerAceptado() {
        String curp = "HEGG560427MVZRRL04";
        CrearUsuarioCommand cmd = commandValido(curp);
        assertEquals(curp, cmd.dni());
    }
    @Test
    void dni21CaracteresDebeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> commandValido("123456789012345678901"));
    }
}
