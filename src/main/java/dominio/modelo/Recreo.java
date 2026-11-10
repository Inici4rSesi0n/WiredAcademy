/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/**
 *
 * @author inici4rsesi0n
 */
public class Recreo extends Evento {
    private static final long serialVersionUID = 1L;
    private String descripcion;
    private List<PeriodoAcademico> periodos;
    protected Recreo() {
        super();
        this.descripcion = "Recreo";
        this.periodos = new ArrayList<>();
    }
    public Recreo(FranjaHoraria franja, String descripcion, List<PeriodoAcademico> periodos) {
        super(franja);
        this.descripcion = (descripcion != null && !descripcion.isBlank()) ? descripcion : "Recreo";
        this.periodos = (periodos != null) ? new ArrayList<>(periodos) : new ArrayList<>();
    }
    @Override
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) {
        this.descripcion = (descripcion != null && !descripcion.isBlank()) ? descripcion : "Recreo";
    }
    public List<PeriodoAcademico> getPeriodos() {
        if (periodos == null) periodos = new ArrayList<>();
        return Collections.unmodifiableList(periodos);
    }
    public void setPeriodos(List<PeriodoAcademico> periodos) {
        this.periodos = (periodos != null) ? new ArrayList<>(periodos) : new ArrayList<>();
    }
    public boolean aplicaEn(PeriodoAcademico periodo) {
        return periodo != null && getPeriodos().contains(periodo);
    }
    public void aplicarA(PeriodoAcademico periodo) {
        if (periodo == null) return;
        if (periodos == null) periodos = new ArrayList<>();
        if (!periodos.contains(periodo)) periodos.add(periodo);
    }
    public void removerDe(PeriodoAcademico periodo) {
        if (periodos == null || periodo == null) return;
        periodos.remove(periodo);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recreo)) return false;
        Recreo recreo = (Recreo) o;
        return Objects.equals(getFranja(), recreo.getFranja())
                && Objects.equals(descripcion, recreo.descripcion);
    }
    @Override
    public int hashCode() {
        return Objects.hash(getFranja(), descripcion);
    }
}
