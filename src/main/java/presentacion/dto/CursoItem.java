/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.dto;
public class CursoItem {
    private final String nombreAsignatura;
    private final String nombreDocente;
    private final String nombreGrupo;
    private final String aula;
    public CursoItem(String nombreAsignatura, String nombreDocente, String nombreGrupo, String aula) {
        this.nombreAsignatura = nombreAsignatura;
        this.nombreDocente = nombreDocente;
        this.nombreGrupo = nombreGrupo;
        this.aula = aula;
    }
    public String getNombreAsignatura() { return nombreAsignatura; }
    public String getNombreDocente() { return nombreDocente; }
    public String getNombreGrupo() { return nombreGrupo; }
    public String getAula() { return aula; }
}
