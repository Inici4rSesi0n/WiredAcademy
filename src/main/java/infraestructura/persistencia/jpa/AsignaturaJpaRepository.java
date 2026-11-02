package infraestructura.persistencia.jpa;
import infraestructura.persistencia.entidades.AsignaturaEntity;
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
public interface AsignaturaJpaRepository extends JpaRepository<AsignaturaEntity, Long>{
    @Query("SELECT a FROM AsignaturaEntity a WHERE LOWER(a.nombre) = LOWER(:nombre)")
    Optional<AsignaturaEntity> findByNombre(@Param("nombre") String nombre);
}
