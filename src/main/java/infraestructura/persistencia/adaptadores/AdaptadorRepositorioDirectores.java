/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Director;
import dominio.puerto.repositorio.RepositorioDirectores;
import infraestructura.persistencia.entidades.DirectorEntity;
import infraestructura.persistencia.jpa.DirectorJpaRepository;
import infraestructura.persistencia.mappers.DirectorMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioDirectores implements RepositorioDirectores{
    private final DirectorJpaRepository jpaRepository;
    private final DirectorMapper mapper;
    public AdaptadorRepositorioDirectores(DirectorJpaRepository jpaRepository, DirectorMapper mapper){
        this.jpaRepository=jpaRepository;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(Director director){
        if(director==null) throw new IllegalArgumentException("El director no puede ser nulo");
        jpaRepository.save(mapper.toEntity(director));
    }
    @Override
    public Optional<Director> buscarPorCodigo(String codigo){
        return jpaRepository.findByCodigo(codigo).map(mapper::toDomain);
    }
    @Override
    public List<Director> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override @Transactional
    public void actualizar(Director original, Director actualizado){
        DirectorEntity entity=jpaRepository.findByCodigo(original.getCodigo())
                .orElseThrow(()->new IllegalArgumentException("El director no existe"));
        entity.setNombre(actualizado.getNombre());
        entity.setApellido(actualizado.getApellido());
        entity.setEdad(actualizado.getEdad());
        entity.setHashContrasena(actualizado.getHashContrasena());
        entity.setDni(actualizado.getDni());
        jpaRepository.save(entity);
    }
    @Override @Transactional
    public void eliminar(Director director){
        jpaRepository.findByCodigo(director.getCodigo()).ifPresent(jpaRepository::delete);
    }
}
