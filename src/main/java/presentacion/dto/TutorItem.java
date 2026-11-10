/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.dto;

import dominio.modelo.Grupo;

public class TutorItem {
    private final Grupo grupo;

    public TutorItem(Grupo grupo) {
        this.grupo = grupo;
    }

    public Grupo getGrupo() { return grupo; }
    public String getNombreGrupo() { return grupo.getNombre(); }
    public String getNombreTutor() { return grupo.getTutor() != null ? grupo.getTutor().getNombreCompleto() : "Sin tutor"; }
}
