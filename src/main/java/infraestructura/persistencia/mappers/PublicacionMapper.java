/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Publicacion;
import dominio.modelo.TipoPublicacion;
import infraestructura.persistencia.entidades.PublicacionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE, uses={AsignaturaMapper.class})
public interface PublicacionMapper {
    @Mapping(target="tipo", expression="java(dominio.modelo.TipoPublicacion.valueOf(entity.getTipo()))")
    @Mapping(target="autor", ignore=true)
    Publicacion toDomain(PublicacionEntity entity);
    @Mapping(target="id", ignore=true)
    @Mapping(target="tipo", expression="java(domain.getTipo().name())")
    @Mapping(target="asignatura", ignore=true)
    @Mapping(target="autor", ignore=true)
    PublicacionEntity toEntity(Publicacion domain);
    List<Publicacion> toDomainList(List<PublicacionEntity> entities);
}
