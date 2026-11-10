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
@DiscriminatorValue("COORDINADOR")
public class CoordinadorEntity extends UsuarioEntity{
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(
        name="coordinadores_grados",
        joinColumns=@JoinColumn(name="coordinador_id"),
        inverseJoinColumns=@JoinColumn(name="grado_id")
    )
    private List<GradoEntity> gradosSupervisados = new ArrayList<>();
    
    public List<GradoEntity> getGradosSupervisados(){return gradosSupervisados;}
    public void setGradosSupervisados(List<GradoEntity> gradosSupervisados){this.gradosSupervisados=gradosSupervisados;}
}
