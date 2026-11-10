/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
import java.io.Serializable;
import java.util.Objects;
/**
 *
 * @author inici4rsesi0n
 */
public final class FranjaHoraria implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String diaSemana;
    private final String horaInicio;
    private final String horaFin;
    public FranjaHoraria(String diaSemana, String horaInicio, String horaFin) {
        if (diaSemana == null || diaSemana.isBlank()) {
            throw new IllegalArgumentException("El día de la semana no puede ser nulo o vacío");
        }
        if (horaInicio == null || horaInicio.isBlank()) {
            throw new IllegalArgumentException("La hora de inicio no puede ser nula o vacía");
        }
        if (horaFin == null || horaFin.isBlank()) {
            throw new IllegalArgumentException("La hora de fin no puede ser nula o vacía");
        }
        if (horaInicio.compareTo(horaFin) >= 0) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
        }
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }
    public String getDiaSemana() { return diaSemana; }
    public String getHoraInicio() { return horaInicio; }
    public String getHoraFin() { return horaFin; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FranjaHoraria)) return false;
        FranjaHoraria that = (FranjaHoraria) o;
        return diaSemana.equals(that.diaSemana) &&
               horaInicio.equals(that.horaInicio) &&
               horaFin.equals(that.horaFin);
    }
    @Override
    public int hashCode() {
        return Objects.hash(diaSemana, horaInicio, horaFin);
    }
    @Override
    public String toString() {
        return diaSemana + " " + horaInicio + "-" + horaFin;
    }
}