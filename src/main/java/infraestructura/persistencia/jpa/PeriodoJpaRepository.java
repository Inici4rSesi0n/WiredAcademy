/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.jpa;
import infraestructura.persistencia.entidades.PeriodoAcademicoEntity;
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
public interface PeriodoJpaRepository extends JpaRepository<PeriodoAcademicoEntity, Long>{
    @Query("SELECT p FROM PeriodoAcademicoEntity p WHERE LOWER(p.nombre) = LOWER(:nombre)")
    Optional<PeriodoAcademicoEntity> findByNombre(@Param("nombre") String nombre);
}
