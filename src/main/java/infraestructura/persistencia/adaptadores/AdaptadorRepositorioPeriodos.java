/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.PeriodoAcademico;
import dominio.puerto.repositorio.RepositorioPeriodos;
import infraestructura.persistencia.entidades.PeriodoAcademicoEntity;
import infraestructura.persistencia.jpa.PeriodoJpaRepository;
import infraestructura.persistencia.mappers.PeriodoAcademicoMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioPeriodos implements RepositorioPeriodos{
    private final PeriodoJpaRepository jpaRepository;
    private final PeriodoAcademicoMapper mapper;
    public AdaptadorRepositorioPeriodos(PeriodoJpaRepository jpaRepository, PeriodoAcademicoMapper mapper){
        this.jpaRepository=jpaRepository;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(PeriodoAcademico periodo){
        if(periodo==null) throw new IllegalArgumentException("El periodo no puede ser nulo");
        jpaRepository.save(mapper.toEntity(periodo));
    }
    @Override
    public Optional<PeriodoAcademico> buscarPorNombre(String nombre){
        return jpaRepository.findByNombre(nombre).map(mapper::toDomain);
    }
    @Override
    public List<PeriodoAcademico> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override @Transactional
    public void actualizar(PeriodoAcademico original, PeriodoAcademico actualizado){
        PeriodoAcademicoEntity entity=jpaRepository.findByNombre(original.getNombre())
                .orElseThrow(()->new IllegalArgumentException("El periodo no existe"));
        entity.setNombre(actualizado.getNombre());
        entity.setFechaInicio(actualizado.getFechaInicio());
        entity.setFechaFin(actualizado.getFechaFin());
        entity.setEstado(actualizado.getEstado());
        jpaRepository.save(entity);
    }
    @Override @Transactional
    public void eliminar(PeriodoAcademico periodo){
        jpaRepository.findByNombre(periodo.getNombre()).ifPresent(jpaRepository::delete);
    }
}
