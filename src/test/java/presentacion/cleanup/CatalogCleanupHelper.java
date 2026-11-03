package presentacion.cleanup;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAulas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionClases;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionRecreos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionTurnos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionUsuarios;
import aplicacion.casosdeuso.portaldeingreso.gestiondetramites.GestionMatriculas;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
/**
 * Helper centralizado para limpiar datos de UI tests por prefijo.
 * Orden FK: hoja -> raiz. Rompe ciclos (grupos <-> usuarios) via JdbcTemplate
 * (auto-commit, sin @Transactional) para no depender de proxy de Spring.
 * Uso: new CatalogCleanupHelper(springContext).limpiarTodo();
 *      new CatalogCleanupHelper(springContext, "IT-").limpiarTodo();
 *
 * @author inici4rsesi0n
 */
public class CatalogCleanupHelper {
    private final ConfigurableApplicationContext ctx;
    private final String prefijo;
    public CatalogCleanupHelper(ConfigurableApplicationContext ctx) {
        this(ctx, "UITest-");
    }
    public CatalogCleanupHelper(ConfigurableApplicationContext ctx, String prefijo) {
        this.ctx = ctx;
        this.prefijo = prefijo;
    }
    public void limpiarTodo() {
        romperCiclosFK();
        limpiarMatriculas();
        limpiarClases();
        limpiarRecreos();
        limpiarGrupos();
        limpiarUsuarios();
        limpiarGrados();
        limpiarPeriodos();
        limpiarAulas();
        limpiarAsignaturas();
        limpiarTurnos();
    }
    private void romperCiclosFK() {
        try {
            JdbcTemplate jdbc = new JdbcTemplate(ctx.getBean(DataSource.class));
            jdbc.update("UPDATE grupos SET tutor_id = NULL WHERE tutor_id IS NOT NULL AND nombre LIKE ?", prefijo + "%");
            jdbc.update("UPDATE usuarios SET grupo_tutoria_id = NULL WHERE grupo_tutoria_id IS NOT NULL AND codigo LIKE ?", prefijo + "%");
        } catch (Exception e) {
            System.err.println("[cleanup] romperCiclosFK fallo: " + e.getMessage());
        }
    }
    private boolean coincide(String valor) {
        return valor != null && valor.startsWith(prefijo);
    }
    private void limpiarMatriculas() {
        try {
            GestionMatriculas gm = ctx.getBean(GestionMatriculas.class);
            gm.listarTodas().stream()
                .filter(m -> coincide(m.getEstudiante().getCodigo())
                          || coincide(m.getPeriodo().getNombre())
                          || coincide(m.getGrupo().getNombre()))
                .forEach(gm::eliminarMatricula);
        } catch (Exception ignored) {}
    }
    private void limpiarClases() {
        try {
            GestionClases gc = ctx.getBean(GestionClases.class);
            gc.listarClases().stream()
                .filter(c -> coincide(c.getGrupo().getNombre())
                          || coincide(c.getAula().getNombre())
                          || coincide(c.getDocente().getCodigo())
                          || coincide(c.getPeriodo().getNombre())
                          || coincide(c.getAsignatura().getNombre()))
                .forEach(gc::eliminarClase);
        } catch (Exception ignored) {}
    }
    private void limpiarRecreos() {
        try {
            GestionRecreos gr = ctx.getBean(GestionRecreos.class);
            gr.listarTodos().stream()
                .filter(r -> coincide(r.getDescripcion()))
                .forEach(gr::eliminarRecreo);
        } catch (Exception ignored) {}
    }
    private void limpiarGrupos() {
        try {
            GestionGrupos ggr = ctx.getBean(GestionGrupos.class);
            ggr.listarTodos().stream()
                .filter(g -> coincide(g.getNombre()))
                .forEach(ggr::eliminarGrupo);
        } catch (Exception ignored) {}
    }
    private void limpiarUsuarios() {
        try {
            GestionUsuarios gu = ctx.getBean(GestionUsuarios.class);
            gu.listarTodos().stream()
                .filter(u -> coincide(u.getCodigo()) || coincide(u.getNombre()))
                .forEach(gu::eliminarUsuario);
        } catch (Exception ignored) {}
    }
    private void limpiarGrados() {
        try {
            GestionGrados gg = ctx.getBean(GestionGrados.class);
            gg.listarTodos().stream()
                .filter(g -> coincide(g.getNombre()))
                .forEach(gg::eliminarGrado);
        } catch (Exception ignored) {}
    }
    private void limpiarPeriodos() {
        try {
            GestionPeriodos gp = ctx.getBean(GestionPeriodos.class);
            gp.listarTodos().stream()
                .filter(p -> coincide(p.getNombre()))
                .forEach(gp::eliminarPeriodo);
        } catch (Exception ignored) {}
    }
    private void limpiarAulas() {
        try {
            GestionAulas ga = ctx.getBean(GestionAulas.class);
            ga.listarTodos().stream()
                .filter(a -> coincide(a.getNombre()))
                .forEach(ga::eliminarAula);
        } catch (Exception ignored) {}
    }
    private void limpiarAsignaturas() {
        try {
            GestionAsignaturas ga = ctx.getBean(GestionAsignaturas.class);
            ga.listarTodos().stream()
                .filter(a -> coincide(a.getNombre()))
                .forEach(ga::eliminarAsignatura);
        } catch (Exception ignored) {}
    }
    private void limpiarTurnos() {
        try {
            GestionTurnos gt = ctx.getBean(GestionTurnos.class);
            gt.listarTodos().stream()
                .filter(t -> coincide(t.getNombre()))
                .forEach(gt::eliminarTurno);
        } catch (Exception ignored) {}
    }
}
