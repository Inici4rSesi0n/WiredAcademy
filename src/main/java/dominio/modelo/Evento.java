/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
import java.io.Serializable;
/**
 * Clase base abstracta para eventos de horario.
 *
 * @author inici4rsesi0n
 */
public abstract class Evento implements Serializable {
    private static final long serialVersionUID = 1L;
    private FranjaHoraria franja;
    protected Evento() {}
    public Evento(FranjaHoraria franja) {
        if (franja == null) {
            throw new IllegalArgumentException("La franja horaria no puede ser nula");
        }
        this.franja = franja;
    }
    public abstract String getDescripcion();
    public String getDiaSemana() { return franja.getDiaSemana(); }
    public String getHoraInicio() { return franja.getHoraInicio(); }
    public String getHoraFin() { return franja.getHoraFin(); }
    public FranjaHoraria getFranja() { return franja; }
    public void setFranja(FranjaHoraria franja) { this.franja = franja; }
    @Override
    public String toString() {
        String desc = getDescripcion();
        return (desc != null ? desc : "Sin descripción") + " (" + franja + ")";
    }
}
