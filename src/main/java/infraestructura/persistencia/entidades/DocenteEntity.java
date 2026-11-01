package infraestructura.persistencia.entidades;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Entity
@DiscriminatorValue("DOCENTE")
public class DocenteEntity extends UsuarioEntity{
    private String especialidad;
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(
        name="docentes_asignaturas",
        joinColumns=@JoinColumn(name="docente_id"),
        inverseJoinColumns=@JoinColumn(name="asignatura_id")
    )
    private List<AsignaturaEntity> asignaturas = new ArrayList<>();
    @OneToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="grupo_tutoria_id")
    private GrupoEntity tutoria;
    public String getEspecialidad(){return especialidad;}
    public void setEspecialidad(String especialidad){this.especialidad=especialidad;}
    public List<AsignaturaEntity> getAsignaturas(){return asignaturas;}
    public void setAsignaturas(List<AsignaturaEntity> asignaturas){this.asignaturas=asignaturas;}
    public GrupoEntity getTutoria(){return tutoria;}
    public void setTutoria(GrupoEntity tutoria){this.tutoria=tutoria;}
}
