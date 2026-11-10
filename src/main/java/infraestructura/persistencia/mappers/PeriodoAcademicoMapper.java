/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.PeriodoAcademico;
import infraestructura.persistencia.entidades.PeriodoAcademicoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring")
public interface PeriodoAcademicoMapper{
    PeriodoAcademico toDomain(PeriodoAcademicoEntity entity);
    @Mapping(target="id", ignore=true)
    PeriodoAcademicoEntity toEntity(PeriodoAcademico domain);
    List<PeriodoAcademico> toDomainList(List<PeriodoAcademicoEntity> entities);
}
