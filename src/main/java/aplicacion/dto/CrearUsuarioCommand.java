/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.dto;
import dominio.modelo.Usuario;
/**
 *
 * @author inici4rsesi0n
 */
public record CrearUsuarioCommand(
        String codigo,
        String hash,
        String dni,
        String nombre,
        String apellido,
        int edad,
        Usuario.Rol rol,
        String especialidad
) {
    public CrearUsuarioCommand {
        if (dni == null || !dni.matches("[A-Za-z0-9]{4,20}"))
            throw new IllegalArgumentException("DNI debe ser alfanumérico de 4 a 20 caracteres");
        if (codigo == null || codigo.isBlank())
            throw new IllegalArgumentException("El código no puede estar vacío");
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        if (apellido == null || apellido.isBlank())
            throw new IllegalArgumentException("El apellido no puede estar vacío");
        if (edad <= 0)
            throw new IllegalArgumentException("La edad debe ser positiva");
        if (rol == null)
            throw new IllegalArgumentException("El rol no puede ser nulo");
    }
}
