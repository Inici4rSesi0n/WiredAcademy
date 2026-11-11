/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Subdirector;
import dominio.puerto.repositorio.RepositorioSubdirectores;
import infraestructura.persistencia.entidades.SubdirectorEntity;
import infraestructura.persistencia.jpa.SubdirectorJpaRepository;
import infraestructura.persistencia.mappers.SubdirectorMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioSubdirectores implements RepositorioSubdirectores{
    private final SubdirectorJpaRepository jpaRepository;
    private final SubdirectorMapper mapper;
    public AdaptadorRepositorioSubdirectores(SubdirectorJpaRepository jpaRepository, SubdirectorMapper mapper){
        this.jpaRepository=jpaRepository;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(Subdirector subdirector){
        if(subdirector==null) throw new IllegalArgumentException("El subdirector no puede ser nulo");
        jpaRepository.save(mapper.toEntity(subdirector));
    }
    @Override
    public Optional<Subdirector> buscarPorCodigo(String codigo){
        return jpaRepository.findByCodigo(codigo).map(mapper::toDomain);
    }
    @Override
    public List<Subdirector> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override @Transactional
    public void actualizar(Subdirector original, Subdirector actualizado){
        SubdirectorEntity entity=jpaRepository.findByCodigo(original.getCodigo())
                .orElseThrow(()->new IllegalArgumentException("El subdirector no existe"));
        entity.setNombre(actualizado.getNombre());
        entity.setApellido(actualizado.getApellido());
        entity.setEdad(actualizado.getEdad());
        entity.setHashContrasena(actualizado.getHashContrasena());
        entity.setDni(actualizado.getDni());
        jpaRepository.save(entity);
    }
    @Override @Transactional
    public void eliminar(Subdirector subdirector){
        jpaRepository.findByCodigo(subdirector.getCodigo()).ifPresent(jpaRepository::delete);
    }
}
