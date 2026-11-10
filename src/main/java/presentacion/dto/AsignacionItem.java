/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.dto;
import dominio.modelo.Asignatura;
public class AsignacionItem {
    private final String codigoDocente, nombreDocente, nombreAsignatura;
    private final Asignatura asignatura;
    public AsignacionItem(String codigoDocente, String nombreDocente, String nombreAsignatura, Asignatura asignatura) {
        this.codigoDocente = codigoDocente;
        this.nombreDocente = nombreDocente;
        this.nombreAsignatura = nombreAsignatura;
        this.asignatura = asignatura;
    }
    public String getCodigoDocente() { return codigoDocente; }
    public String getNombreDocente() { return nombreDocente; }
    public String getNombreAsignatura() { return nombreAsignatura; }
    public Asignatura getAsignatura() { return asignatura; }
}
