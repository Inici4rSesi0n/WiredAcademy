/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.puerto.repositorio;
import dominio.modelo.Clase;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioClases extends Repositorio<Clase> {
    List<Clase> buscarPorGrupoYGrado(String nombreGrupo, String nombreGrado);
    List<Clase> buscarPorDocente(String codigoDocente, String nombrePeriodo);
    List<Clase> buscarPorAula(String nombreAula, String nombrePeriodo);
    Optional<Clase> buscarClase(String nombreGrado, String nombreGrupo, String dia,
                                String horaInicio, String horaFin);
    /**
     * Verifica si existe conflicto de horario para el docente indicado.
     * Detecta solapamiento real de franjas (no solo coincidencia exacta).
     *
     * @param claseExcluida clase a excluir de la verificación (útil en actualizaciones); puede ser null
     */
    boolean existeConflictoDocente(String codigoDocente, String dia,
                                   String horaInicio, String horaFin, Clase claseExcluida);
    /**
     * Verifica si el aula ya está ocupada en la franja indicada.
     */
    boolean existeConflictoAula(String nombreAula, String dia,
                                String horaInicio, String horaFin, Clase claseExcluida);
    /**
     * Verifica si el grupo ya tiene una clase en la franja indicada.
     */
    boolean existeConflictoGrupo(String nombreGrupo, String nombreGrado, String dia,
                                 String horaInicio, String horaFin, Clase claseExcluida);
}
