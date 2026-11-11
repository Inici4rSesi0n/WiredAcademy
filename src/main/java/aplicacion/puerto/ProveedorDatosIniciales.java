/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.puerto;
import dominio.modelo.configuracion.TipoInstitucion;
/**
 *
 * @author inici4rsesi0n
 */
public interface ProveedorDatosIniciales {
    String getCodigo();
    String getNombre();
    String getApellido();
    String getDni();
    char[] getContrasena();
    boolean isConfirmado();
    TipoInstitucion getTipoInstitucion();
}
