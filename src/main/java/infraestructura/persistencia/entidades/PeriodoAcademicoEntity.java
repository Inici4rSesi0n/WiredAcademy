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
@Table(name="periodos_academicos")
public class PeriodoAcademicoEntity{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=50)
    private String nombre;
    @Column(name="fecha_inicio", nullable=false)
    private String fechaInicio;
    @Column(name="fecha_fin", nullable=false)
    private String fechaFin;
    @Column(nullable=false, length=20)
    private String estado;
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre=nombre;}
    public String getFechaInicio(){return fechaInicio;}
    public void setFechaInicio(String fechaInicio){this.fechaInicio=fechaInicio;}
    public String getFechaFin(){return fechaFin;}
    public void setFechaFin(String fechaFin){this.fechaFin=fechaFin;}
    public String getEstado(){return estado;}
    public void setEstado(String estado){this.estado=estado;}
}
