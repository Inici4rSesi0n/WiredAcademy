/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso.administraciondelsistema.gestionacademica.horarios;
import infraestructura.eventos.EventBus;
import dominio.puerto.externo.EventBusListener;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAulas;
import aplicacion.casosdeuso.portaldeingreso.miareapersonal.GestionHorario;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import dominio.modelo.Clase;
import dominio.modelo.Docente;
import dominio.modelo.Evento;
import dominio.modelo.Grupo;
import dominio.modelo.Aula;
import dominio.modelo.PeriodoAcademico;
import dominio.modelo.TipoEvento;
import presentacion.dialogos.Dialogos;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
/**
 *
 * @author inici4rsesi0n
 */
@Component
@Scope("prototype")
public class VisorHorarioController implements Initializable, EventBusListener {
    private enum ModoVista { GRUPO, DOCENTE, AULA }
    private final GestionGrupos gestionGrupos;
    private final GestionDocentes gestionDocentes;
    private final GestionAulas gestionAulas;
    private final GestionPeriodos gestionPeriodos;
    private final GestionHorario gestionHorario;
    private final EventBus eventBus;
    @FXML private TabPane tabPaneFiltros;
    @FXML private ComboBox<String> cmbGrupoVisor;
    @FXML private ComboBox<String> cmbPeriodoVisor;
    @FXML private ComboBox<String> cmbDocenteVisor;
    @FXML private ComboBox<String> cmbPeriodoDocente;
    @FXML private ComboBox<String> cmbAulaVisor;
    @FXML private ComboBox<String> cmbPeriodoAula;
    @FXML private GridPane gridHorario;
    @FXML private FlowPane panelLeyenda;
    @FXML private ToggleButton toggleFinde;
    private final String[] DIAS_SEMANA = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
    private final Map<String, String> coloresAsignaturas = new HashMap<>();
    private final String[] PALETA_COLORES = {"#00D2FF", "#8B5CF6", "#EC4899", "#F59E0B", "#10B981", "#3B82F6", "#6366F1", "#F43F5E", "#14B8A6", "#F97316"};
    private List<Evento> eventosActuales = new ArrayList<>();
    private ModoVista ultimoModo = ModoVista.GRUPO;
    private String ultimoFiltro = "";
    private String ultimoPeriodo = "";
    public VisorHorarioController(GestionGrupos gestionGrupos, GestionDocentes gestionDocentes,
                                  GestionAulas gestionAulas, GestionPeriodos gestionPeriodos,
                                  GestionHorario gestionHorario, EventBus eventBus) {
        this.gestionGrupos = gestionGrupos;
        this.gestionDocentes = gestionDocentes;
        this.gestionAulas = gestionAulas;
        this.gestionPeriodos = gestionPeriodos;
        this.gestionHorario = gestionHorario;
        this.eventBus = eventBus;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarCombos();
        tabPaneFiltros.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> limpiarVisor());
        toggleFinde.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (!eventosActuales.isEmpty()) dibujarGrid(gridHorario, panelLeyenda, false);
        });
        eventBus.suscribir(TipoEvento.CLASES, this);
        eventBus.suscribir(TipoEvento.RECREOS, this);
    }
    @Override
    public void onEvent(TipoEvento evento) {
        if (evento == TipoEvento.CLASES || evento == TipoEvento.RECREOS) {
            if (!ultimoFiltro.isEmpty() && !eventosActuales.isEmpty()) regenerarHorarioActual();
        }
    }
    private void configurarCombos() {
        cmbGrupoVisor.getItems().add("Seleccione un grupo");
        for (Grupo g : gestionGrupos.listarTodos()) cmbGrupoVisor.getItems().add(g.getNombre());
        cmbGrupoVisor.getSelectionModel().selectFirst();
        cmbDocenteVisor.getItems().add("Seleccione un docente");
        for (Docente d : gestionDocentes.listarTodos()) cmbDocenteVisor.getItems().add(d.getCodigo() + " - " + d.getNombreCompleto());
        cmbDocenteVisor.getSelectionModel().selectFirst();
        cmbAulaVisor.getItems().add("Seleccione un aula");
        for (Aula a : gestionAulas.listarTodos()) cmbAulaVisor.getItems().add(a.getNombre());
        cmbAulaVisor.getSelectionModel().selectFirst();
        cmbPeriodoVisor.getItems().add("Seleccione un periodo");
        cmbPeriodoDocente.getItems().add("Seleccione un periodo");
        cmbPeriodoAula.getItems().add("Seleccione un periodo");
        for (PeriodoAcademico p : gestionPeriodos.listarTodos()) {
            cmbPeriodoVisor.getItems().add(p.getNombre());
            cmbPeriodoDocente.getItems().add(p.getNombre());
            cmbPeriodoAula.getItems().add(p.getNombre());
        }
        cmbPeriodoVisor.getSelectionModel().selectFirst();
        cmbPeriodoDocente.getSelectionModel().selectFirst();
        cmbPeriodoAula.getSelectionModel().selectFirst();
    }
    private void limpiarVisor() {
        gridHorario.getChildren().clear();
        gridHorario.getColumnConstraints().clear();
        gridHorario.getRowConstraints().clear();
        panelLeyenda.getChildren().clear();
        eventosActuales.clear();
        ultimoFiltro = "";
        ultimoPeriodo = "";
    }
    @FXML
    private void handleGenerarHorario() {
        String grupo = cmbGrupoVisor.getValue();
        String periodo = cmbPeriodoVisor.getValue();
        if (grupo == null || grupo.startsWith("Seleccione") || periodo == null || periodo.startsWith("Seleccione")) {
            Dialogos.info("Seleccione grupo y periodo."); return;
        }
        PeriodoAcademico p = gestionPeriodos.buscarPorNombre(periodo);
        Grupo g = gestionGrupos.buscarPorNombre(grupo);
        ultimoModo = ModoVista.GRUPO;
        ultimoFiltro = grupo;
        ultimoPeriodo = periodo;
        eventosActuales = gestionHorario.obtenerHorarioCompleto(g, p);
        dibujarGrid(gridHorario, panelLeyenda, false);
    }
    @FXML
    private void handleGenerarHorarioDocente() {
        String docSel = cmbDocenteVisor.getValue();
        String periodo = cmbPeriodoDocente.getValue();
        if (docSel == null || docSel.startsWith("Seleccione") || periodo == null || periodo.startsWith("Seleccione")) {
            Dialogos.info("Seleccione docente y periodo."); return;
        }
        String codigoDoc = docSel.split(" - ")[0];
        PeriodoAcademico p = gestionPeriodos.buscarPorNombre(periodo);
        ultimoModo = ModoVista.DOCENTE;
        ultimoFiltro = codigoDoc;
        ultimoPeriodo = periodo;
        eventosActuales = gestionHorario.obtenerHorarioDocente(codigoDoc, p);
        dibujarGrid(gridHorario, panelLeyenda, false);
    }
    @FXML
    private void handleGenerarHorarioAula() {
        String aula = cmbAulaVisor.getValue();
        String periodo = cmbPeriodoAula.getValue();
        if (aula == null || aula.startsWith("Seleccione") || periodo == null || periodo.startsWith("Seleccione")) {
            Dialogos.info("Seleccione aula y periodo."); return;
        }
        PeriodoAcademico p = gestionPeriodos.buscarPorNombre(periodo);
        ultimoModo = ModoVista.AULA;
        ultimoFiltro = aula;
        ultimoPeriodo = periodo;
        eventosActuales = gestionHorario.obtenerHorarioAula(aula, p);
        dibujarGrid(gridHorario, panelLeyenda, false);
    }
    private void regenerarHorarioActual() {
        PeriodoAcademico p = gestionPeriodos.buscarPorNombre(ultimoPeriodo);
        if (p == null) return;
        switch (ultimoModo) {
            case DOCENTE -> eventosActuales = gestionHorario.obtenerHorarioDocente(ultimoFiltro, p);
            case AULA -> eventosActuales = gestionHorario.obtenerHorarioAula(ultimoFiltro, p);
            default -> {
                Grupo g = gestionGrupos.buscarPorNombre(ultimoFiltro);
                if (g != null) eventosActuales = gestionHorario.obtenerHorarioCompleto(g, p);
            }
        }
        dibujarGrid(gridHorario, panelLeyenda, false);
    }
    private void dibujarGrid(GridPane targetGrid, FlowPane targetLeyenda, boolean isExport) {
        targetGrid.getChildren().clear();
        targetGrid.getColumnConstraints().clear();
        targetGrid.getRowConstraints().clear();
        targetLeyenda.getChildren().clear();
        targetGrid.setMaxWidth(Double.MAX_VALUE);
        if (eventosActuales.isEmpty()) {
            targetGrid.add(new Label("No hay clases programadas en este horario."), 0, 0);
            return;
        }
        LocalTime minHora = LocalTime.MAX;
        LocalTime maxHora = LocalTime.MIN;
        for (Evento e : eventosActuales) {
            LocalTime ini = LocalTime.parse(e.getHoraInicio());
            LocalTime fin = LocalTime.parse(e.getHoraFin());
            if (ini.isBefore(minHora)) minHora = ini;
            if (fin.isAfter(maxHora)) maxHora = fin;
        }
        int minMinutos = minHora.getHour() * 60 + minHora.getMinute();
        minMinutos = (minMinutos / 30) * 30;
        int maxMinutos = maxHora.getHour() * 60 + maxHora.getMinute();
        if (maxMinutos % 30 != 0) maxMinutos = ((maxMinutos / 30) + 1) * 30;
        int totalSlots = (maxMinutos - minMinutos) / 30;
        LocalTime baseTime = LocalTime.of(minMinutos / 60, minMinutos % 60);
        
        boolean mostrarFinde = toggleFinde.isSelected();
        int diasAMostrar = mostrarFinde ? 7 : 5;
        double percent = 100.0 / (diasAMostrar + 1);
        
        ColumnConstraints colHora = new ColumnConstraints(65);
        targetGrid.getColumnConstraints().add(colHora);
        for (int i = 0; i < diasAMostrar; i++) {
            ColumnConstraints colDia = new ColumnConstraints();
            colDia.setHgrow(Priority.ALWAYS);
            colDia.setPercentWidth(percent);
            targetGrid.getColumnConstraints().add(colDia);
        }
        
        targetGrid.add(crearHeader("Hora"), 0, 0);
        for (int i = 0; i < diasAMostrar; i++) {
            targetGrid.add(crearHeader(DIAS_SEMANA[i]), i + 1, 0);
        }
        for (int i = 0; i <= totalSlots; i++) {
            RowConstraints row = new RowConstraints(45);
            targetGrid.getRowConstraints().add(row);
            LocalTime t = baseTime.plusMinutes(i * 30L);
            Label lblHora = new Label(t.format(DateTimeFormatter.ofPattern("HH:mm")));
            lblHora.getStyleClass().add("horario-hora-label");
            targetGrid.add(lblHora, 0, i + 1);
        }
        
        String hoy = getDiaSemanaActual();
        LocalTime ahora = LocalTime.now();
        boolean indicadorAhoraDibujado = false;
        
        for (Evento ev : eventosActuales) {
            int colIdx = getColumnaDia(ev.getDiaSemana());
            if (colIdx < 0 || colIdx > diasAMostrar) continue;
            
            LocalTime ini = LocalTime.parse(ev.getHoraInicio());
            LocalTime fin = LocalTime.parse(ev.getHoraFin());
            int startMinutos = ini.getHour() * 60 + ini.getMinute();
            int endMinutos = fin.getHour() * 60 + fin.getMinute();
            int startRow = ((startMinutos - minMinutos) / 30) + 1;
            int rowSpan = Math.max(1, (endMinutos - startMinutos) / 30);
            VBox bloque = crearBloqueEvento(ev, isExport);
            targetGrid.add(bloque, colIdx, startRow, 1, rowSpan);
            
            if (ev.getDiaSemana().equalsIgnoreCase(hoy) && !indicadorAhoraDibujado && colIdx <= diasAMostrar) {
                if (ahora.isAfter(ini) && ahora.isBefore(fin)) {
                    HBox lineaAhora = new HBox(5);
                    lineaAhora.setAlignment(Pos.CENTER_LEFT);
                    Label lblAhora = new Label(ahora.format(DateTimeFormatter.ofPattern("HH:mm")));
                    lblAhora.getStyleClass().add("linea-ahora-badge");
                    Region linea = new Region();
                    linea.getStyleClass().add("linea-ahora");
                    linea.setPrefHeight(2);
                    linea.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(linea, Priority.ALWAYS);
                    lineaAhora.getChildren().addAll(lblAhora, linea);
                    targetGrid.add(lineaAhora, colIdx, startRow, 1, 1);
                    indicadorAhoraDibujado = true;
                }
            }
        }
        actualizarLeyenda(targetLeyenda, isExport);
    }
    private String getDiaSemanaActual() {
        switch (LocalDate.now().getDayOfWeek()) {
            case MONDAY: return "Lunes";
            case TUESDAY: return "Martes";
            case WEDNESDAY: return "Miércoles";
            case THURSDAY: return "Jueves";
            case FRIDAY: return "Viernes";
            case SATURDAY: return "Sábado";
            case SUNDAY: return "Domingo";
            default: return "";
        }
    }
    private int getColumnaDia(String dia) {
        for (int i = 0; i < DIAS_SEMANA.length; i++) {
            if (DIAS_SEMANA[i].equalsIgnoreCase(dia)) return i + 1;
        }
        return -1;
    }
    private Label crearHeader(String texto) {
        Label lbl = new Label(texto);
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.getStyleClass().add("horario-header");
        return lbl;
    }
    private VBox crearBloqueEvento(Evento ev, boolean isExport) {
        VBox box = new VBox(4);
        box.setMaxWidth(Double.MAX_VALUE);
        box.setMaxHeight(Double.MAX_VALUE);
        if (ev instanceof Clase clase) {
            String asignatura = clase.getAsignatura().getNombre();
            String colorHex = coloresAsignaturas.computeIfAbsent(asignatura, k -> {
                int index = Math.abs(k.hashCode()) % PALETA_COLORES.length;
                return PALETA_COLORES[index];
            });
            box.getStyleClass().add("tarjeta-clase");
            box.setStyle("-fx-border-color: transparent transparent transparent " + colorHex + ";");
            box.getProperties().put("asignatura", asignatura);
            
            Label lblAsig = new Label(asignatura);
            lblAsig.getStyleClass().add("clase-titulo");
            lblAsig.setWrapText(true);
            lblAsig.setStyle("-fx-text-fill: " + colorHex + ";");
            
            Label lblHora = new Label(ev.getHoraInicio() + " - " + ev.getHoraFin());
            lblHora.getStyleClass().add("clase-hora");
            
            Label lblDetalle = new Label("Aula: " + (clase.getAula() != null ? clase.getAula().getNombre() : "S/A"));
            lblDetalle.getStyleClass().add("clase-detalle");
            
            Label lblDoc = new Label(clase.getDocente() != null ? clase.getDocente().getNombreCompleto() : "");
            lblDoc.getStyleClass().add("clase-detalle");
            lblDoc.setWrapText(true);
            
            Region spacer = new Region();
            VBox.setVgrow(spacer, Priority.ALWAYS);
            box.getChildren().addAll(lblAsig, lblHora, spacer, lblDetalle, lblDoc);
            
            if (!isExport) {
                box.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2) {
                        mostrarCartaClase(clase, asignatura, colorHex);
                    }
                });
            }
        } else {
            box.getStyleClass().add("tarjeta-recreo");
            Label lblRec = new Label("R E C R E O");
            lblRec.getStyleClass().add("recreo-texto");
            box.getChildren().add(lblRec);
            box.setAlignment(Pos.CENTER);
        }
        return box;
    }
    private void mostrarCartaClase(Clase clase, String asignatura, String colorHex) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(tabPaneFiltros.getScene().getWindow());
        stage.initStyle(StageStyle.TRANSPARENT);
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: rgba(15, 23, 42, 0.98); -fx-border-color: " + colorHex + "; -fx-border-width: 2px; -fx-border-radius: 12px; -fx-background-radius: 12px;");
        root.setMinWidth(400);
        
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.7));
        dropShadow.setRadius(20);
        root.setEffect(dropShadow);
        
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label lblAsig = new Label(asignatura);
        lblAsig.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-size: 26px; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.getStyleClass().add("visor-close-btn");
        btnClose.setOnAction(e -> stage.close());
        header.getChildren().addAll(lblAsig, spacer, btnClose);
        
        VBox infoBox = new VBox(8);
        infoBox.getChildren().addAll(
            crearFilaInfo("M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z", clase.getHoraInicio() + " - " + clase.getHoraFin(), colorHex),
            crearFilaInfo("M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z", clase.getDocente() != null ? clase.getDocente().getNombreCompleto() : "S/A", colorHex),
            crearFilaInfo("M12 7V3H2v18h20V7H12zM6 19H4v-2h2v2zm0-4H4v-2h2v2zm0-4H4V9h2v2zm0-4H4V5h2v2zm4 12H8v-2h2v2zm0-4H8v-2h2v2zm0-4H8V9h2v2zm0-4H8V5h2v2zm10 12h-8v-2h2v-2h-2v-2h2v-2h-2V9h8v10zm-2-8h-2v2h2v-2zm0 4h-2v2h2v-2z", clase.getAula() != null ? clase.getAula().getNombre() : "S/A", colorHex),
            crearFilaInfo("M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z", clase.getGrupo() != null ? clase.getGrupo().getNombre() : "S/A", colorHex),
            crearFilaInfo("M19 3h-1V1h-2v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11z", clase.getPeriodo() != null ? clase.getPeriodo().getNombre() : "S/A", colorHex)
        );
        
        Label lblColorTitle = new Label("Seleccionar Color de Asignatura:");
        lblColorTitle.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px; -fx-padding: 10 0 0 0;");
        
        FlowPane colorPanel = new FlowPane(10, 10);
        for (String color : PALETA_COLORES) {
            Circle circle = new Circle(12);
            circle.setFill(Color.web(color));
            if (color.equals(colorHex)) {
                circle.setStroke(Color.WHITE);
                circle.setStrokeWidth(3);
            } else {
                circle.setStroke(Color.TRANSPARENT);
            }
            circle.setCursor(javafx.scene.Cursor.HAND);
            circle.setOnMouseClicked(e -> {
                coloresAsignaturas.put(asignatura, color);
                dibujarGrid(gridHorario, panelLeyenda, false);
                stage.close();
            });
            colorPanel.getChildren().add(circle);
        }
        
        root.getChildren().addAll(header, new Separator(), infoBox, lblColorTitle, colorPanel);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        URL css = getClass().getResource("/css/app.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
        stage.setScene(scene);
        stage.showAndWait();
    }
    private HBox crearFilaInfo(String svgContent, String texto, String colorHex) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        SVGPath icon = new SVGPath();
        icon.setContent(svgContent);
        icon.setStyle("-fx-fill: " + colorHex + "; -fx-scale-x: 1.1; -fx-scale-y: 1.1;");
        Label lbl = new Label(texto);
        lbl.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 14px;");
        hbox.getChildren().addAll(icon, lbl);
        return hbox;
    }
    private void actualizarLeyenda(FlowPane targetLeyenda, boolean isExport) {
        targetLeyenda.getChildren().clear();
        for (Map.Entry<String, String> entry : coloresAsignaturas.entrySet()) {
            HBox item = new HBox(8);
            item.setAlignment(Pos.CENTER_LEFT);
            if (!isExport) {
                item.setCursor(javafx.scene.Cursor.HAND);
            }
            Circle circle = new Circle(6);
            circle.setFill(Color.web(entry.getValue()));
            Label lbl = new Label(entry.getKey());
            lbl.getStyleClass().add("leyenda-item");
            item.getChildren().addAll(circle, lbl);
            if (!isExport) {
                item.setOnMouseEntered(e -> resaltarAsignatura(entry.getKey(), true));
                item.setOnMouseExited(e -> resaltarAsignatura(null, false));
            }
            targetLeyenda.getChildren().add(item);
        }
    }
    private void resaltarAsignatura(String asignatura, boolean enfocar) {
        for (javafx.scene.Node node : gridHorario.getChildren()) {
            if (node instanceof VBox box && box.getProperties().containsKey("asignatura")) {
                String asigBox = (String) box.getProperties().get("asignatura");
                if (enfocar && asigBox.equals(asignatura)) {
                    box.setOpacity(1.0);
                    box.setScaleX(1.02);
                    box.setScaleY(1.02);
                } else if (enfocar && !asigBox.equals(asignatura)) {
                    box.setOpacity(0.2);
                    box.setScaleX(1.0);
                    box.setScaleY(1.0);
                } else {
                    box.setOpacity(1.0);
                    box.setScaleX(1.0);
                    box.setScaleY(1.0);
                }
            }
        }
    }
    private WritableImage generarSnapshot() {
        GridPane exportGrid = new GridPane();
        exportGrid.setHgap(4);
        exportGrid.setVgap(4);
        exportGrid.setStyle("-fx-background-color: transparent;");
        exportGrid.setMaxWidth(Double.MAX_VALUE);
        
        FlowPane exportLeyenda = new FlowPane(15, 10);
        exportLeyenda.getStyleClass().add("leyenda-container");
        
        dibujarGrid(exportGrid, exportLeyenda, true);
        
        VBox exportRoot = new VBox(15, exportGrid, exportLeyenda);
        exportRoot.setStyle("-fx-background-color: #0F172A; -fx-padding: 20px;");
        exportRoot.setPrefWidth(1200);
        exportRoot.setPrefHeight(800);
        
        Scene tempScene = new Scene(exportRoot);
        URL css = getClass().getResource("/css/app.css");
        if (css != null) {
            tempScene.getStylesheets().add(css.toExternalForm());
        }
        
        exportRoot.applyCss();
        exportRoot.layout();
        
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.web("#0F172A"));
        return exportRoot.snapshot(params, null);
    }
    @FXML
    private void handleExportarPdf() {
        if (gridHorario.getChildren().isEmpty()) {
            Dialogos.info("No hay ningún horario generado para exportar.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Horario como PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documento PDF", "*.pdf"));
        fileChooser.setInitialFileName("horario_" + LocalDate.now() + ".pdf");
        File file = fileChooser.showSaveDialog(tabPaneFiltros.getScene().getWindow());
        
        if (file != null) {
            try {
                WritableImage image = generarSnapshot();
                try (PDDocument document = new PDDocument()) {
                    PDImageXObject pdImage = LosslessFactory.createFromImage(document, SwingFXUtils.fromFXImage(image, null));
                    PDPage page = new PDPage(new PDRectangle(pdImage.getWidth(), pdImage.getHeight()));
                    document.addPage(page);
                    try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                        contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());
                    }
                    document.save(file);
                }
                Dialogos.info("Horario exportado exitosamente a PDF:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                Dialogos.info("Error al exportar el PDF: " + e.getMessage());
            }
        }
    }
    @FXML
    private void handleExportarImagen() {
        if (gridHorario.getChildren().isEmpty()) {
            Dialogos.info("No hay ningún horario generado para exportar.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Horario como Imagen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagen PNG", "*.png"));
        fileChooser.setInitialFileName("horario_" + LocalDate.now() + ".png");
        File file = fileChooser.showSaveDialog(tabPaneFiltros.getScene().getWindow());
        
        if (file != null) {
            try {
                WritableImage image = generarSnapshot();
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                Dialogos.info("Horario exportado exitosamente a Imagen:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                Dialogos.info("Error al exportar la imagen: " + e.getMessage());
            }
        }
    }
    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) tabPaneFiltros.getScene().getWindow();
        stage.close();
    }
}
