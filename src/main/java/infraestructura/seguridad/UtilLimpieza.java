/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.seguridad;
import java.util.Arrays;
/**
 *
 * @author inici4rsesi0n
 */
public class UtilLimpieza {
    public static void limpiarContraseña(char[] pwd) {
        if (pwd != null) {
            Arrays.fill(pwd, '\0');
        }
    }
}