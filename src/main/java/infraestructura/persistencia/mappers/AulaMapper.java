/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Aula;
import dominio.modelo.ModalidadAula;
import infraestructura.persistencia.entidades.AulaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE)
public interface AulaMapper{
    default Aula toDomain(AulaEntity entity){
        if(entity==null) return null;
        return new Aula(
            entity.getNombre(),
            entity.getCapacidad(),
            entity.getUbicacion(),
            entity.getTipo(),
            ModalidadAula.valueOf(entity.getModalidad())
        );
    }
    @Mapping(target="id", ignore=true)
    @Mapping(target="modalidad", expression="java(domain.getModalidad().name())")
    AulaEntity toEntity(Aula domain);
    List<Aula> toDomainList(List<AulaEntity> entities);
}
