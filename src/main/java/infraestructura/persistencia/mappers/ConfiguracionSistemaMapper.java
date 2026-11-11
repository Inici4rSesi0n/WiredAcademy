/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.configuracion.ConfiguracionSistema;
import dominio.modelo.configuracion.TipoInstitucion;
import infraestructura.persistencia.entidades.ConfiguracionSistemaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE)
public interface ConfiguracionSistemaMapper {
    default ConfiguracionSistema toDomain(ConfiguracionSistemaEntity entity) {
        if (entity == null) return null;
        return new ConfiguracionSistema(entity.getId(), TipoInstitucion.valueOf(entity.getTipoInstitucion()));
    }
    @Mapping(target="id", ignore=true)
    @Mapping(target="tipoInstitucion", expression="java(domain.getTipoInstitucion().name())")
    ConfiguracionSistemaEntity toEntity(ConfiguracionSistema domain);
}
