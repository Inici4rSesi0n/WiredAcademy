/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Asistencia;
import infraestructura.persistencia.entidades.AsistenciaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
@Mapper(componentModel="spring", uses={GrupoMapper.class, AsignaturaMapper.class, EstudianteMapper.class})
public interface AsistenciaMapper {
    @Mapping(target="estado", expression="java(dominio.modelo.EstadoAsistencia.valueOf(entity.getEstado()))")
    Asistencia toDomain(AsistenciaEntity entity);
    @Mapping(target="id", ignore=true)
    @Mapping(target="estado", expression="java(domain.getEstado().name())")
    @Mapping(target="grupo", ignore=true)
    @Mapping(target="asignatura", ignore=true)
    @Mapping(target="estudiante", ignore=true)
    AsistenciaEntity toEntity(Asistencia domain);
    List<Asistencia> toDomainList(List<AsistenciaEntity> entities);
}
