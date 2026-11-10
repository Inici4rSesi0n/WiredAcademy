/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.sistema;

import aplicacion.servicio.BuscadorUsuario;
import dominio.modelo.Docente;
import dominio.modelo.Secretario;
import dominio.modelo.Usuario;
import dominio.puerto.externo.HashProvider;
import dominio.puerto.externo.LoggerPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author inici4rsesi0n
 */
class AutenticarUsuarioTest {

    private BuscadorUsuario buscadorMock;
    private HashProvider hashProvider;
    private LoggerPort loggerPort;

    private AutenticarUsuario autenticarUsuario;

    @BeforeEach
    void setUp() {
        buscadorMock = mock(BuscadorUsuario.class);
        hashProvider = mock(HashProvider.class);
        loggerPort = mock(LoggerPort.class);

        autenticarUsuario = new AutenticarUsuario(buscadorMock, hashProvider, loggerPort);
    }

    @Test
    void ejecutar_debeAutenticarDocenteCorrectamente() {
        String codigo = "D001";
        char[] contrasena = "secreta".toCharArray();
        Docente docente = new Docente(codigo, "hash", "123", "Juan", "Perez", 30, "Mat", new ArrayList<>());

        when(buscadorMock.buscarPorCodigo(codigo)).thenReturn(docente);
        when(hashProvider.verificarHash("hash", contrasena)).thenReturn(true);

        Usuario resultado = autenticarUsuario.ejecutar(codigo, contrasena);

        assertNotNull(resultado);
        assertEquals(docente, resultado);
    }

    @Test
    void ejecutar_debeFallarSiCodigoNoExiste() {
        String codigo = "XXX";
        char[] contrasena = "secreta".toCharArray();

        when(buscadorMock.buscarPorCodigo(codigo)).thenReturn(null);

        Usuario resultado = autenticarUsuario.ejecutar(codigo, contrasena);

        assertNull(resultado);
        verify(hashProvider, never()).verificarHash(anyString(), any());
    }

    @Test
    void ejecutar_debeFallarSiContrasenaIncorrecta() {
        String codigo = "D001";
        char[] contrasena = "incorrecta".toCharArray();
        Docente docente = new Docente(codigo, "hash", "123", "Ana", "Lopez", 28, "Ing", new ArrayList<>());

        when(buscadorMock.buscarPorCodigo(codigo)).thenReturn(docente);
        when(hashProvider.verificarHash("hash", contrasena)).thenReturn(false);

        Usuario resultado = autenticarUsuario.ejecutar(codigo, contrasena);

        assertNull(resultado);
    }

    @Test
    void ejecutar_debeAutenticarSecretarioCorrectamente() {
        String codigo = "S001";
        char[] contrasena = "pass".toCharArray();
        Secretario secretario = new Secretario(codigo, "hashS", "111", "Sec", "Uno", 35);

        when(buscadorMock.buscarPorCodigo(codigo)).thenReturn(secretario);
        when(hashProvider.verificarHash("hashS", contrasena)).thenReturn(true);

        Usuario resultado = autenticarUsuario.ejecutar(codigo, contrasena);

        assertNotNull(resultado);
        assertEquals(secretario, resultado);
    }

    @Test
    void ejecutar_debeLanzarExcepcionSiCodigoVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> autenticarUsuario.ejecutar("", "pass".toCharArray()));
    }

    @Test
    void ejecutar_debeLanzarExcepcionSiContrasenaNula() {
        assertThrows(IllegalArgumentException.class,
                () -> autenticarUsuario.ejecutar("D001", null));
    }
}
