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
@DiscriminatorValue("PADRE")
public class PadreEntity extends UsuarioEntity{
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(
        name="padres_estudiantes",
        joinColumns=@JoinColumn(name="padre_id"),
        inverseJoinColumns=@JoinColumn(name="estudiante_id")
    )
    private List<EstudianteEntity> estudiantes = new ArrayList<>();
    
    public List<EstudianteEntity> getEstudiantes(){return estudiantes;}
    public void setEstudiantes(List<EstudianteEntity> estudiantes){this.estudiantes=estudiantes;}
}
