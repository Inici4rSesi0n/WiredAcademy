/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
import java.io.Serializable;
public class Aula implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private int capacidad;
    private String ubicacion;
    private String tipo;
    protected Aula() {}
    public Aula(String nombre, int capacidad, String ubicacion, String tipo) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre del aula no puede ser nulo o vacío");
        if (capacidad <= 0) throw new IllegalArgumentException("La capacidad del aula debe ser mayor a 0");
        if (ubicacion == null || ubicacion.isBlank()) throw new IllegalArgumentException("La ubicación del aula no puede ser nula o vacía");
        if (tipo == null || tipo.isBlank()) throw new IllegalArgumentException("El tipo de aula no puede ser nulo o vacío");
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.ubicacion = ubicacion;
        this.tipo = tipo;
    }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Aula)) return false;
        Aula aula = (Aula) o;
        return nombre != null && nombre.equalsIgnoreCase(aula.nombre);
    }
    @Override
    public int hashCode() {
        return nombre != null ? nombre.toLowerCase().hashCode() : 0;
    }
    @Override
    public String toString() {
        return "Aula [nombre=" + nombre + ", capacidad=" + capacidad + ", tipo=" + tipo + "]";
    }
}
