/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
import java.io.Serializable;
public class Calificacion implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Evaluacion evaluacion;
    private Estudiante estudiante;
    private Double nota;
    protected Calificacion() {}
    public Calificacion(Long id, Evaluacion evaluacion, Estudiante estudiante, Double nota) {
        if (evaluacion == null) throw new IllegalArgumentException("La evaluación no puede ser nula");
        if (estudiante == null) throw new IllegalArgumentException("El estudiante no puede ser nulo");
        if (nota != null && (nota < 0 || nota > 20)) throw new IllegalArgumentException("La nota debe estar entre 0 y 20");
        this.id = id;
        this.evaluacion = evaluacion;
        this.estudiante = estudiante;
        this.nota = nota;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Evaluacion getEvaluacion() { return evaluacion; }
    public void setEvaluacion(Evaluacion evaluacion) { this.evaluacion = evaluacion; }
    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }
    public Double getNota() { return nota; }
    public void setNota(Double nota) { this.nota = nota; }
}
