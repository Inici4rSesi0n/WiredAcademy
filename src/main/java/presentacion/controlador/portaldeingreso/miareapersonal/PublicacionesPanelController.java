/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso.miareapersonal;
import aplicacion.casosdeuso.portaldeingreso.publicaciones.GestionPublicaciones;
import dominio.modelo.*;
import dominio.puerto.externo.EventBusListener;
import dominio.puerto.externo.LoggerPort;
import infraestructura.eventos.EventBus;
import presentacion.estadousuario.SesionUsuario;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
@Component
@Scope("prototype")
public class PublicacionesPanelController implements Initializable, EventBusListener {
    private final GestionPublicaciones gestionPublicaciones;
    private final LoggerPort logger;
    private final EventBus eventBus;
    @FXML private VBox vboxPublicaciones;
    @FXML private Label lblVacio;
    private Asignatura asignaturaActual;
    private TipoPublicacion tipoActual;
    public PublicacionesPanelController(GestionPublicaciones gestionPublicaciones, LoggerPort logger, EventBus eventBus) {
        this.gestionPublicaciones = gestionPublicaciones;
        this.logger = logger;
        this.eventBus = eventBus;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventBus.suscribir(TipoEvento.PUBLICACIONES, this);
    }
    @Override
    public void onEvent(TipoEvento evento) {
        if (evento == TipoEvento.PUBLICACIONES && asignaturaActual != null) {
            cargarPublicaciones();
        }
    }
    public void configurar(Asignatura asignatura, TipoPublicacion tipo) {
        this.asignaturaActual = asignatura;
        this.tipoActual = tipo;
        cargarPublicaciones();
    }
    private void cargarPublicaciones() {
        vboxPublicaciones.getChildren().clear();
        List<Publicacion> publicaciones = gestionPublicaciones.listarPorAsignatura(asignaturaActual.getNombre())
                .stream()
                .filter(p -> p.getTipo() == tipoActual)
                .toList();
        if (publicaciones.isEmpty()) {
            lblVacio.setVisible(true);
            lblVacio.setManaged(true);
            return;
        }
        lblVacio.setVisible(false);
        lblVacio.setManaged(false);
        Map<Integer, List<Publicacion>> porSemana = publicaciones.stream()
                .collect(Collectors.groupingBy(p -> p.getNumeroSemana() != null ? p.getNumeroSemana() : 0));
        List<Integer> semanas = new ArrayList<>(porSemana.keySet());
        Collections.sort(semanas);
        if (tipoActual == TipoPublicacion.MATERIAL || tipoActual == TipoPublicacion.TAREA) {
            Accordion accordion = new Accordion();
            accordion.setStyle("-fx-background-color: transparent;");
            for (Integer semana : semanas) {
                String titulo = semana == 0 ? "General / Sin Semana" : "Semana " + String.format("%02d", semana);
                VBox contenido = new VBox(10);
                contenido.setStyle("-fx-padding: 10px; -fx-background-color: transparent;");
                for (Publicacion pub : porSemana.get(semana)) {
                    contenido.getChildren().add(crearTarjeta(pub));
                }
                TitledPane pane = new TitledPane(titulo, contenido);
                pane.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 14px; -fx-font-weight: bold;");
                accordion.getPanes().add(pane);
            }
            vboxPublicaciones.getChildren().add(accordion);
        } else {
            for (Publicacion pub : publicaciones) {
                vboxPublicaciones.getChildren().add(crearTarjeta(pub));
            }
        }
    }
    private VBox crearTarjeta(Publicacion pub) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: rgba(22, 26, 41, 0.8); -fx-background-radius: 10px; -fx-border-color: rgba(0, 210, 255, 0.2); -fx-border-radius: 10px; -fx-border-width: 1px; -fx-padding: 15px;");
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        SVGPath icon = new SVGPath();
        icon.setContent(getIconoPorTipo(tipoActual));
        icon.setStyle("-fx-fill: " + getColorPorTipo(tipoActual) + "; -fx-scale-x: 1.2; -fx-scale-y: 1.2;");
        Label lblTitulo = new Label(pub.getTitulo());
        lblTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #F8FAFC;");
        HBox.setHgrow(lblTitulo, Priority.ALWAYS);
        Label lblEstado = crearBadgeEstado(pub);
        header.getChildren().addAll(icon, lblTitulo, lblEstado);
        Label lblDesc = new Label(pub.getDescripcion() != null ? pub.getDescripcion() : "");
        lblDesc.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px;");
        lblDesc.setWrapText(true);
        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER_LEFT);
        if (pub.getFechaLimite() != null) {
            Label lblFechaLimite = new Label("Límite: " + pub.getFechaLimite().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            lblFechaLimite.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 11px;");
            footer.getChildren().add(lblFechaLimite);
        }
        if (pub.getUrlArchivo() != null && !pub.getUrlArchivo().isBlank()) {
            Label lblArchivo = new Label("📎 " + new java.io.File(pub.getUrlArchivo()).getName());
            lblArchivo.setStyle("-fx-text-fill: #00D2FF; -fx-font-size: 11px; -fx-cursor: hand;");
            footer.getChildren().add(lblArchivo);
        }
        card.getChildren().addAll(header, lblDesc, footer);
        return card;
    }
    private Label crearBadgeEstado(Publicacion pub) {
        Usuario usuario = SesionUsuario.getUsuarioActual();
        Label badge = new Label();
        if (usuario == null || usuario.getRol() == Usuario.Rol.DOCENTE) return badge;
        if (tipoActual == TipoPublicacion.TAREA || tipoActual == TipoPublicacion.EVALUACION) {
            badge.setText("Por entregar");
            badge.setStyle("-fx-background-color: rgba(245, 158, 11, 0.15); -fx-text-fill: #F59E0B; -fx-padding: 3px 10px; -fx-background-radius: 10px; -fx-font-size: 10px; -fx-font-weight: bold;");
        }
        return badge;
    }
    private String getIconoPorTipo(TipoPublicacion tipo) {
        return switch (tipo) {
            case MATERIAL -> "M18 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zM6 4h5v8l-2.5-1.5L6 12V4z";
            case TAREA -> "M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2z";
            case EVALUACION -> "M5 9.2h3V19H5V9.2zM10.6 5h2.8v14h-2.8V5zm5.6 8H19v6h-2.8v-6z";
            case FORO -> "M21 6h-2v9H6v2c0 .55.45 1 1 1h11l4 4V7c0-.55-.45-1-1-1z";
            case ANUNCIO -> "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2z";
        };
    }
    private String getColorPorTipo(TipoPublicacion tipo) {
        return switch (tipo) {
            case MATERIAL -> "#00D2FF";
            case TAREA -> "#F59E0B";
            case EVALUACION -> "#8B5CF6";
            case FORO -> "#10B981";
            case ANUNCIO -> "#EF4444";
        };
    }
}
