/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Psicologo;
import infraestructura.persistencia.entidades.PsicologoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring")
public interface PsicologoMapper{
    Psicologo toDomain(PsicologoEntity entity);
    @Mapping(target="id", ignore=true)
    PsicologoEntity toEntity(Psicologo domain);
    List<Psicologo> toDomainList(List<PsicologoEntity> entities);
}
