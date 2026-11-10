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
@Table(name="grados")
public class GradoEntity{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=50)
    private String nombre;
    @Column(nullable=false, length=50)
    private String nivel;
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre=nombre;}
    public String getNivel(){return nivel;}
    public void setNivel(String nivel){this.nivel=nivel;}
}
