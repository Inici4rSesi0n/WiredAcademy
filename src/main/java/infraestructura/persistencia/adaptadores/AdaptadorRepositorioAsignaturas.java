/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Asignatura;
import dominio.puerto.repositorio.RepositorioAsignaturas;
import infraestructura.persistencia.entidades.AsignaturaEntity;
import infraestructura.persistencia.jpa.AsignaturaJpaRepository;
import infraestructura.persistencia.mappers.AsignaturaMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioAsignaturas implements RepositorioAsignaturas{
    private final AsignaturaJpaRepository jpaRepository;
    private final AsignaturaMapper mapper;
    public AdaptadorRepositorioAsignaturas(AsignaturaJpaRepository jpaRepository, AsignaturaMapper mapper){
        this.jpaRepository=jpaRepository;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(Asignatura asignatura){
        if(asignatura==null) throw new IllegalArgumentException("La asignatura no puede ser nula");
        jpaRepository.save(mapper.toEntity(asignatura));
    }
    @Override
    public Optional<Asignatura> buscarPorNombre(String nombre){
        return jpaRepository.findByNombre(nombre).map(mapper::toDomain);
    }
    @Override
    public List<Asignatura> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override @Transactional
    public void actualizar(Asignatura original, Asignatura actualizada){
        AsignaturaEntity entity=jpaRepository.findByNombre(original.getNombre())
                .orElseThrow(()->new IllegalArgumentException("La asignatura no existe"));
        entity.setNombre(actualizada.getNombre());
        jpaRepository.save(entity);
    }
    @Override @Transactional
    public void eliminar(Asignatura asignatura){
        jpaRepository.findByNombre(asignatura.getNombre()).ifPresent(jpaRepository::delete);
    }
}
