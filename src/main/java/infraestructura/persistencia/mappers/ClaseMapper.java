/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.Clase;
import infraestructura.persistencia.entidades.ClaseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE,
        uses={PeriodoAcademicoMapper.class, AsignaturaMapper.class, DocenteMapper.class, GrupoMapper.class, AulaMapper.class})
public interface ClaseMapper{
    @Mapping(target="franja", expression="java(new dominio.modelo.FranjaHoraria(entity.getDiaSemana(), entity.getHoraInicio(), entity.getHoraFin()))")
    Clase toDomain(ClaseEntity entity);
    @Mapping(target="id", ignore=true)
    @Mapping(target="periodo", ignore=true)
    @Mapping(target="asignatura", ignore=true)
    @Mapping(target="docente", ignore=true)
    @Mapping(target="grupo", ignore=true)
    @Mapping(target="aula", ignore=true)
    @Mapping(target="diaSemana", source="franja.diaSemana")
    @Mapping(target="horaInicio", source="franja.horaInicio")
    @Mapping(target="horaFin", source="franja.horaFin")
    ClaseEntity toEntity(Clase domain);
    List<Clase> toDomainList(List<ClaseEntity> entities);
}
