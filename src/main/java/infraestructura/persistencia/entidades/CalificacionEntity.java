/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.entidades;
import jakarta.persistence.*;
@Entity
@Table(name="calificaciones")
public class CalificacionEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="evaluacion_id", nullable=false)
    private EvaluacionEntity evaluacion;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="estudiante_id", nullable=false)
    private EstudianteEntity estudiante;
    private Double nota;
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public EvaluacionEntity getEvaluacion(){return evaluacion;}
    public void setEvaluacion(EvaluacionEntity evaluacion){this.evaluacion=evaluacion;}
    public EstudianteEntity getEstudiante(){return estudiante;}
    public void setEstudiante(EstudianteEntity estudiante){this.estudiante=estudiante;}
    public Double getNota(){return nota;}
    public void setNota(Double nota){this.nota=nota;}
}
