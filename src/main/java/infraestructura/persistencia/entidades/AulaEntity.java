/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.entidades;
import jakarta.persistence.*;
@Entity
@Table(name="aulas")
public class AulaEntity{
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=50)
    private String nombre;
    @Column(nullable=false)
    private Integer capacidad;
    @Column(nullable=false, length=100)
    private String ubicacion;
    @Column(nullable=false, length=50)
    private String tipo;
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre=nombre;}
    public Integer getCapacidad(){return capacidad;}
    public void setCapacidad(Integer capacidad){this.capacidad=capacidad;}
    public String getUbicacion(){return ubicacion;}
    public void setUbicacion(String ubicacion){this.ubicacion=ubicacion;}
    public String getTipo(){return tipo;}
    public void setTipo(String tipo){this.tipo=tipo;}
}
