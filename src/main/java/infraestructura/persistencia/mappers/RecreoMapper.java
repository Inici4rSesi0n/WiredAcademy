/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.mappers;
import dominio.modelo.FranjaHoraria;
import dominio.modelo.PeriodoAcademico;
import dominio.modelo.Recreo;
import infraestructura.persistencia.entidades.PeriodoAcademicoEntity;
import infraestructura.persistencia.entidades.RecreoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE, uses={PeriodoAcademicoMapper.class})
public interface RecreoMapper{
    default Recreo toDomain(RecreoEntity entity){
        if(entity==null) return null;
        FranjaHoraria franja=new FranjaHoraria(entity.getDiaSemana(), entity.getHoraInicio(), entity.getHoraFin());
        List<PeriodoAcademico> periodos=entity.getPeriodos().stream()
                .map(p->new PeriodoAcademico(p.getNombre(), p.getFechaInicio(), p.getFechaFin(), p.getEstado()))
                .toList();
        return new Recreo(franja, entity.getDescripcion(), periodos);
    }
    @Mapping(target="id", ignore=true)
    @Mapping(target="periodos", ignore=true)
    @Mapping(target="diaSemana", source="franja.diaSemana")
    @Mapping(target="horaInicio", source="franja.horaInicio")
    @Mapping(target="horaFin", source="franja.horaFin")
    RecreoEntity toEntity(Recreo domain);
    default List<Recreo> toDomainList(List<RecreoEntity> entities){
        return entities.stream().map(this::toDomain).toList();
    }
}
