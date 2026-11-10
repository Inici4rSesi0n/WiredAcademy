/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Turno;
import dominio.puerto.repositorio.RepositorioTurnos;
import infraestructura.persistencia.entidades.TurnoEntity;
import infraestructura.persistencia.jpa.TurnoJpaRepository;
import infraestructura.persistencia.mappers.TurnoMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioTurnos implements RepositorioTurnos{
    private final TurnoJpaRepository jpaRepository;
    private final TurnoMapper mapper;
    public AdaptadorRepositorioTurnos(TurnoJpaRepository jpaRepository, TurnoMapper mapper){
        this.jpaRepository=jpaRepository;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(Turno turno){
        if(turno==null) throw new IllegalArgumentException("El turno no puede ser nulo");
        jpaRepository.save(mapper.toEntity(turno));
    }
    @Override
    public Optional<Turno> buscarPorNombre(String nombre){
        return jpaRepository.findByNombre(nombre).map(mapper::toDomain);
    }
    @Override
    public List<Turno> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override @Transactional
    public void actualizar(Turno original, Turno actualizado){
        TurnoEntity entity=jpaRepository.findByNombre(original.getNombre())
                .orElseThrow(()->new IllegalArgumentException("El turno no existe"));
        entity.setNombre(actualizado.getNombre());
        jpaRepository.save(entity);
    }
    @Override @Transactional
    public void eliminar(Turno turno){
        jpaRepository.findByNombre(turno.getNombre()).ifPresent(jpaRepository::delete);
    }
}
