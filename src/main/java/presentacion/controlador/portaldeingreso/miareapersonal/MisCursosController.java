/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso.miareapersonal;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import aplicacion.casosdeuso.portaldeingreso.gestiondetramites.GestionMatriculas;
import dominio.modelo.*;
import dominio.puerto.externo.LoggerPort;
import infraestructura.configuracion.SpringFxmlLoader;
import presentacion.dto.CursoItem;
import presentacion.estadousuario.SesionUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
@Component
@Scope("prototype")
public class MisCursosController implements Initializable {
    private final GestionDocentes gestionDocentes;
    private final GestionMatriculas gestionMatriculas;
    private final SpringFxmlLoader fxmlLoader;
    private final LoggerPort logger;
    @FXML private FlowPane flowPaneCursos;
    public MisCursosController(GestionDocentes gestionDocentes, GestionMatriculas gestionMatriculas, SpringFxmlLoader fxmlLoader, LoggerPort logger) {
        this.gestionDocentes = gestionDocentes;
        this.gestionMatriculas = gestionMatriculas;
        this.fxmlLoader = fxmlLoader;
        this.logger = logger;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Usuario usuario = SesionUsuario.getUsuarioActual();
        if (usuario == null) {
            logger.warn("op=READ entity=CURSO | error=usuario_nulo_en_sesion");
            return;
        }
        logger.info("op=READ entity=CURSO | inicio_carga rol=" + usuario.getRol() + " codigo=" + usuario.getCodigo());
        List<CursoItem> cursos = new ArrayList<>();
        if (usuario.getRol() == Usuario.Rol.DOCENTE) {
            Docente docente = gestionDocentes.buscarPorCodigo(usuario.getCodigo());
            if (docente != null) {
                for (Asignatura a : docente.getAsignaturas()) {
                    cursos.add(new CursoItem(a.getNombre(), docente.getNombreCompleto(), "Ver secciones", "Varias", a));
                }
            }
        } else if (usuario.getRol() == Usuario.Rol.ESTUDIANTE) {
            Matricula matricula = gestionMatriculas.buscarMatricula(usuario.getCodigo(), "2026-I");
            if (matricula != null) {
                for (InscripcionAsignatura ins : matricula.getInscripciones()) {
                    String grupoNombre = ins.getGrupo() != null ? ins.getGrupo().getNombre() : "S/G";
                    cursos.add(new CursoItem(ins.getAsignatura().getNombre(), "Docente Asignado", grupoNombre, "Aula Asignada", ins.getAsignatura()));
                }
            }
        }
        logger.info("op=READ entity=CURSO | fin_carga cursos_encontrados=" + cursos.size());
        for (CursoItem c : cursos) {
            flowPaneCursos.getChildren().add(crearTarjetaCurso(c));
        }
    }
    private VBox crearTarjetaCurso(CursoItem curso) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setMinWidth(300);
        card.setStyle("-fx-background-color: rgba(22, 26, 41, 0.8); -fx-background-radius: 12px; -fx-border-color: rgba(0, 210, 255, 0.2); -fx-border-radius: 12px; -fx-border-width: 1px; -fx-padding: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 5); -fx-cursor: hand;");
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        SVGPath icon = new SVGPath();
        icon.setContent("M18 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zM6 4h5v8l-2.5-1.5L6 12V4z");
        icon.setStyle("-fx-fill: #00D2FF; -fx-scale-x: 1.2; -fx-scale-y: 1.2;");
        Label lblAsig = new Label(curso.getNombreAsignatura());
        lblAsig.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #F8FAFC;");
        lblAsig.setWrapText(true);
        HBox.setHgrow(lblAsig, Priority.ALWAYS);
        header.getChildren().addAll(icon, lblAsig);
        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.1);");
        Label lblDetalle = new Label("Grupo: " + curso.getNombreGrupo() + "\nDocente: " + curso.getNombreDocente());
        lblDetalle.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-line-spacing: 3px;");
        card.getChildren().addAll(header, sep, lblDetalle);
        card.setOnMouseClicked(e -> abrirDetalleCurso(curso));
        return card;
    }
    private void abrirDetalleCurso(CursoItem curso) {
        Usuario usuario = SesionUsuario.getUsuarioActual();
        try {
            if (usuario != null && usuario.getRol() == Usuario.Rol.DOCENTE) {
                logger.debug("op=NAVIGATE entity=CURSO | destino=secciones curso=" + curso.getNombreAsignatura());
                FXMLLoader loader = fxmlLoader.crearLoader("/fxml/portaldeingreso/miareapersonal/secciones.fxml");
                Parent root = loader.load();
                SeccionesController controller = loader.getController();
                controller.setAsignatura(curso.getAsignatura());
                BorderPane mainRoot = (BorderPane) flowPaneCursos.getScene().getRoot();
                mainRoot.setCenter(root);
            } else {
                logger.debug("op=NAVIGATE entity=CURSO | destino=detalle curso=" + curso.getNombreAsignatura());
                FXMLLoader loader = fxmlLoader.crearLoader("/fxml/portaldeingreso/miareapersonal/curso_detalle.fxml");
                Parent root = loader.load();
                CursoDetalleController controller = loader.getController();
                controller.setAsignatura(curso.getAsignatura());
                BorderPane mainRoot = (BorderPane) flowPaneCursos.getScene().getRoot();
                mainRoot.setCenter(root);
            }
        } catch (Exception e) {
            logger.error("Error al abrir curso: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
