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
public class CoordinadorAcademico extends Usuario {

    private static final long serialVersionUID = 1L;

    private List<Grado> gradosSupervisados;

    protected CoordinadorAcademico() {
        super();
        this.gradosSupervisados = new ArrayList<>();
    }

    public CoordinadorAcademico(String codigo, String hashContrasena, String dni,
                                String nombre, String apellido, int edad) {
        super(codigo, hashContrasena, dni, nombre, apellido, edad, Rol.COORDINADOR);
        this.gradosSupervisados = new ArrayList<>();
    }

    public List<Grado> getGradosSupervisados() {
        if (gradosSupervisados == null) {
            gradosSupervisados = new ArrayList<>();
        }
        return Collections.unmodifiableList(gradosSupervisados);
    }

    public void setGradosSupervisados(List<Grado> grados) {
        this.gradosSupervisados = (grados != null) ? new ArrayList<>(grados) : new ArrayList<>();
    }

    public void agregarGrado(Grado grado) {
        if (grado == null) return;
        if (gradosSupervisados == null) {
            gradosSupervisados = new ArrayList<>();
        }
        if (!gradosSupervisados.contains(grado)) {
            gradosSupervisados.add(grado);
        }
    }

    public void removerGrado(Grado grado) {
        if (gradosSupervisados == null) {
            gradosSupervisados = new ArrayList<>();
        }
        gradosSupervisados.remove(grado);
    }

    public String getNombreCompleto() {
        return getNombre() + " " + getApellido();
    }
    @Override
    public String toString() {
        String nombresGrados = gradosSupervisados.stream()
                .map(Grado::getNombre)
                .collect(java.util.stream.Collectors.joining(", "));
        return "CoordinadorAcademico [" + super.toString()
                + ", gradosSupervisados=[" + nombresGrados + "]]";
    }
}
