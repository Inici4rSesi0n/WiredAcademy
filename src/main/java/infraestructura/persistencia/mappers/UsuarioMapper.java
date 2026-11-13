/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.*;
import infraestructura.persistencia.entidades.*;
import org.springframework.stereotype.Component;
/**
 * Mapper manual para convertir UsuarioEntity a Usuario (clase abstracta).
 * Necesario porque MapStruct no puede instanciar la clase abstracta Usuario.
 *
 * @author inici4rsesi0n
 */
@Component
public class UsuarioMapper {
    private final AdministradorMapper adminMapper;
    private final DirectorMapper dirMapper;
    private final SubdirectorMapper subMapper;
    private final DocenteMapper docMapper;
    private final PsicologoMapper psiMapper;
    private final EstudianteMapper estMapper;
    private final SecretarioMapper secMapper;
    private final TesoreroMapper tesMapper;
    private final CoordinadorMapper coordMapper;
    private final PadreMapper padreMapper;
    public UsuarioMapper(AdministradorMapper adminMapper, DirectorMapper dirMapper, SubdirectorMapper subMapper,
                         DocenteMapper docMapper, PsicologoMapper psiMapper, EstudianteMapper estMapper,
                         SecretarioMapper secMapper, TesoreroMapper tesMapper, CoordinadorMapper coordMapper,
                         PadreMapper padreMapper) {
        this.adminMapper = adminMapper;
        this.dirMapper = dirMapper;
        this.subMapper = subMapper;
        this.docMapper = docMapper;
        this.psiMapper = psiMapper;
        this.estMapper = estMapper;
        this.secMapper = secMapper;
        this.tesMapper = tesMapper;
        this.coordMapper = coordMapper;
        this.padreMapper = padreMapper;
    }
    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;
        String rol = entity.getClass().getAnnotation(jakarta.persistence.DiscriminatorValue.class).value();
        return switch (rol) {
            case "ADMINISTRADOR" -> adminMapper.toDomain((AdministradorEntity) entity);
            case "DIRECTOR" -> dirMapper.toDomain((DirectorEntity) entity);
            case "SUBDIRECTOR" -> subMapper.toDomain((SubdirectorEntity) entity);
            case "DOCENTE" -> docMapper.toDomain((DocenteEntity) entity);
            case "PSICOLOGO" -> psiMapper.toDomain((PsicologoEntity) entity);
            case "ESTUDIANTE" -> estMapper.toDomain((EstudianteEntity) entity);
            case "SECRETARIO" -> secMapper.toDomain((SecretarioEntity) entity);
            case "TESORERO" -> tesMapper.toDomain((TesoreroEntity) entity);
            case "COORDINADOR" -> coordMapper.toDomain((CoordinadorEntity) entity);
            case "PADRE" -> padreMapper.toDomain((PadreEntity) entity);
            default -> throw new IllegalArgumentException("Rol desconocido: " + rol);
        };
    }
}
