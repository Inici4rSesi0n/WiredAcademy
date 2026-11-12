/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
import java.io.Serializable;
import java.util.Objects;
public class InscripcionAsignatura implements Serializable {
    private static final long serialVersionUID = 1L;
    private Asignatura asignatura;
    private Grupo grupo;
    public InscripcionAsignatura(Asignatura asignatura, Grupo grupo) {
        if (asignatura == null) throw new IllegalArgumentException("La asignatura no puede ser nula");
        if (grupo == null) throw new IllegalArgumentException("El grupo/sección no puede ser nulo");
        this.asignatura = asignatura;
        this.grupo = grupo;
    }
    public Asignatura getAsignatura() { return asignatura; }
    public void setAsignatura(Asignatura asignatura) { this.asignatura = asignatura; }
    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InscripcionAsignatura)) return false;
        InscripcionAsignatura that = (InscripcionAsignatura) o;
        return Objects.equals(asignatura, that.asignatura);
    }
    @Override
    public int hashCode() {
        return Objects.hash(asignatura);
    }
}
