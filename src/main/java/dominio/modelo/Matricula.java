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
/**
 *
 * @author inici4rsesi0n
 */
public class Matricula implements Serializable {
    private static final long serialVersionUID = 1L;
    private Estudiante estudiante;
    private PeriodoAcademico periodo;
    private Grupo grupo;
    private List<InscripcionAsignatura> inscripciones;
    private String fecha;
    private EstadoMatricula estado;
    public Matricula() {
        this.inscripciones = new ArrayList<>();
    }
    public Matricula(Estudiante estudiante, PeriodoAcademico periodo, Grupo grupo,
                     List<Asignatura> asignaturas, String fecha, EstadoMatricula estado) {
        if (estudiante == null) throw new IllegalArgumentException("El estudiante no puede ser nulo");
        if (periodo == null) throw new IllegalArgumentException("El periodo académico no puede ser nulo");
        if (grupo == null) throw new IllegalArgumentException("El grupo no puede ser nulo");
        this.estudiante = estudiante;
        this.periodo = periodo;
        this.grupo = grupo;
        this.inscripciones = new ArrayList<>();
        if (asignaturas != null) {
            for (Asignatura a : asignaturas) {
                this.inscripciones.add(new InscripcionAsignatura(a, grupo));
            }
        }
        this.fecha = (fecha != null && !fecha.isBlank()) ? fecha : "";
        this.estado = (estado != null) ? estado : EstadoMatricula.ACTIVA;
    }
    public Matricula(Estudiante estudiante, PeriodoAcademico periodo,
                     List<InscripcionAsignatura> inscripciones, String fecha, EstadoMatricula estado) {
        if (estudiante == null) throw new IllegalArgumentException("El estudiante no puede ser nulo");
        if (periodo == null) throw new IllegalArgumentException("El periodo académico no puede ser nulo");
        if (inscripciones == null || inscripciones.isEmpty()) throw new IllegalArgumentException("Debe tener al menos una asignatura");
        this.estudiante = estudiante;
        this.periodo = periodo;
        this.grupo = inscripciones.get(0).getGrupo();
        this.inscripciones = new ArrayList<>(inscripciones);
        this.fecha = (fecha != null && !fecha.isBlank()) ? fecha : "";
        this.estado = (estado != null) ? estado : EstadoMatricula.ACTIVA;
    }
    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }
    public PeriodoAcademico getPeriodo() { return periodo; }
    public void setPeriodo(PeriodoAcademico periodo) { this.periodo = periodo; }
    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }
    public List<InscripcionAsignatura> getInscripciones() {
        return Collections.unmodifiableList(inscripciones);
    }
    public void agregarInscripcion(InscripcionAsignatura inscripcion) {
        if (inscripcion == null) throw new IllegalArgumentException("La inscripción no puede ser nula");
        if (!inscripciones.contains(inscripcion)) {
            inscripciones.add(inscripcion);
        }
    }
    public void removerInscripcion(InscripcionAsignatura inscripcion) {
        inscripciones.remove(inscripcion);
    }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public EstadoMatricula getEstado() { return estado; }
    public void setEstado(EstadoMatricula estado) { this.estado = estado; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Matricula)) return false;
        Matricula that = (Matricula) o;
        return Objects.equals(estudiante, that.estudiante) &&
               Objects.equals(periodo, that.periodo);
    }
    @Override
    public int hashCode() {
        return Objects.hash(estudiante, periodo);
    }
    @Override
    public String toString() {
        String nombresAsignaturas = inscripciones.stream()
                .map(i -> i.getAsignatura().getNombre())
                .collect(java.util.stream.Collectors.joining(", "));
        return "Matricula [estudiante=" + (estudiante != null ? estudiante.getCodigo() : "N/A")
                + ", periodo=" + (periodo != null ? periodo.getNombre() : "N/A")
                + ", asignaturas=[" + nombresAsignaturas + "]"
                + ", fecha=" + fecha
                + ", estado=" + estado + "]";
    }
}
