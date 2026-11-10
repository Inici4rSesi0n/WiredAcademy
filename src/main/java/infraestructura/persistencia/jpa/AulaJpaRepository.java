/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.jpa;
import infraestructura.persistencia.entidades.AulaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public interface AulaJpaRepository extends JpaRepository<AulaEntity, Long>{
    @Query("SELECT a FROM AulaEntity a WHERE LOWER(a.nombre) = LOWER(:nombre)")
    Optional<AulaEntity> findByNombre(@Param("nombre") String nombre);
}
