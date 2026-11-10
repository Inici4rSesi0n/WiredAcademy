/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Aula;
import dominio.puerto.repositorio.RepositorioAulas;
import infraestructura.persistencia.entidades.AulaEntity;
import infraestructura.persistencia.jpa.AulaJpaRepository;
import infraestructura.persistencia.mappers.AulaMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioAulas implements RepositorioAulas{
    private final AulaJpaRepository jpaRepository;
    private final AulaMapper mapper;
    public AdaptadorRepositorioAulas(AulaJpaRepository jpaRepository, AulaMapper mapper){
        this.jpaRepository=jpaRepository;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(Aula aula){
        if(aula==null) throw new IllegalArgumentException("El aula no puede ser nula");
        jpaRepository.save(mapper.toEntity(aula));
    }
    @Override
    public Optional<Aula> buscarPorNombre(String nombre){
        return jpaRepository.findByNombre(nombre).map(mapper::toDomain);
    }
    @Override
    public List<Aula> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override @Transactional
    public void actualizar(Aula original, Aula actualizada){
        AulaEntity entity=jpaRepository.findByNombre(original.getNombre())
                .orElseThrow(()->new IllegalArgumentException("El aula no existe"));
        entity.setNombre(actualizada.getNombre());
        entity.setCapacidad(actualizada.getCapacidad());
        entity.setUbicacion(actualizada.getUbicacion());
        entity.setTipo(actualizada.getTipo());
        entity.setModalidad(actualizada.getModalidad().name());
        jpaRepository.save(entity);
    }
    @Override @Transactional
    public void eliminar(Aula aula){
        jpaRepository.findByNombre(aula.getNombre()).ifPresent(jpaRepository::delete);
    }
}
