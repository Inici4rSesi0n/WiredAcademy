/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.puerto.externo;
/**
 *
 * @author inici4rsesi0n
 */
public interface HashProvider {
    String generarHash(char[] contraseña);
    boolean verificarHash(String hash, char[] contraseña);
}