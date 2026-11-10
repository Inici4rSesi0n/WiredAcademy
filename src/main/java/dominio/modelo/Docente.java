/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
public class Docente extends Usuario {
    private static final long serialVersionUID = 1L;
    private String especialidad;
    private List<Asignatura> asignaturas;
    private Grupo tutoria;
    protected Docente() {
        super();
        this.asignaturas = new ArrayList<>();
    }
    public Docente(String codigo, String hashContrasena, String dni,
                   String nombre, String apellido, int edad,
                   String especialidad, List<Asignatura> asignaturas) {
        super(codigo, hashContrasena, dni, nombre, apellido, edad, Rol.DOCENTE);
        if (especialidad == null || especialidad.isBlank()) {
            throw new IllegalArgumentException("La especialidad no puede ser nula o vacía");
        }
        this.especialidad = especialidad;
        this.asignaturas = (asignaturas != null)
                ? new ArrayList<>(asignaturas)
                : new ArrayList<>();
        this.tutoria = null;
    }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad){
        if(especialidad==null || especialidad.isBlank()){
            throw new IllegalArgumentException("La especialidad no puede ser nula o vacía");
        }
        this.especialidad = especialidad;
    }
    public List<Asignatura> getAsignaturas() {
        return Collections.unmodifiableList(asignaturas);
    }
    public void setAsignaturas(List<Asignatura> asignaturas) {
        this.asignaturas = (asignaturas != null)
            ? new ArrayList<>(asignaturas)
            : new ArrayList<>();
    }
    public void agregarAsignatura(Asignatura asignatura) {
        if (asignatura == null) throw new IllegalArgumentException("La asignatura no puede ser nula");
        if (!asignaturas.contains(asignatura)) {
            asignaturas.add(asignatura);
        }
    }
    public void removerAsignatura(Asignatura asignatura) {
        asignaturas.remove(asignatura);
    }
    public Grupo getTutoria() { return tutoria; }
    public void setTutoria(Grupo tutoria) {
        this.tutoria = tutoria;
    }
    public String getNombreCompleto() {
        return getNombre() + " " + getApellido();
    }
    @Override
    public String toString() {
        String nombresAsignaturas = asignaturas.stream()
            .map(Asignatura::getNombre)
            .collect(java.util.stream.Collectors.joining(", "));
        return super.toString() +
               ", Especialidad=" + especialidad +
               ", Tutoría=" + (tutoria != null ? tutoria.getNombre() : "Sin tutoría") +
               ", Asignaturas=[" + nombresAsignaturas + "]";
    }
}
