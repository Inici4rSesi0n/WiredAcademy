/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.jpa;
import infraestructura.persistencia.entidades.EvaluacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface EvaluacionJpaRepository extends JpaRepository<EvaluacionEntity, Long>{
    List<EvaluacionEntity> findByAsignaturaNombreOrderByIdAsc(String nombreAsignatura);
}
