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
public abstract class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum Rol {
        DIRECTOR,
        DOCENTE,
        ESTUDIANTE,
        SECRETARIO,
        COORDINADOR,
        PADRE,
        ADMINISTRADOR
    }
    private String codigo;
    private String hashContrasena;
    private String dni;
    private String nombre;
    private String apellido;
    private int edad;
    private Rol rol;
    protected Usuario() {}
    public Usuario(String codigo, String hashContrasena, String dni,
                   String nombre, String apellido, int edad, Rol rol) {
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("Código no puede ser nulo/vacío");
        if (hashContrasena == null || hashContrasena.isBlank()) throw new IllegalArgumentException("Hash no puede ser nulo/vacío");
        if (dni == null || !dni.matches("[A-Za-z0-9]{1,20}")) throw new IllegalArgumentException("DNI debe ser alfanumérico de 1 a 20 caracteres");
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre no puede ser nulo/vacío");
        if (apellido == null || apellido.isBlank()) throw new IllegalArgumentException("Apellido no puede ser nulo/vacío");
        if (edad <= 0) throw new IllegalArgumentException("Edad debe ser positiva");
        if (rol == null) throw new IllegalArgumentException("Rol no puede ser nulo");
        this.codigo = codigo;
        this.hashContrasena = hashContrasena;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.rol = rol;
    }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getHashContrasena() { return hashContrasena; }
    public void setHashContrasena(String hashContrasena) { this.hashContrasena = hashContrasena; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public Rol getRol() { return rol; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(codigo, usuario.codigo);
    }
    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
    @Override
    public String toString() {
        return "Usuario [codigo=" + codigo + ", dni=" + dni + ", nombre=" + nombre +
               ", apellido=" + apellido + ", edad=" + edad + ", rol=" + rol + "]";
    }
}
