/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Asignatura;
import dominio.modelo.Docente;
import dominio.modelo.Grupo;
import infraestructura.persistencia.entidades.DocenteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE, uses={AsignaturaMapper.class, GradoMapper.class})
public abstract class DocenteMapper{
    @Autowired protected AsignaturaMapper asignaturaMapper;
    @Autowired protected GradoMapper gradoMapper;
    public Docente toDomain(DocenteEntity entity) {
        if (entity == null) return null;
        List<Asignatura> asigs = entity.getAsignaturas().stream().map(asignaturaMapper::toDomain).toList();
        Docente docente = new Docente(
            entity.getCodigo(), entity.getHashContrasena(), entity.getDni(),
            entity.getNombre(), entity.getApellido(), entity.getEdad(),
            entity.getEspecialidad(), asigs
        );
        if (entity.getTutoria() != null) {
            Grupo tutoriaLight = new Grupo(
                entity.getTutoria().getNombre(),
                gradoMapper.toDomain(entity.getTutoria().getGrado())
            );
            docente.setTutoria(tutoriaLight);
        }
        return docente;
    }
    @Mapping(target="id", ignore=true)
    @Mapping(target="asignaturas", ignore=true)
    @Mapping(target="tutoria", ignore=true)
    public abstract DocenteEntity toEntity(Docente domain);
    public List<Docente> toDomainList(List<DocenteEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }
}
