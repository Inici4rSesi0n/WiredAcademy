/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;
import java.io.Serializable;
import java.time.LocalDateTime;
public class Publicacion implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String titulo;
    private String descripcion;
    private TipoPublicacion tipo;
    private Integer numeroSemana;
    private LocalDateTime fechaLimite;
    private String urlArchivo;
    private Asignatura asignatura;
    private Usuario autor;
    public Publicacion(Long id, String titulo, String descripcion, TipoPublicacion tipo,
                       Integer numeroSemana, LocalDateTime fechaLimite, String urlArchivo,
                       Asignatura asignatura, Usuario autor) {
        if (titulo == null || titulo.isBlank()) throw new IllegalArgumentException("El título es obligatorio");
        if (tipo == null) throw new IllegalArgumentException("El tipo es obligatorio");
        if (autor == null) throw new IllegalArgumentException("El autor es obligatorio");
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.numeroSemana = numeroSemana;
        this.fechaLimite = fechaLimite;
        this.urlArchivo = urlArchivo;
        this.asignatura = asignatura;
        this.autor = autor;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public TipoPublicacion getTipo() { return tipo; }
    public void setTipo(TipoPublicacion tipo) { this.tipo = tipo; }
    public Integer getNumeroSemana() { return numeroSemana; }
    public void setNumeroSemana(Integer numeroSemana) { this.numeroSemana = numeroSemana; }
    public LocalDateTime getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDateTime fechaLimite) { this.fechaLimite = fechaLimite; }
    public String getUrlArchivo() { return urlArchivo; }
    public void setUrlArchivo(String urlArchivo) { this.urlArchivo = urlArchivo; }
    public Asignatura getAsignatura() { return asignatura; }
    public void setAsignatura(Asignatura asignatura) { this.asignatura = asignatura; }
    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }
}
