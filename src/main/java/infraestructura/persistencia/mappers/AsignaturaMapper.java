/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Asignatura;
import infraestructura.persistencia.entidades.AsignaturaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE)
public interface AsignaturaMapper{
    Asignatura toDomain(AsignaturaEntity entity);
    
    @Mapping(target="id", ignore=true)
    AsignaturaEntity toEntity(Asignatura domain);
    
    List<Asignatura> toDomainList(List<AsignaturaEntity> entities);
}
