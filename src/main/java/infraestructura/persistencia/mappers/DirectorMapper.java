/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Director;
import infraestructura.persistencia.entidades.DirectorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring")
public interface DirectorMapper{
    Director toDomain(DirectorEntity entity);
    @Mapping(target="id", ignore=true)
    DirectorEntity toEntity(Director domain);
    List<Director> toDomainList(List<DirectorEntity> entities);
}
