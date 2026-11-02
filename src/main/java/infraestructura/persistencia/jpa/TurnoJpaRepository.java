package infraestructura.persistencia.jpa;
import infraestructura.persistencia.entidades.TurnoEntity;
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
public interface TurnoJpaRepository extends JpaRepository<TurnoEntity, Long>{
    @Query("SELECT t FROM TurnoEntity t WHERE LOWER(t.nombre) = LOWER(:nombre)")
    Optional<TurnoEntity> findByNombre(@Param("nombre") String nombre);
}
