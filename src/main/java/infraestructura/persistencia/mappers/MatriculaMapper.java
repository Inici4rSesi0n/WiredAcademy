/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.EstadoMatricula;
import dominio.modelo.Estudiante;
import dominio.modelo.Grupo;
import dominio.modelo.InscripcionAsignatura;
import dominio.modelo.Matricula;
import dominio.modelo.PeriodoAcademico;
import infraestructura.persistencia.entidades.MatriculaAsignaturaEntity;
import infraestructura.persistencia.entidades.MatriculaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE,
        uses={EstudianteMapper.class, PeriodoAcademicoMapper.class, GrupoMapper.class, AsignaturaMapper.class})
public abstract class MatriculaMapper {
    @Autowired protected EstudianteMapper estudianteMapper;
    @Autowired protected PeriodoAcademicoMapper periodoMapper;
    @Autowired protected GrupoMapper grupoMapper;
    @Autowired protected AsignaturaMapper asignaturaMapper;
    public Matricula toDomain(MatriculaEntity entity) {
        if (entity == null) return null;
        Estudiante estudiante = estudianteMapper.toDomain(entity.getEstudiante());
        PeriodoAcademico periodo = periodoMapper.toDomain(entity.getPeriodo());
        EstadoMatricula estado = EstadoMatricula.valueOf(entity.getEstado());
        List<InscripcionAsignatura> inscripciones = new ArrayList<>();
        if (entity.getInscripciones() != null) {
            for (MatriculaAsignaturaEntity insEntity : entity.getInscripciones()) {
                inscripciones.add(new InscripcionAsignatura(
                        asignaturaMapper.toDomain(insEntity.getAsignatura()),
                        grupoMapper.toDomain(insEntity.getGrupo())
                ));
            }
        }
        if (!inscripciones.isEmpty()) {
            return new Matricula(estudiante, periodo, inscripciones, entity.getFecha(), estado);
        }
        Grupo grupo = (entity.getGrupo() != null) ? grupoMapper.toDomain(entity.getGrupo()) : null;
        if (grupo == null) {
            System.err.println("[MatriculaMapper] Matrícula id=" + entity.getId()
                    + " sin grupo ni inscripciones (registro inconsistente en BD). Omitiendo del listado.");
            return null;
        }
        return new Matricula(estudiante, periodo, grupo, new ArrayList<>(), entity.getFecha(), estado);
    }
    @Mapping(target="id", ignore=true)
    @Mapping(target="estudiante", ignore=true)
    @Mapping(target="periodo", ignore=true)
    @Mapping(target="grupo", ignore=true)
    @Mapping(target="inscripciones", ignore=true)
    @Mapping(target="estado", expression="java(domain.getEstado().name())")
    public abstract MatriculaEntity toEntity(Matricula domain);
    public List<Matricula> toDomainList(List<MatriculaEntity> entities) {
        return entities.stream().map(this::toDomain).filter(java.util.Objects::nonNull).toList();
    }
}
