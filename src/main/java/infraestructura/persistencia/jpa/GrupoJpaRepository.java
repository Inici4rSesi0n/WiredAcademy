package infraestructura.persistencia.jpa;
import infraestructura.persistencia.entidades.GrupoEntity;
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
public interface GrupoJpaRepository extends JpaRepository<GrupoEntity, Long>{
    @Query("SELECT g FROM GrupoEntity g WHERE LOWER(g.nombre) = LOWER(:nombre)")
    Optional<GrupoEntity> findByNombre(@Param("nombre") String nombre);
    @Query("SELECT g FROM GrupoEntity g WHERE LOWER(g.nombre) = LOWER(:nombre) AND LOWER(g.grado.nombre) = LOWER(:gradoNombre)")
    List<GrupoEntity> findByNombreAndGradoNombre(@Param("nombre") String nombre, @Param("gradoNombre") String gradoNombre);
}
