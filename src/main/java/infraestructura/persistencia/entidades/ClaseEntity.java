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
@Table(name="clases")
public class ClaseEntity{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="dia_semana", nullable=false, length=15)
    private String diaSemana;
    @Column(name="hora_inicio", nullable=false)
    private String horaInicio;
    @Column(name="hora_fin", nullable=false)
    private String horaFin;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="periodo_id", nullable=false)
    private PeriodoAcademicoEntity periodo;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="asignatura_id", nullable=false)
    private AsignaturaEntity asignatura;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="docente_id", nullable=false)
    private DocenteEntity docente;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="grupo_id", nullable=false)
    private GrupoEntity grupo;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="aula_id", nullable=false)
    private AulaEntity aula;
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getDiaSemana(){return diaSemana;}
    public void setDiaSemana(String diaSemana){this.diaSemana=diaSemana;}
    public String getHoraInicio(){return horaInicio;}
    public void setHoraInicio(String horaInicio){this.horaInicio=horaInicio;}
    public String getHoraFin(){return horaFin;}
    public void setHoraFin(String horaFin){this.horaFin=horaFin;}
    public PeriodoAcademicoEntity getPeriodo(){return periodo;}
    public void setPeriodo(PeriodoAcademicoEntity periodo){this.periodo=periodo;}
    public AsignaturaEntity getAsignatura(){return asignatura;}
    public void setAsignatura(AsignaturaEntity asignatura){this.asignatura=asignatura;}
    public DocenteEntity getDocente(){return docente;}
    public void setDocente(DocenteEntity docente){this.docente=docente;}
    public GrupoEntity getGrupo(){return grupo;}
    public void setGrupo(GrupoEntity grupo){this.grupo=grupo;}
    public AulaEntity getAula(){return aula;}
    public void setAula(AulaEntity aula){this.aula=aula;}
}
