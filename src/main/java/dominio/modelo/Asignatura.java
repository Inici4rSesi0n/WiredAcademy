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
public class Asignatura implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    protected Asignatura() {}
    public Asignatura(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la asignatura no puede ser nulo o vacío");
        }
        this.nombre = nombre;
    }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asignatura)) return false;
        Asignatura that = (Asignatura) o;
        return nombre != null && nombre.equalsIgnoreCase(that.nombre);
    }
    @Override
    public int hashCode() {
        return nombre != null ? nombre.toLowerCase().hashCode() : 0;
    }
    @Override
    public String toString() {
        return "Asignatura [nombre=" + nombre + "]";
    }
}