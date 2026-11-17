/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.jpa;
import infraestructura.persistencia.entidades.MatriculaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface MatriculaJpaRepository extends JpaRepository<MatriculaEntity, Long> {
    @Query("SELECT DISTINCT m FROM MatriculaEntity m " +
           "LEFT JOIN FETCH m.estudiante " +
           "LEFT JOIN FETCH m.periodo " +
           "LEFT JOIN FETCH m.grupo g " +
           "LEFT JOIN FETCH g.grado " +
           "LEFT JOIN FETCH m.inscripciones i " +
           "LEFT JOIN FETCH i.asignatura " +
           "LEFT JOIN FETCH i.grupo gi " +
           "LEFT JOIN FETCH gi.grado " +
           "WHERE m.estudiante.codigo = :codigo AND m.periodo.nombre = :nombrePeriodo")
    Optional<MatriculaEntity> findByEstudianteCodigoAndPeriodoNombre(
        @Param("codigo") String codigo, @Param("nombrePeriodo") String nombrePeriodo);
    @Query("SELECT DISTINCT m FROM MatriculaEntity m " +
           "LEFT JOIN FETCH m.estudiante " +
           "LEFT JOIN FETCH m.periodo " +
           "LEFT JOIN FETCH m.grupo g " +
           "LEFT JOIN FETCH g.grado " +
           "LEFT JOIN FETCH m.inscripciones i " +
           "LEFT JOIN FETCH i.asignatura " +
           "LEFT JOIN FETCH i.grupo gi " +
           "LEFT JOIN FETCH gi.grado " +
           "WHERE m.grupo.nombre = :nombreGrupo")
    List<MatriculaEntity> findByGrupoNombreConInscripciones(@Param("nombreGrupo") String nombreGrupo);
    @Query("SELECT DISTINCT m FROM MatriculaEntity m " +
           "LEFT JOIN FETCH m.estudiante " +
           "LEFT JOIN FETCH m.periodo " +
           "LEFT JOIN FETCH m.grupo g " +
           "LEFT JOIN FETCH g.grado " +
           "LEFT JOIN FETCH m.inscripciones i " +
           "LEFT JOIN FETCH i.asignatura " +
           "LEFT JOIN FETCH i.grupo gi " +
           "LEFT JOIN FETCH gi.grado")
    List<MatriculaEntity> findAllConInscripciones();
}
