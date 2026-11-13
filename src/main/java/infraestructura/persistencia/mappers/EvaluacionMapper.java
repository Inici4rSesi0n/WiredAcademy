/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Evaluacion;
import infraestructura.persistencia.entidades.EvaluacionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
@Mapper(componentModel="spring", uses={AsignaturaMapper.class})
public interface EvaluacionMapper {
    Evaluacion toDomain(EvaluacionEntity entity);
    @Mapping(target="id", ignore=true)
    EvaluacionEntity toEntity(Evaluacion domain);
    List<Evaluacion> toDomainList(List<EvaluacionEntity> entities);
}
