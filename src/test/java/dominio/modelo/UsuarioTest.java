/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author inici4rsesi0n
 */
class UsuarioTest {
    private static class UsuarioConcreto extends Usuario {
        public UsuarioConcreto(String codigo, String hashContrasena, String dni,
                               String nombre, String apellido, int edad, Rol rol) {
            super(codigo, hashContrasena, dni, nombre, apellido, edad, rol);
        }
    }

    @Test
    void constructor_debeCrearUsuarioConDatosCorrectos() {
        Usuario usuario = new UsuarioConcreto("U001", "hash", "123", "Test", "User", 25, Usuario.Rol.ESTUDIANTE);

        assertEquals("U001", usuario.getCodigo());
        assertEquals("hash", usuario.getHashContrasena());
        assertEquals("123", usuario.getDni());
        assertEquals("Test", usuario.getNombre());
        assertEquals("User", usuario.getApellido());
        assertEquals(25, usuario.getEdad());
        assertEquals(Usuario.Rol.ESTUDIANTE, usuario.getRol());
    }

    @Test
    void constructor_debeLanzarExcepcionSiCodigoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioConcreto(null, "hash", "123", "Test", "User", 25, Usuario.Rol.ESTUDIANTE));
    }

    @Test
    void constructor_debeLanzarExcepcionSiCodigoVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioConcreto("", "hash", "123", "Test", "User", 25, Usuario.Rol.ESTUDIANTE));
    }

    @Test
    void constructor_debeLanzarExcepcionSiHashNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioConcreto("U001", null, "123", "Test", "User", 25, Usuario.Rol.ESTUDIANTE));
    }

    @Test
    void constructor_debeLanzarExcepcionSiHashVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioConcreto("U001", "", "123", "Test", "User", 25, Usuario.Rol.ESTUDIANTE));
    }

    @Test
    void constructor_debeLanzarExcepcionSiDniNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioConcreto("U001", "hash", null, "Test", "User", 25, Usuario.Rol.ESTUDIANTE));
    }

    @Test
    void constructor_debeLanzarExcepcionSiDniVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioConcreto("U001", "hash", "", "Test", "User", 25, Usuario.Rol.ESTUDIANTE));
    }

    @Test
    void constructor_debeLanzarExcepcionSiNombreNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioConcreto("U001", "hash", "123", null, "User", 25, Usuario.Rol.ESTUDIANTE));
    }

    @Test
    void constructor_debeLanzarExcepcionSiNombreVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioConcreto("U001", "hash", "123", "", "User", 25, Usuario.Rol.ESTUDIANTE));
    }

    @Test
    void constructor_debeLanzarExcepcionSiApellidoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioConcreto("U001", "hash", "123", "Test", null, 25, Usuario.Rol.ESTUDIANTE));
    }

    @Test
    void constructor_debeLanzarExcepcionSiApellidoVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioConcreto("U001", "hash", "123", "Test", "", 25, Usuario.Rol.ESTUDIANTE));
    }

    @Test
    void constructor_debeLanzarExcepcionSiEdadCero() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioConcreto("U001", "hash", "123", "Test", "User", 0, Usuario.Rol.ESTUDIANTE));
    }

    @Test
    void constructor_debeLanzarExcepcionSiEdadNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioConcreto("U001", "hash", "123", "Test", "User", -5, Usuario.Rol.ESTUDIANTE));
    }

    @Test
    void constructor_debeLanzarExcepcionSiRolNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioConcreto("U001", "hash", "123", "Test", "User", 25, null));
    }

    @Test
    void setters_debenActualizarPropiedades() {
        Usuario usuario = new UsuarioConcreto("U001", "hash", "123", "Test", "User", 25, Usuario.Rol.ESTUDIANTE);

        usuario.setCodigo("U002");
        usuario.setHashContrasena("nuevoHash");
        usuario.setDni("456");
        usuario.setNombre("Nuevo");
        usuario.setApellido("Apellido");
        usuario.setEdad(30);

        assertEquals("U002", usuario.getCodigo());
        assertEquals("nuevoHash", usuario.getHashContrasena());
        assertEquals("456", usuario.getDni());
        assertEquals("Nuevo", usuario.getNombre());
        assertEquals("Apellido", usuario.getApellido());
        assertEquals(30, usuario.getEdad());
        // El rol no tiene setter, se fija en el constructor de la subclase
    }

    @Test
    void equals_debeRetornarTrueParaMismoCodigo() {
        Usuario u1 = new UsuarioConcreto("U001", "hash1", "111", "A", "B", 20, Usuario.Rol.ESTUDIANTE);
        Usuario u2 = new UsuarioConcreto("U001", "hash2", "222", "C", "D", 30, Usuario.Rol.DOCENTE);
        assertEquals(u1, u2);
    }

    @Test
    void equals_debeRetornarFalseParaDiferenteCodigo() {
        Usuario u1 = new UsuarioConcreto("U001", "hash", "111", "A", "B", 20, Usuario.Rol.ESTUDIANTE);
        Usuario u2 = new UsuarioConcreto("U002", "hash", "111", "A", "B", 20, Usuario.Rol.ESTUDIANTE);
        assertNotEquals(u1, u2);
    }

    @Test
    void equals_debeRetornarFalseSiEsNull() {
        Usuario u1 = new UsuarioConcreto("U001", "hash", "111", "A", "B", 20, Usuario.Rol.ESTUDIANTE);
        assertNotEquals(u1, null);
    }

    @Test
    void hashCode_debeSerConsistenteConEquals() {
        Usuario u1 = new UsuarioConcreto("U001", "hash1", "111", "A", "B", 20, Usuario.Rol.ESTUDIANTE);
        Usuario u2 = new UsuarioConcreto("U001", "hash2", "222", "C", "D", 30, Usuario.Rol.DOCENTE);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    void toString_debeContenerDatos() {
        Usuario usuario = new UsuarioConcreto("U001", "hash", "123", "Test", "User", 25, Usuario.Rol.ESTUDIANTE);
        String texto = usuario.toString();
        assertTrue(texto.contains("U001"));
        assertTrue(texto.contains("Test"));
        assertTrue(texto.contains("ESTUDIANTE"));
    }

    @Test
    void constructor_dniVacioDebeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioConcreto("U001", "hash", "", "Test", "User", 25, Usuario.Rol.ESTUDIANTE));
    }

    @Test
    void constructor_dniConEspaciosDebeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioConcreto("U001", "hash", "123 456", "Test", "User", 25, Usuario.Rol.ESTUDIANTE));
    }

    @Test
    void constructor_dniConGuionesDebeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioConcreto("U001", "hash", "12-345", "Test", "User", 25, Usuario.Rol.ESTUDIANTE));
    }

    @Test
    void constructor_dni9DigitosDebeSerAceptado() {
        Usuario usuario = new UsuarioConcreto("U001", "hash", "123456789", "Test", "User", 25, Usuario.Rol.ESTUDIANTE);
        assertEquals("123456789", usuario.getDni());
    }

    @Test
    void constructor_dniConKChilenoDebeSerAceptado() {
        Usuario usuario = new UsuarioConcreto("U001", "hash", "12345678K", "Test", "User", 25, Usuario.Rol.ESTUDIANTE);
        assertEquals("12345678K", usuario.getDni());
    }

    @Test
    void constructor_dni21CaracteresDebeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioConcreto("U001", "hash", "123456789012345678901", "Test", "User", 25, Usuario.Rol.ESTUDIANTE));
    }
}
