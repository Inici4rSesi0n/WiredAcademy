/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Calificacion;
import infraestructura.persistencia.entidades.CalificacionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
@Mapper(componentModel="spring", uses={EvaluacionMapper.class, EstudianteMapper.class})
public interface CalificacionMapper {
    Calificacion toDomain(CalificacionEntity entity);
    @Mapping(target="id", ignore=true)
    CalificacionEntity toEntity(Calificacion domain);
    List<Calificacion> toDomainList(List<CalificacionEntity> entities);
}
