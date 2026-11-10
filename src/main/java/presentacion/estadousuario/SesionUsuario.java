/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.estadousuario;

import dominio.modelo.Usuario;

/**
 *
 * @author inici4rsesi0n
 */
public class SesionUsuario {

    private static Usuario usuarioActual;
    private static boolean sesionActiva;

    public static void iniciarSesion(Usuario usuario) {
        usuarioActual = usuario;
        sesionActiva = true;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
        sesionActiva = false;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static boolean isSesionActiva() {
        return sesionActiva && usuarioActual != null;
    }
}