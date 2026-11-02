package infraestructura.persistencia.jpa;
import infraestructura.persistencia.entidades.GradoEntity;
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
public interface GradoJpaRepository extends JpaRepository<GradoEntity, Long>{
    @Query("SELECT g FROM GradoEntity g WHERE LOWER(g.nombre) = LOWER(:nombre)")
    Optional<GradoEntity> findByNombre(@Param("nombre") String nombre);
    @Query("SELECT g FROM GradoEntity g WHERE LOWER(g.nombre) = LOWER(:nombre) AND LOWER(g.nivel) = LOWER(:nivel)")
    Optional<GradoEntity> findByNombreAndNivel(@Param("nombre") String nombre, @Param("nivel") String nivel);
}
