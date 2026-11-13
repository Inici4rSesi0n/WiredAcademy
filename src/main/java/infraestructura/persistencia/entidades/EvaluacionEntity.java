/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.entidades;
import jakarta.persistence.*;
@Entity
@Table(name="evaluaciones")
public class EvaluacionEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private double peso;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="asignatura_id", nullable=false)
    private AsignaturaEntity asignatura;
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre=nombre;}
    public double getPeso(){return peso;}
    public void setPeso(double peso){this.peso=peso;}
    public AsignaturaEntity getAsignatura(){return asignatura;}
    public void setAsignatura(AsignaturaEntity asignatura){this.asignatura=asignatura;}
}
