/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
public class Grupo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private Grado grado;
    private Docente tutor;
    private List<Estudiante> estudiantes;
    private Modalidad modalidad;
    public Grupo() {}
    public Grupo(String nombre, Grado grado) {
        this(nombre, grado, Modalidad.PRESENCIAL);
    }
    public Grupo(String nombre, Grado grado, Modalidad modalidad) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre del grupo no puede ser nulo o vacío");
        if (grado == null) throw new IllegalArgumentException("El grado no puede ser nulo");
        this.nombre = nombre;
        this.grado = grado;
        this.tutor = null;
        this.estudiantes = new ArrayList<>();
        this.modalidad = (modalidad != null) ? modalidad : Modalidad.PRESENCIAL;
    }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Grado getGrado() { return grado; }
    public void setGrado(Grado grado) { this.grado = grado; }
    public Docente getTutor() { return tutor; }
    public void asignarTutor(Docente tutor) {
        if (this.tutor != null) {
            this.tutor.setTutoria(null);
        }
        this.tutor = tutor;
        if (tutor != null) {
            tutor.setTutoria(this);
        }
    }
    public List<Estudiante> getEstudiantes() {
        return Collections.unmodifiableList(estudiantes);
    }
    public void agregarEstudiante(Estudiante estudiante) {
        if (estudiante == null) throw new IllegalArgumentException("El estudiante no puede ser nulo");
        if (!estudiantes.contains(estudiante)) {
            estudiantes.add(estudiante);
        }
    }
    public void removerEstudiante(Estudiante estudiante) {
        estudiantes.remove(estudiante);
    }
    public Modalidad getModalidad() { return modalidad; }
    public void setModalidad(Modalidad modalidad) { this.modalidad = (modalidad != null) ? modalidad : Modalidad.PRESENCIAL; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grupo)) return false;
        Grupo grupo = (Grupo) o;
        return nombre != null && grado != null && nombre.equalsIgnoreCase(grupo.nombre) && Objects.equals(grado, grupo.grado);
    }
    @Override
    public int hashCode() {
        return Objects.hash(nombre != null ? nombre.toLowerCase() : null, grado);
    }
    @Override
    public String toString() {
        String tutorCodigo = (tutor != null) ? tutor.getCodigo() : "Sin tutor";
        return "Grupo [nombre=" + nombre + ", grado=" + (grado != null ? grado.getNombre() : "Sin grado") + ", modalidad=" + modalidad + "]";
    }
}
