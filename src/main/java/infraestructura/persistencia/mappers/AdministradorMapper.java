/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Administrador;
import infraestructura.persistencia.entidades.AdministradorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring")
public interface AdministradorMapper{
    Administrador toDomain(AdministradorEntity entity);
    
    @Mapping(target="id", ignore=true)
    AdministradorEntity toEntity(Administrador domain);
    
    List<Administrador> toDomainList(List<AdministradorEntity> entities);
}
