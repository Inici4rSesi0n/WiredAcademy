/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Recreo;
import dominio.puerto.repositorio.RepositorioRecreos;
import infraestructura.persistencia.entidades.PeriodoAcademicoEntity;
import infraestructura.persistencia.entidades.RecreoEntity;
import infraestructura.persistencia.jpa.PeriodoJpaRepository;
import infraestructura.persistencia.jpa.RecreoJpaRepository;
import infraestructura.persistencia.mappers.RecreoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioRecreos implements RepositorioRecreos{
    private final RecreoJpaRepository jpaRepository;
    private final PeriodoJpaRepository periodoJpaRepository;
    private final RecreoMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(AdaptadorRepositorioRecreos.class);
    public AdaptadorRepositorioRecreos(RecreoJpaRepository jpaRepository, PeriodoJpaRepository periodoJpaRepository, RecreoMapper mapper){
        this.jpaRepository=jpaRepository;
        this.periodoJpaRepository=periodoJpaRepository;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(Recreo recreo){
        if(recreo==null) throw new IllegalArgumentException("El recreo no puede ser nulo");
        RecreoEntity entity = mapper.toEntity(recreo);
        entity.setPeriodos(resolverPeriodos(recreo));
        jpaRepository.saveAndFlush(entity);
    }
    @Override
    public Optional<Recreo> buscarPorDescripcion(String descripcion){
        List<RecreoEntity> candidatos = jpaRepository.findByDescripcion(descripcion);
        if (candidatos.isEmpty()) return Optional.empty();
        if (candidatos.size() > 1) {
            log.warn("Multiples recreos con descripcion '{}' ({}), usando el primero (id={})",
                    descripcion, candidatos.size(), candidatos.get(0).getId());
        }
        return Optional.of(mapper.toDomain(candidatos.get(0)));
    }
    @Override
    public List<Recreo> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override @Transactional
    public void actualizar(Recreo original, Recreo actualizada){
        RecreoEntity entity = buscarUnicoPorCombinacion(
                original.getDescripcion(), original.getDiaSemana(),
                original.getHoraInicio(), original.getHoraFin())
                .orElseThrow(()->new IllegalArgumentException("El recreo no existe"));
        entity.setDiaSemana(actualizada.getDiaSemana());
        entity.setHoraInicio(actualizada.getHoraInicio());
        entity.setHoraFin(actualizada.getHoraFin());
        entity.setDescripcion(actualizada.getDescripcion());
        entity.setPeriodos(resolverPeriodos(actualizada));
        jpaRepository.saveAndFlush(entity);
    }
    private Optional<RecreoEntity> buscarUnicoPorCombinacion(String descripcion, String diaSemana, String horaInicio, String horaFin) {
        List<RecreoEntity> candidatos = jpaRepository.findByDescripcionAndDiaSemanaAndHoraInicioAndHoraFin(
                descripcion, diaSemana, horaInicio, horaFin);
        if (candidatos.isEmpty()) return Optional.empty();
        if (candidatos.size() > 1) {
            log.warn("Multiples recreos con misma descripcion+dia+hora ({}), usando el primero (id={})",
                    candidatos.size(), candidatos.get(0).getId());
        }
        return Optional.of(candidatos.get(0));
    }
    @Override @Transactional
    public void eliminar(Recreo recreo){
        buscarUnicoPorCombinacion(
                recreo.getDescripcion(), recreo.getDiaSemana(),
                recreo.getHoraInicio(), recreo.getHoraFin())
                .ifPresent(jpaRepository::delete);
    }
    private List<PeriodoAcademicoEntity> resolverPeriodos(Recreo recreo){
        return recreo.getPeriodos().stream()
                .map(p -> periodoJpaRepository.findByNombre(p.getNombre())
                        .orElseThrow(()->new IllegalArgumentException("Periodo no encontrado: " + p.getNombre())))
                .collect(java.util.stream.Collectors.toList());
    }
}
