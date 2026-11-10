/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.entidades;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Entity
@Table(name="matriculas")
public class MatriculaEntity{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="estudiante_id", nullable=false)
    private EstudianteEntity estudiante;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="periodo_id", nullable=false)
    private PeriodoAcademicoEntity periodo;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="grupo_id", nullable=false)
    private GrupoEntity grupo;

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(
        name="matricula_asignaturas",
        joinColumns=@JoinColumn(name="matricula_id"),
        inverseJoinColumns=@JoinColumn(name="asignatura_id")
    )
    private List<AsignaturaEntity> asignaturas=new ArrayList<>();

    @Column(nullable=false)
    private String fecha;
    @Column(nullable=false, length=20)
    private String estado;

    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public EstudianteEntity getEstudiante(){return estudiante;}
    public void setEstudiante(EstudianteEntity estudiante){this.estudiante=estudiante;}
    public PeriodoAcademicoEntity getPeriodo(){return periodo;}
    public void setPeriodo(PeriodoAcademicoEntity periodo){this.periodo=periodo;}
    public GrupoEntity getGrupo(){return grupo;}
    public void setGrupo(GrupoEntity grupo){this.grupo=grupo;}
    public List<AsignaturaEntity> getAsignaturas(){return asignaturas;}
    public void setAsignaturas(List<AsignaturaEntity> asignaturas){this.asignaturas=asignaturas;}
    public String getFecha(){return fecha;}
    public void setFecha(String fecha){this.fecha=fecha;}
    public String getEstado(){return estado;}
    public void setEstado(String estado){this.estado=estado;}
}
