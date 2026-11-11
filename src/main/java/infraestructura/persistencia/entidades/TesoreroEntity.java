/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.entidades;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
/**
 *
 * @author inici4rsesi0n
 */
@Entity
@DiscriminatorValue("TESORERO")
public class TesoreroEntity extends UsuarioEntity{}
