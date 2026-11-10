/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
/**
 *
 * @author inici4rsesi0n
 */
public class Secretario extends Usuario {
    private static final long serialVersionUID = 1L;
    protected Secretario() {
        super();
    }
    public Secretario(String codigo, String hashContrasena, String dni,
                      String nombre, String apellido, int edad) {
        super(codigo, hashContrasena, dni, nombre, apellido, edad, Rol.SECRETARIO);
    }
    @Override
    public String toString() {
        return "Secretario [" + super.toString() + "]";
    }
}
