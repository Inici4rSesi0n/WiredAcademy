/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Tesorero;
import infraestructura.persistencia.entidades.TesoreroEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring")
public interface TesoreroMapper{
    Tesorero toDomain(TesoreroEntity entity);
    @Mapping(target="id", ignore=true)
    TesoreroEntity toEntity(Tesorero domain);
    List<Tesorero> toDomainList(List<TesoreroEntity> entities);
}
