/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
import java.util.Objects;
/**
 *
 * @author inici4rsesi0n
 */
public class Clase extends Evento {
    private static final long serialVersionUID = 1L;
    private PeriodoAcademico periodo;
    private Asignatura asignatura;
    private Docente docente;
    private Grupo grupo;
    private Aula aula;
    protected Clase() {
        super();
    }
    public Clase(FranjaHoraria franja, PeriodoAcademico periodo,
                 Asignatura asignatura, Docente docente,
                 Grupo grupo, Aula aula) {
        super(franja);
        if (periodo == null) throw new IllegalArgumentException("El periodo académico no puede ser nulo");
        if (asignatura == null) throw new IllegalArgumentException("La asignatura no puede ser nula");
        if (docente == null) throw new IllegalArgumentException("El docente no puede ser nulo");
        if (grupo == null) throw new IllegalArgumentException("El grupo no puede ser nulo");
        if (aula == null) throw new IllegalArgumentException("El aula no puede ser nula");
        this.periodo = periodo;
        this.asignatura = asignatura;
        this.docente = docente;
        this.grupo = grupo;
        this.aula = aula;
    }
    public PeriodoAcademico getPeriodo() { return periodo; }
    public void setPeriodo(PeriodoAcademico periodo) { this.periodo = periodo; }
    public Asignatura getAsignatura() { return asignatura; }
    public void setAsignatura(Asignatura asignatura) { this.asignatura = asignatura; }
    public Docente getDocente() { return docente; }
    public void setDocente(Docente docente) { this.docente = docente; }
    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }
    public Aula getAula() { return aula; }
    public void setAula(Aula aula) { this.aula = aula; }
    @Override
    public String getDescripcion() {
        String nomAsig = (asignatura != null) ? asignatura.getNombre() : "Sin asignatura";
        String nomDoc = (docente != null) ? docente.getNombre() + " " + docente.getApellido() : "Sin docente";
        return nomAsig + " (" + nomDoc + ")";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Clase)) return false;
        Clase clase = (Clase) o;
        return Objects.equals(getFranja(), clase.getFranja()) &&
               Objects.equals(periodo, clase.periodo) &&
               Objects.equals(asignatura, clase.asignatura) &&
               Objects.equals(docente, clase.docente) &&
               Objects.equals(grupo, clase.grupo) &&
               Objects.equals(aula, clase.aula);
    }
    @Override
    public int hashCode() {
        return Objects.hash(getFranja(), periodo, asignatura, docente, grupo, aula);
    }
}
