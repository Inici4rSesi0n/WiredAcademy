package infraestructura.persistencia.mappers;
import dominio.modelo.Padre;
import infraestructura.persistencia.entidades.PadreEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE, uses={EstudianteMapper.class})
public abstract class PadreMapper{
    @Autowired
    protected EstudianteMapper estudianteMapper;
    public Padre toDomain(PadreEntity entity) {
        if (entity == null) return null;
        Padre padre = new Padre(
            entity.getCodigo(), entity.getHashContrasena(), entity.getDni(),
            entity.getNombre(), entity.getApellido(), entity.getEdad(),
            entity.getEstudiantes().stream().map(estudianteMapper::toDomain).toList()
        );
        return padre;
    }
    @Mapping(target="id", ignore=true)
    @Mapping(target="estudiantes", ignore=true)
    public abstract PadreEntity toEntity(Padre domain);
    public List<Padre> toDomainList(List<PadreEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }
}
