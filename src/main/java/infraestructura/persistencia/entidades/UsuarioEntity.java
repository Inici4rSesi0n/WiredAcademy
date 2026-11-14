/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.entidades;
import jakarta.persistence.*;
@Entity
@Table(name="usuarios")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="rol", discriminatorType=DiscriminatorType.STRING)
public abstract class UsuarioEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true, length=20)
    private String codigo;
    @Column(name="hash_contrasena", nullable=false)
    private String hashContrasena;
    @Column(nullable=false, unique=true, length=20)
    private String dni;
    @Column(nullable=false)
    private String nombre;
    @Column(nullable=false)
    private String apellido;
    @Column(nullable=false)
    private Integer edad;
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getCodigo(){return codigo;}
    public void setCodigo(String codigo){this.codigo=codigo;}
    public String getHashContrasena(){return hashContrasena;}
    public void setHashContrasena(String hashContrasena){this.hashContrasena=hashContrasena;}
    public String getDni(){return dni;}
    public void setDni(String dni){this.dni=dni;}
    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre=nombre;}
    public String getApellido(){return apellido;}
    public void setApellido(String apellido){this.apellido=apellido;}
    public Integer getEdad(){return edad;}
    public void setEdad(Integer edad){this.edad=edad;}
}
