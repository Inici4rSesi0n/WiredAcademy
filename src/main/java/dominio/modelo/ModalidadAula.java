/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
/**
 *
 * @author inici4rsesi0n
 */
public enum ModalidadAula {
    PRESENCIAL,
    VIRTUAL,
    REMOTO;
    public static ModalidadAula fromString(String texto) {
        if (texto == null || texto.isBlank()) {
            return PRESENCIAL;
        }
        for (ModalidadAula m : values()) {
            if (m.name().equalsIgnoreCase(texto.trim())) {
                return m;
            }
        }
        return PRESENCIAL;
    }
}