/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.entidades;
import jakarta.persistence.*;
/**
 *
 * @author inici4rsesi0n
 */
@Entity
@Table(name="matricula_asignaturas")
public class MatriculaAsignaturaEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="matricula_id", nullable=false)
    private MatriculaEntity matricula;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="asignatura_id", nullable=false)
    private AsignaturaEntity asignatura;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="grupo_id", nullable=false)
    private GrupoEntity grupo;
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public MatriculaEntity getMatricula(){return matricula;}
    public void setMatricula(MatriculaEntity matricula){this.matricula=matricula;}
    public AsignaturaEntity getAsignatura(){return asignatura;}
    public void setAsignatura(AsignaturaEntity asignatura){this.asignatura=asignatura;}
    public GrupoEntity getGrupo(){return grupo;}
    public void setGrupo(GrupoEntity grupo){this.grupo=grupo;}
}
