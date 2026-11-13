/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Aula;
import infraestructura.persistencia.entidades.AulaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE)
public interface AulaMapper{
    Aula toDomain(AulaEntity entity);
    @Mapping(target="id", ignore=true)
    AulaEntity toEntity(Aula domain);
    List<Aula> toDomainList(List<AulaEntity> entities);
}
