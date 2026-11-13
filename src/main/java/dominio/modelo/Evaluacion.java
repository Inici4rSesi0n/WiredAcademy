/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
import java.io.Serializable;
public class Evaluacion implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String nombre;
    private double peso;
    private Asignatura asignatura;
    protected Evaluacion() {}
    public Evaluacion(Long id, String nombre, double peso, Asignatura asignatura) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre no puede ser nulo");
        if (peso <= 0 || peso > 1) throw new IllegalArgumentException("El peso debe estar entre 0.01 y 1.0");
        if (asignatura == null) throw new IllegalArgumentException("La asignatura no puede ser nula");
        this.id = id;
        this.nombre = nombre;
        this.peso = peso;
        this.asignatura = asignatura;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
    public Asignatura getAsignatura() { return asignatura; }
    public void setAsignatura(Asignatura asignatura) { this.asignatura = asignatura; }
}
