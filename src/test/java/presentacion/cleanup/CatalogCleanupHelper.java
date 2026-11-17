/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
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
public class CatalogCleanupHelper {
    private final ConfigurableApplicationContext ctx;
    private final String prefijo;
    private final String prefijoDni;
    public CatalogCleanupHelper(ConfigurableApplicationContext ctx) {
        this(ctx, "UITest-", "9999");
    }
    public CatalogCleanupHelper(ConfigurableApplicationContext ctx, String prefijo) {
        this(ctx, prefijo, prefijo.replace("-", ""));
    }
    public CatalogCleanupHelper(ConfigurableApplicationContext ctx, String prefijo, String prefijoDni) {
        this.ctx = ctx;
        this.prefijo = prefijo;
        this.prefijoDni = prefijoDni;
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
            jdbc.update("UPDATE usuarios SET grupo_tutoria_id = NULL WHERE grupo_tutoria_id IS NOT NULL AND (codigo LIKE ? OR dni LIKE ?)", prefijo + "%", prefijoDni + "%");
        } catch (Exception e) {
            System.err.println("[cleanup] romperCiclosFK fallo: " + e.getMessage());
        }
    }
    private boolean coincide(String valor) {
        return valor != null && valor.startsWith(prefijo);
    }
    private boolean coincideDni(String valor) {
        return valor != null && valor.startsWith(prefijoDni);
    }
    private void limpiarMatriculas() {
        try {
            JdbcTemplate jdbc = new JdbcTemplate(ctx.getBean(DataSource.class));
            jdbc.update("DELETE FROM matricula_asignaturas WHERE matricula_id IN (SELECT id FROM matriculas)");
            jdbc.update("DELETE FROM matriculas");
        } catch (Exception ignored) {
            try {
                JdbcTemplate jdbc = new JdbcTemplate(ctx.getBean(DataSource.class));
                jdbc.update("DELETE FROM matricula_asignaturas");
                jdbc.update("DELETE FROM matriculas");
            } catch (Exception ignored2) {}
        }
    }
    private void limpiarClases() {
        try {
            GestionClases gc = ctx.getBean(GestionClases.class);
            gc.listarClases().stream()
                .filter(c -> coincide(c.getGrupo().getNombre()) || coincide(c.getAula().getNombre()) || coincide(c.getDocente().getCodigo()) || coincide(c.getPeriodo().getNombre()) || coincide(c.getAsignatura().getNombre()))
                .forEach(gc::eliminarClase);
        } catch (Exception ignored) {}
    }
    private void limpiarRecreos() {
        try {
            GestionRecreos gr = ctx.getBean(GestionRecreos.class);
            gr.listarTodos().stream().filter(r -> coincide(r.getDescripcion())).forEach(gr::eliminarRecreo);
        } catch (Exception ignored) {}
    }
    private void limpiarGrupos() {
        try {
            JdbcTemplate jdbc = new JdbcTemplate(ctx.getBean(DataSource.class));
            jdbc.update("UPDATE matriculas SET grupo_id = NULL WHERE grupo_id IN (SELECT id FROM grupos WHERE nombre LIKE ?)", prefijo + "%");
            jdbc.update("DELETE FROM matricula_asignaturas WHERE grupo_id IN (SELECT id FROM grupos WHERE nombre LIKE ?)", prefijo + "%");
            jdbc.update("DELETE FROM grupo_estudiantes WHERE grupo_id IN (SELECT id FROM grupos WHERE nombre LIKE ?)", prefijo + "%");
            GestionGrupos ggr = ctx.getBean(GestionGrupos.class);
            ggr.listarTodos().stream().filter(g -> coincide(g.getNombre())).forEach(ggr::eliminarGrupo);
        } catch (Exception ignored) {
            try {
                JdbcTemplate jdbc = new JdbcTemplate(ctx.getBean(DataSource.class));
                jdbc.update("DELETE FROM grupos WHERE nombre LIKE ?", prefijo + "%");
            } catch (Exception ignored2) {}
        }
    }
    private void limpiarUsuarios() {
        try {
            GestionUsuarios gu = ctx.getBean(GestionUsuarios.class);
            gu.listarTodos().stream()
                .filter(u -> coincide(u.getCodigo()) || coincide(u.getNombre()) || coincideDni(u.getDni()))
                .forEach(gu::eliminarUsuario);
        } catch (Exception ignored) {}
    }
    private void limpiarGrados() {
        try {
            GestionGrados gg = ctx.getBean(GestionGrados.class);
            gg.listarTodos().stream().filter(g -> coincide(g.getNombre())).forEach(gg::eliminarGrado);
        } catch (Exception ignored) {
            try {
                JdbcTemplate jdbc = new JdbcTemplate(ctx.getBean(DataSource.class));
                jdbc.update("DELETE FROM grados WHERE nombre LIKE ?", prefijo + "%");
            } catch (Exception ignored2) {}
        }
    }
    private void limpiarPeriodos() {
        try {
            JdbcTemplate jdbc = new JdbcTemplate(ctx.getBean(DataSource.class));
            jdbc.update("UPDATE matriculas SET periodo_id = NULL WHERE periodo_id IN (SELECT id FROM periodos_academicos WHERE nombre LIKE ?)", prefijo + "%");
            jdbc.update("DELETE FROM recreo_periodos WHERE periodo_id IN (SELECT id FROM periodos_academicos WHERE nombre LIKE ?)", prefijo + "%");
            GestionPeriodos gp = ctx.getBean(GestionPeriodos.class);
            gp.listarTodos().stream().filter(p -> coincide(p.getNombre())).forEach(gp::eliminarPeriodo);
        } catch (Exception ignored) {
            try {
                JdbcTemplate jdbc = new JdbcTemplate(ctx.getBean(DataSource.class));
                jdbc.update("DELETE FROM periodos_academicos WHERE nombre LIKE ?", prefijo + "%");
            } catch (Exception ignored2) {}
        }
    }
    private void limpiarAulas() {
        try {
            GestionAulas ga = ctx.getBean(GestionAulas.class);
            ga.listarTodos().stream().filter(a -> coincide(a.getNombre())).forEach(ga::eliminarAula);
        } catch (Exception ignored) {}
    }
    private void limpiarAsignaturas() {
        try {
            GestionAsignaturas ga = ctx.getBean(GestionAsignaturas.class);
            ga.listarTodos().stream().filter(a -> coincide(a.getNombre())).forEach(ga::eliminarAsignatura);
        } catch (Exception ignored) {}
    }
    private void limpiarTurnos() {
        try {
            GestionTurnos gt = ctx.getBean(GestionTurnos.class);
            gt.listarTodos().stream().filter(t -> coincide(t.getNombre())).forEach(gt::eliminarTurno);
        } catch (Exception ignored) {}
    }
}
