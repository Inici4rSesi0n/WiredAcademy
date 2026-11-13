/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.entidades;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name="publicaciones")
public class PublicacionEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=150)
    private String titulo;
    @Column(columnDefinition="TEXT")
    private String descripcion;
    @Column(nullable=false, length=20)
    private String tipo;
    @Column(name="numero_semana")
    private Integer numeroSemana;
    @Column(name="fecha_limite")
    private LocalDateTime fechaLimite;
    @Column(name="url_archivo", length=255)
    private String urlArchivo;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="asignatura_id")
    private AsignaturaEntity asignatura;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="autor_id", nullable=false)
    private UsuarioEntity autor;
    @Column(name="fecha_creacion", nullable=false)
    private LocalDateTime fechaCreacion;
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getTitulo(){return titulo;}
    public void setTitulo(String titulo){this.titulo=titulo;}
    public String getDescripcion(){return descripcion;}
    public void setDescripcion(String descripcion){this.descripcion=descripcion;}
    public String getTipo(){return tipo;}
    public void setTipo(String tipo){this.tipo=tipo;}
    public Integer getNumeroSemana(){return numeroSemana;}
    public void setNumeroSemana(Integer numeroSemana){this.numeroSemana=numeroSemana;}
    public LocalDateTime getFechaLimite(){return fechaLimite;}
    public void setFechaLimite(LocalDateTime fechaLimite){this.fechaLimite=fechaLimite;}
    public String getUrlArchivo(){return urlArchivo;}
    public void setUrlArchivo(String urlArchivo){this.urlArchivo=urlArchivo;}
    public AsignaturaEntity getAsignatura(){return asignatura;}
    public void setAsignatura(AsignaturaEntity asignatura){this.asignatura=asignatura;}
    public UsuarioEntity getAutor(){return autor;}
    public void setAutor(UsuarioEntity autor){this.autor=autor;}
    public LocalDateTime getFechaCreacion(){return fechaCreacion;}
    public void setFechaCreacion(LocalDateTime fechaCreacion){this.fechaCreacion=fechaCreacion;}
}
