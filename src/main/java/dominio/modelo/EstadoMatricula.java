/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
/**
 *
 * @author inici4rsesi0n
 */
public enum EstadoMatricula {
    ACTIVA,
    RETIRADA,
    CULMINADA;
    public static EstadoMatricula fromString(String texto) {
        if (texto == null || texto.isBlank()) {
            return ACTIVA;
        }
        for (EstadoMatricula e : values()) {
            if (e.name().equalsIgnoreCase(texto.trim())) {
                return e;
            }
        }
        return ACTIVA;
    }
}