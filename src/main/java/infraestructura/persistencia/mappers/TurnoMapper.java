/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Turno;
import infraestructura.persistencia.entidades.TurnoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE)
public interface TurnoMapper{
    Turno toDomain(TurnoEntity entity);
    @Mapping(target="id", ignore=true)
    TurnoEntity toEntity(Turno domain);
    List<Turno> toDomainList(List<TurnoEntity> entities);
}
