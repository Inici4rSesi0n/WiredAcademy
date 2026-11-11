/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo.configuracion;
public class ConfiguracionSistema {
    private Long id;
    private TipoInstitucion tipoInstitucion;
    public ConfiguracionSistema() {}
    public ConfiguracionSistema(Long id, TipoInstitucion tipoInstitucion) {
        this.id = id;
        this.tipoInstitucion = tipoInstitucion;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TipoInstitucion getTipoInstitucion() { return tipoInstitucion; }
    public void setTipoInstitucion(TipoInstitucion tipoInstitucion) { this.tipoInstitucion = tipoInstitucion; }
}
