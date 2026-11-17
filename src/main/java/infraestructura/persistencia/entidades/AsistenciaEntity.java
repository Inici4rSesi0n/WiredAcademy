/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.entidades;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity
@Table(name="asistencias")
public class AsistenciaEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="grupo_id", nullable=false)
    private GrupoEntity grupo;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="asignatura_id", nullable=false)
    private AsignaturaEntity asignatura;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="estudiante_id", nullable=false)
    private EstudianteEntity estudiante;
    @Column(nullable=false)
    private LocalDate fecha;
    @Column(nullable=false, length=20)
    private String estado;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public GrupoEntity getGrupo() { return grupo; }
    public void setGrupo(GrupoEntity grupo) { this.grupo = grupo; }
    public AsignaturaEntity getAsignatura() { return asignatura; }
    public void setAsignatura(AsignaturaEntity asignatura) { this.asignatura = asignatura; }
    public EstudianteEntity getEstudiante() { return estudiante; }
    public void setEstudiante(EstudianteEntity estudiante) { this.estudiante = estudiante; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
