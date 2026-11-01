package infraestructura.persistencia.mappers;
import dominio.modelo.CoordinadorAcademico;
import dominio.modelo.Grado;
import infraestructura.persistencia.entidades.CoordinadorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE, uses={GradoMapper.class})
public abstract class CoordinadorMapper{
    @Autowired
    protected GradoMapper gradoMapper;
    public CoordinadorAcademico toDomain(CoordinadorEntity entity) {
        if (entity == null) return null;
        CoordinadorAcademico coord = new CoordinadorAcademico(
            entity.getCodigo(), entity.getHashContrasena(), entity.getDni(),
            entity.getNombre(), entity.getApellido(), entity.getEdad()
        );
        if (entity.getGradosSupervisados() != null) {
            List<Grado> grados = entity.getGradosSupervisados().stream()
                .map(gradoMapper::toDomain)
                .toList();
            coord.setGradosSupervisados(grados);
        }
        return coord;
    }
    @Mapping(target="id", ignore=true)
    @Mapping(target="gradosSupervisados", ignore=true)
    public abstract CoordinadorEntity toEntity(CoordinadorAcademico domain);
    public List<CoordinadorAcademico> toDomainList(List<CoordinadorEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }
}
