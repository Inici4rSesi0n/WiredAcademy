/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Subdirector;
import infraestructura.persistencia.entidades.SubdirectorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring")
public interface SubdirectorMapper{
    Subdirector toDomain(SubdirectorEntity entity);
    @Mapping(target="id", ignore=true)
    SubdirectorEntity toEntity(Subdirector domain);
    List<Subdirector> toDomainList(List<SubdirectorEntity> entities);
}
