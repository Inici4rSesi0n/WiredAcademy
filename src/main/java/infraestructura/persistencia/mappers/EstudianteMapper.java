/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Estudiante;
import infraestructura.persistencia.entidades.EstudianteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring")
public interface EstudianteMapper{
    Estudiante toDomain(EstudianteEntity entity);
    @Mapping(target="id", ignore=true)
    EstudianteEntity toEntity(Estudiante domain);
    List<Estudiante> toDomainList(List<EstudianteEntity> entities);
}
