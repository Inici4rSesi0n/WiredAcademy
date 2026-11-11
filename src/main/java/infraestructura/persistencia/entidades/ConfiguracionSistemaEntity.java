/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.entidades;
import jakarta.persistence.*;
/**
 *
 * @author inici4rsesi0n
 */
@Entity
@Table(name="configuracion_sistema")
public class ConfiguracionSistemaEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="tipo_institucion", nullable=false, length=20)
    private String tipoInstitucion;
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getTipoInstitucion(){return tipoInstitucion;}
    public void setTipoInstitucion(String tipoInstitucion){this.tipoInstitucion=tipoInstitucion;}
}
