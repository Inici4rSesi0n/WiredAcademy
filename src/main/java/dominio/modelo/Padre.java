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
public class Padre extends Usuario {
    private static final long serialVersionUID = 1L;
    private List<Estudiante> estudiantes;
    protected Padre() {
        super();
        this.estudiantes = new ArrayList<>();
    }
    public Padre(String codigo, String hashContrasena, String dni,
                 String nombre, String apellido, int edad,
                 List<Estudiante> estudiantes) {
        super(codigo, hashContrasena, dni, nombre, apellido, edad, Rol.PADRE);
        this.estudiantes = (estudiantes != null)
                ? new ArrayList<>(estudiantes)
                : new ArrayList<>();
    }
    public List<Estudiante> getEstudiantes() {
        return Collections.unmodifiableList(estudiantes);
    }
    public void agregarHijo(Estudiante estudiante) {
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante no puede ser nulo");
        }
        if (!estudiantes.contains(estudiante)) {
            estudiantes.add(estudiante);
        }
    }
    public void removerHijo(Estudiante estudiante) {
        estudiantes.remove(estudiante);
    }
    @Override
    public String toString() {
        return "Padre [" + super.toString() + ", estudiantesVinculados=" + estudiantes.size() + "]";
    }
}
