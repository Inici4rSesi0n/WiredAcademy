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
@Table(name="recreos")
public class RecreoEntity{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="dia_semana", nullable=false, length=15)
    private String diaSemana;
    @Column(name="hora_inicio", nullable=false)
    private String horaInicio;
    @Column(name="hora_fin", nullable=false)
    private String horaFin;
    @Column(nullable=false, length=100)
    private String descripcion;
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(
        name="recreo_periodos",
        joinColumns=@JoinColumn(name="recreo_id"),
        inverseJoinColumns=@JoinColumn(name="periodo_id")
    )
    private List<PeriodoAcademicoEntity> periodos=new ArrayList<>();
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getDiaSemana(){return diaSemana;}
    public void setDiaSemana(String diaSemana){this.diaSemana=diaSemana;}
    public String getHoraInicio(){return horaInicio;}
    public void setHoraInicio(String horaInicio){this.horaInicio=horaInicio;}
    public String getHoraFin(){return horaFin;}
    public void setHoraFin(String horaFin){this.horaFin=horaFin;}
    public String getDescripcion(){return descripcion;}
    public void setDescripcion(String descripcion){this.descripcion=descripcion;}
    public List<PeriodoAcademicoEntity> getPeriodos(){return periodos;}
    public void setPeriodos(List<PeriodoAcademicoEntity> periodos){this.periodos=periodos;}
}
