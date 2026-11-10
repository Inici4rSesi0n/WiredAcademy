/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Secretario;
import dominio.puerto.repositorio.RepositorioSecretarios;
import infraestructura.persistencia.entidades.SecretarioEntity;
import infraestructura.persistencia.jpa.SecretarioJpaRepository;
import infraestructura.persistencia.mappers.SecretarioMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioSecretarios implements RepositorioSecretarios{
    private final SecretarioJpaRepository jpaRepository;
    private final SecretarioMapper mapper;
    public AdaptadorRepositorioSecretarios(SecretarioJpaRepository jpaRepository, SecretarioMapper mapper){
        this.jpaRepository=jpaRepository;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(Secretario secretario){
        if(secretario==null) throw new IllegalArgumentException("El secretario no puede ser nulo");
        jpaRepository.save(mapper.toEntity(secretario));
    }
    @Override
    public Optional<Secretario> buscarPorCodigo(String codigo){
        return jpaRepository.findByCodigo(codigo).map(mapper::toDomain);
    }
    @Override
    public List<Secretario> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override @Transactional
    public void actualizar(Secretario original, Secretario actualizado){
        SecretarioEntity entity=jpaRepository.findByCodigo(original.getCodigo())
                .orElseThrow(()->new IllegalArgumentException("El secretario no existe"));
        entity.setNombre(actualizado.getNombre());
        entity.setApellido(actualizado.getApellido());
        entity.setEdad(actualizado.getEdad());
        entity.setHashContrasena(actualizado.getHashContrasena());
        entity.setDni(actualizado.getDni());
        jpaRepository.save(entity);
    }
    @Override @Transactional
    public void eliminar(Secretario secretario){
        jpaRepository.findByCodigo(secretario.getCodigo()).ifPresent(jpaRepository::delete);
    }
}
