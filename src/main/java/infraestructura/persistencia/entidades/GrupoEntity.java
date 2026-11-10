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
@Table(name="grupos")
public class GrupoEntity{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=50)
    private String nombre;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="grado_id", nullable=false)
    private GradoEntity grado;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="tutor_id")
    private DocenteEntity tutor;
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(
        name="grupo_estudiantes",
        joinColumns=@JoinColumn(name="grupo_id"),
        inverseJoinColumns=@JoinColumn(name="estudiante_id")
    )
    private List<EstudianteEntity> estudiantes=new ArrayList<>();
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre=nombre;}
    public GradoEntity getGrado(){return grado;}
    public void setGrado(GradoEntity grado){this.grado=grado;}
    public DocenteEntity getTutor(){return tutor;}
    public void setTutor(DocenteEntity tutor){this.tutor=tutor;}
    public List<EstudianteEntity> getEstudiantes(){return estudiantes;}
    public void setEstudiantes(List<EstudianteEntity> estudiantes){this.estudiantes=estudiantes;}
}
