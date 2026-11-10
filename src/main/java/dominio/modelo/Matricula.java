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
    private List<Asignatura> asignaturas;
    private String fecha;
    private EstadoMatricula estado;
    protected Matricula() {
        this.asignaturas = new ArrayList<>();
    }
    public Matricula(Estudiante estudiante, PeriodoAcademico periodo, Grupo grupo,
                     List<Asignatura> asignaturas, String fecha, EstadoMatricula estado) {
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante no puede ser nulo");
        }
        if (periodo == null) {
            throw new IllegalArgumentException("El periodo académico no puede ser nulo");
        }
        if (grupo == null) {
            throw new IllegalArgumentException("El grupo no puede ser nulo");
        }
        this.estudiante = estudiante;
        this.periodo = periodo;
        this.grupo = grupo;
        this.asignaturas = (asignaturas != null) ? new ArrayList<>(asignaturas) : new ArrayList<>();
        this.fecha = (fecha != null && !fecha.isBlank()) ? fecha : "";
        this.estado = (estado != null) ? estado : EstadoMatricula.ACTIVA;
    }
    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }
    public PeriodoAcademico getPeriodo() { return periodo; }
    public void setPeriodo(PeriodoAcademico periodo) { this.periodo = periodo; }
    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }
    public List<Asignatura> getAsignaturas() {
        return Collections.unmodifiableList(asignaturas);
    }
    public void agregarAsignatura(Asignatura asignatura) {
        if (asignatura == null) {
            throw new IllegalArgumentException("La asignatura no puede ser nula");
        }
        if (!asignaturas.contains(asignatura)) {
            asignaturas.add(asignatura);
        }
    }
    public void removerAsignatura(Asignatura asignatura) {
        asignaturas.remove(asignatura);
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
               Objects.equals(periodo, that.periodo) &&
               Objects.equals(grupo, that.grupo);
    }
    @Override
    public int hashCode() {
        return Objects.hash(estudiante, periodo, grupo);
    }
    @Override
    public String toString() {
        String nombresAsignaturas = asignaturas.stream()
                .map(Asignatura::getNombre)
                .collect(java.util.stream.Collectors.joining(", "));
        return "Matricula [estudiante=" + (estudiante != null ? estudiante.getCodigo() : "N/A")
                + ", periodo=" + (periodo != null ? periodo.getNombre() : "N/A")
                + ", grupo=" + (grupo != null ? grupo.getNombre() : "N/A")
                + ", asignaturas=[" + nombresAsignaturas + "]"
                + ", fecha=" + fecha
                + ", estado=" + estado + "]";
    }
}
