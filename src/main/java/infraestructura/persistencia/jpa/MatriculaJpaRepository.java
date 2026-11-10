/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.jpa;
import infraestructura.persistencia.entidades.MatriculaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public interface MatriculaJpaRepository extends JpaRepository<MatriculaEntity, Long>{
    Optional<MatriculaEntity> findByEstudianteCodigoAndPeriodoNombre(String codigoEstudiante, String periodoNombre);
    List<MatriculaEntity> findByGrupoNombre(String grupoNombre);
}
