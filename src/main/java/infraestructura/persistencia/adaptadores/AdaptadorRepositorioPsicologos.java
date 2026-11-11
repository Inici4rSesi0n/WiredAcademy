/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Psicologo;
import dominio.puerto.repositorio.RepositorioPsicologos;
import infraestructura.persistencia.entidades.PsicologoEntity;
import infraestructura.persistencia.jpa.PsicologoJpaRepository;
import infraestructura.persistencia.mappers.PsicologoMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioPsicologos implements RepositorioPsicologos{
    private final PsicologoJpaRepository jpaRepository;
    private final PsicologoMapper mapper;
    public AdaptadorRepositorioPsicologos(PsicologoJpaRepository jpaRepository, PsicologoMapper mapper){
        this.jpaRepository=jpaRepository;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(Psicologo psicologo){
        if(psicologo==null) throw new IllegalArgumentException("El psicologo no puede ser nulo");
        jpaRepository.save(mapper.toEntity(psicologo));
    }
    @Override
    public Optional<Psicologo> buscarPorCodigo(String codigo){
        return jpaRepository.findByCodigo(codigo).map(mapper::toDomain);
    }
    @Override
    public List<Psicologo> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override @Transactional
    public void actualizar(Psicologo original, Psicologo actualizado){
        PsicologoEntity entity=jpaRepository.findByCodigo(original.getCodigo())
                .orElseThrow(()->new IllegalArgumentException("El psicologo no existe"));
        entity.setNombre(actualizado.getNombre());
        entity.setApellido(actualizado.getApellido());
        entity.setEdad(actualizado.getEdad());
        entity.setHashContrasena(actualizado.getHashContrasena());
        entity.setDni(actualizado.getDni());
        jpaRepository.save(entity);
    }
    @Override @Transactional
    public void eliminar(Psicologo psicologo){
        jpaRepository.findByCodigo(psicologo.getCodigo()).ifPresent(jpaRepository::delete);
    }
}
