/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
import java.io.Serializable;
import java.time.LocalDate;
public class Asistencia implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Grupo grupo;
    private Asignatura asignatura;
    private Estudiante estudiante;
    private LocalDate fecha;
    private EstadoAsistencia estado;
    protected Asistencia() {}
    public Asistencia(Long id, Grupo grupo, Asignatura asignatura, Estudiante estudiante, LocalDate fecha, EstadoAsistencia estado) {
        if (grupo == null) throw new IllegalArgumentException("El grupo no puede ser nulo");
        if (asignatura == null) throw new IllegalArgumentException("La asignatura no puede ser nula");
        if (estudiante == null) throw new IllegalArgumentException("El estudiante no puede ser nulo");
        if (fecha == null) throw new IllegalArgumentException("La fecha no puede ser nula");
        if (estado == null) throw new IllegalArgumentException("El estado no puede ser nulo");
        this.id = id;
        this.grupo = grupo;
        this.asignatura = asignatura;
        this.estudiante = estudiante;
        this.fecha = fecha;
        this.estado = estado;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }
    public Asignatura getAsignatura() { return asignatura; }
    public void setAsignatura(Asignatura asignatura) { this.asignatura = asignatura; }
    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public EstadoAsistencia getEstado() { return estado; }
    public void setEstado(EstadoAsistencia estado) { this.estado = estado; }
}
