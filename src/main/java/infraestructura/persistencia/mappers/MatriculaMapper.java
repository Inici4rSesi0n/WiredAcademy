/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.InscripcionAsignatura;
import dominio.modelo.Matricula;
import infraestructura.persistencia.entidades.MatriculaAsignaturaEntity;
import infraestructura.persistencia.entidades.MatriculaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE, 
        uses={EstudianteMapper.class, PeriodoAcademicoMapper.class, GrupoMapper.class, AsignaturaMapper.class})
public interface MatriculaMapper {
    @Mapping(target="estado", expression="java(dominio.modelo.EstadoMatricula.valueOf(entity.getEstado()))")
    @Mapping(target="inscripciones", ignore=true)
    @Mapping(target="grupo.estudiantes", ignore=true)
    Matricula toDomain(MatriculaEntity entity);
    @Mapping(target="id", ignore=true)
    @Mapping(target="estudiante", ignore=true)
    @Mapping(target="periodo", ignore=true)
    @Mapping(target="grupo", ignore=true)
    @Mapping(target="inscripciones", ignore=true)
    @Mapping(target="estado", expression="java(domain.getEstado().name())")
    MatriculaEntity toEntity(Matricula domain);
    List<Matricula> toDomainList(List<MatriculaEntity> entities);
    @Mapping(target="id", ignore=true)
    @Mapping(target="matricula", ignore=true)
    MatriculaAsignaturaEntity toEntityInscripcion(InscripcionAsignatura domain);
    default InscripcionAsignatura toDomainInscripcion(MatriculaAsignaturaEntity entity) {
        if (entity == null) return null;
        AsignaturaMapper aMapper = new AsignaturaMapperImpl();
        GrupoMapper gMapper = new GrupoMapperImpl();
        return new InscripcionAsignatura(aMapper.toDomain(entity.getAsignatura()), gMapper.toDomain(entity.getGrupo()));
    }
}
