/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
import java.io.Serializable;
import java.util.Objects;
/**
 *
 * @author inici4rsesi0n
 */
public class Grado implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private String nivel;
    protected Grado() {}
    public Grado(String nombre, String nivel) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del grado no puede ser nulo o vacío");
        }
        if (nivel == null || nivel.isBlank()) {
            throw new IllegalArgumentException("El nivel del grado no puede ser nulo o vacío");
        }
        this.nombre = nombre;
        this.nivel = nivel;
    }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grado)) return false;
        Grado grado = (Grado) o;
        return nombre != null && nivel != null
                && nombre.equalsIgnoreCase(grado.nombre)
                && nivel.equalsIgnoreCase(grado.nivel);
    }
    @Override
    public int hashCode() {
        return Objects.hash(
                nombre != null ? nombre.toLowerCase() : null,
                nivel != null ? nivel.toLowerCase() : null);
    }
    @Override
    public String toString() {
        return "Grado [nombre=" + nombre + ", nivel=" + nivel + "]";
    }
}
