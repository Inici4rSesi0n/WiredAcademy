/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.jpa;
import infraestructura.persistencia.entidades.ClaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public interface ClaseJpaRepository extends JpaRepository<ClaseEntity, Long>{
    List<ClaseEntity> findByGrupoNombreAndGrupoGradoNombreOrderByDiaSemanaAscHoraInicioAsc(String nombreGrupo, String nombreGrado);
    Optional<ClaseEntity> findByGrupoGradoNombreAndGrupoNombreAndDiaSemanaAndHoraInicioAndHoraFin(
            String nombreGrado, String nombreGrupo, String diaSemana, String horaInicio, String horaFin);
    @Query("SELECT COUNT(c) > 0 FROM ClaseEntity c WHERE c.docente.codigo = :codigoDocente " +
           "AND c.diaSemana = :dia AND c.horaInicio < :horaFin AND c.horaFin > :horaInicio " +
           "AND (:excluirId IS NULL OR c.id <> :excluirId)")
    boolean existeConflictoDocente(@Param("codigoDocente") String codigoDocente, @Param("dia") String dia,
                                   @Param("horaInicio") String horaInicio, @Param("horaFin") String horaFin,
                                   @Param("excluirId") Long excluirId);
    @Query("SELECT COUNT(c) > 0 FROM ClaseEntity c WHERE c.aula.nombre = :nombreAula " +
           "AND c.diaSemana = :dia AND c.horaInicio < :horaFin AND c.horaFin > :horaInicio " +
           "AND (:excluirId IS NULL OR c.id <> :excluirId)")
    boolean existeConflictoAula(@Param("nombreAula") String nombreAula, @Param("dia") String dia,
                                @Param("horaInicio") String horaInicio, @Param("horaFin") String horaFin,
                                @Param("excluirId") Long excluirId);
    @Query("SELECT COUNT(c) > 0 FROM ClaseEntity c WHERE c.grupo.nombre = :nombreGrupo AND c.grupo.grado.nombre = :nombreGrado " +
           "AND c.diaSemana = :dia AND c.horaInicio < :horaFin AND c.horaFin > :horaInicio " +
           "AND (:excluirId IS NULL OR c.id <> :excluirId)")
    boolean existeConflictoGrupo(@Param("nombreGrupo") String nombreGrupo, @Param("nombreGrado") String nombreGrado,
                                 @Param("dia") String dia, @Param("horaInicio") String horaInicio,
                                 @Param("horaFin") String horaFin, @Param("excluirId") Long excluirId);
}
