/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
/**
 *
 * @author inici4rsesi0n
 */
public class Director extends Usuario {
    private static final long serialVersionUID = 1L;
    protected Director() {
        super();
    }
    public Director(String codigo, String hashContrasena, String dni,
                    String nombre, String apellido, int edad) {
        super(codigo, hashContrasena, dni, nombre, apellido, edad, Rol.DIRECTOR);
    }
    @Override
    public String toString() {
        return "Director [" + super.toString() + "]";
    }
}
