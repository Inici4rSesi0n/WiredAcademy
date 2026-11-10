/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Grado;
import infraestructura.persistencia.entidades.GradoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE)
public interface GradoMapper{
    Grado toDomain(GradoEntity entity);
    @Mapping(target="id", ignore=true)
    GradoEntity toEntity(Grado domain);
    List<Grado> toDomainList(List<GradoEntity> entities);
}
