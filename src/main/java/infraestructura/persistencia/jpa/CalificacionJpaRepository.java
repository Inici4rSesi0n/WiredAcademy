/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.jpa;
import infraestructura.persistencia.entidades.CalificacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface CalificacionJpaRepository extends JpaRepository<CalificacionEntity, Long>{
    List<CalificacionEntity> findByEstudianteCodigo(String codigoEstudiante);
    List<CalificacionEntity> findByEvaluacionId(Long idEvaluacion);
    Optional<CalificacionEntity> findByEvaluacionIdAndEstudianteCodigo(Long idEvaluacion, String codigoEstudiante);
}
