/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Grupo;
import infraestructura.persistencia.entidades.GrupoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring", uses={GradoMapper.class, DocenteMapper.class}, unmappedTargetPolicy=ReportingPolicy.IGNORE)
public abstract class GrupoMapper{
    @Autowired
    protected GradoMapper gradoMapper;
    @Autowired
    protected DocenteMapper docenteMapper;
    public Grupo toDomain(GrupoEntity entity) {
        if (entity == null) return null;
        Grupo grupo = new Grupo(entity.getNombre(), gradoMapper.toDomain(entity.getGrado()));
        if (entity.getTutor() != null) {
            grupo.asignarTutor(docenteMapper.toDomain(entity.getTutor()));
        }
        return grupo;
    }
    @Mapping(target="id", ignore=true)
    @Mapping(target="grado", ignore=true)
    @Mapping(target="tutor", ignore=true)
    @Mapping(target="estudiantes", ignore=true)
    public abstract GrupoEntity toEntity(Grupo domain);
    public List<Grupo> toDomainList(List<GrupoEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }
}
