/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Secretario;
import infraestructura.persistencia.entidades.SecretarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring")
public interface SecretarioMapper{
    Secretario toDomain(SecretarioEntity entity);
    
    @Mapping(target="id", ignore=true)
    SecretarioEntity toEntity(Secretario domain);
    
    List<Secretario> toDomainList(List<SecretarioEntity> entities);
}
