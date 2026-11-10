/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
import java.io.Serializable;
/**
 *
 * @author inici4rsesi0n
 */
public class PeriodoAcademico implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private String fechaInicio;
    private String fechaFin;
    private String estado;
    protected PeriodoAcademico() {}
    public PeriodoAcademico(String nombre, String fechaInicio, String fechaFin, String estado) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del periodo no puede ser nulo o vacío");
        }
        if (fechaInicio == null || fechaInicio.isBlank()) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser nula o vacía");
        }
        if (fechaFin == null || fechaFin.isBlank()) {
            throw new IllegalArgumentException("La fecha de fin no puede ser nula o vacía");
        }
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = (estado != null && !estado.isBlank()) ? estado : "Activo";
    }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PeriodoAcademico)) return false;
        PeriodoAcademico that = (PeriodoAcademico) o;
        return nombre != null && nombre.equalsIgnoreCase(that.nombre);
    }
    @Override
    public int hashCode() {
        return nombre != null ? nombre.toLowerCase().hashCode() : 0;
    }
    @Override
    public String toString() {
        return "PeriodoAcademico [nombre=" + nombre + ", estado=" + estado + "]";
    }
}