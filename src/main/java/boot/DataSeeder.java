/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package boot;

import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAulas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionEstudiantes;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionPadres;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionUsuarios;
import aplicacion.casosdeuso.portaldeingreso.gestiondetramites.GestionMatriculas;
import aplicacion.dto.CrearUsuarioCommand;
import dominio.modelo.*;
import dominio.puerto.externo.HashProvider;
import dominio.puerto.repositorio.RepositorioPeriodos;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder {

    private final GestionUsuarios gestionUsuarios;
    private final GestionDocentes gestionDocentes;
    private final GestionEstudiantes gestionEstudiantes;
    private final GestionPadres gestionPadres;
    private final GestionGrados gestionGrados;
    private final GestionAsignaturas gestionAsignaturas;
    private final GestionAulas gestionAulas;
    private final GestionGrupos gestionGrupos;
    private final GestionPeriodos gestionPeriodos;
    private final GestionMatriculas gestionMatriculas;
    private final HashProvider hashProvider;
    private final RepositorioPeriodos repoPeriodos;

    public DataSeeder(GestionUsuarios gestionUsuarios, GestionDocentes gestionDocentes,
                      GestionEstudiantes gestionEstudiantes, GestionPadres gestionPadres,
                      GestionGrados gestionGrados, GestionAsignaturas gestionAsignaturas,
                      GestionAulas gestionAulas, GestionGrupos gestionGrupos,
                      GestionPeriodos gestionPeriodos, GestionMatriculas gestionMatriculas,
                      HashProvider hashProvider, RepositorioPeriodos repoPeriodos) {
        this.gestionUsuarios = gestionUsuarios;
        this.gestionDocentes = gestionDocentes;
        this.gestionEstudiantes = gestionEstudiantes;
        this.gestionPadres = gestionPadres;
        this.gestionGrados = gestionGrados;
        this.gestionAsignaturas = gestionAsignaturas;
        this.gestionAulas = gestionAulas;
        this.gestionGrupos = gestionGrupos;
        this.gestionPeriodos = gestionPeriodos;
        this.gestionMatriculas = gestionMatriculas;
        this.hashProvider = hashProvider;
        this.repoPeriodos = repoPeriodos;
    }

    @Transactional
    public void run() {
        System.out.println("🚀 Iniciando Seeder (idempotente)...");
        String defaultPass = "WiredAcademy2026!";
        String hash = hashProvider.generarHash(defaultPass.toCharArray());

        // 1. Periodo
        if (repoPeriodos.buscarPorNombre("2026-I").isEmpty()) {
            gestionPeriodos.crearPeriodo("2026-I", "2026-01-01", "2026-07-15", "Activo");
            System.out.println("  ✓ Periodo 2026-I creado.");
        }
        PeriodoAcademico periodo = repoPeriodos.buscarPorNombre("2026-I").orElseThrow();

        // 2. Grados
        String[] nombresGrados = {"1er Grado", "2do Grado", "3er Grado", "4to Grado", "5to Grado", "6to Grado"};
        for (String n : nombresGrados) {
            if (gestionGrados.buscarPorNombreYNivel(n, "Primaria") == null) {
                gestionGrados.crearGrado(n, "Primaria");
            }
        }
        Grado grado1 = gestionGrados.buscarPorNombreYNivel("1er Grado", "Primaria");
        Grado grado2 = gestionGrados.buscarPorNombreYNivel("2do Grado", "Primaria");
        System.out.println("  ✓ Grados verificados/creados.");

        // 3. Asignaturas
        String[] nombresAsig = {"Matemática", "Lenguaje", "Ciencias", "Historia", "Inglés", "Arte", "Música", "Ed. Física", "Física", "Química"};
        for (String n : nombresAsig) {
            if (gestionAsignaturas.buscarAsignatura(n) == null) {
                gestionAsignaturas.crearAsignatura(n);
            }
        }
        List<Asignatura> todasAsig = gestionAsignaturas.listarTodos();
        System.out.println("  ✓ Asignaturas verificadas/creadas (" + todasAsig.size() + ").");

        // 4. Aulas
        for (int i = 1; i <= 10; i++) {
            String nombre = "Aula " + (100 + i);
            if (gestionAulas.buscarAula(nombre) == null) {
                gestionAulas.crearAula(nombre, 30 + (i * 2), "Pabellón " + ((i % 3) + 1), "Teoría", ModalidadAula.PRESENCIAL);
            }
        }
        System.out.println("  ✓ Aulas verificadas/creadas (" + gestionAulas.listarTodos().size() + ").");

        // 5. Usuarios administrativos
        crearUsuarioSiNoExiste("DIR-01", hash, "80000001", "Carlos", "Director", 45, Usuario.Rol.DIRECTOR, null);
        crearUsuarioSiNoExiste("CORD-01", hash, "80000002", "Ana", "Coordinadora", 40, Usuario.Rol.COORDINADOR, null);
        crearUsuarioSiNoExiste("SEC-01", hash, "80000003", "Luis", "Secretario 1", 30, Usuario.Rol.SECRETARIO, null);
        crearUsuarioSiNoExiste("SEC-02", hash, "80000004", "Maria", "Secretaria 2", 28, Usuario.Rol.SECRETARIO, null);
        System.out.println("  ✓ Admin/Director/Coordinador/Secretarios verificados/creados.");

        // 6. Docentes + asignaturas
        List<Docente> docentes = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            String codigo = "DOC-" + i;
            String dni = String.format("100000%02d", i);
            crearUsuarioSiNoExiste(codigo, hash, dni, "Docente" + i, "Apellido", 35 + i, Usuario.Rol.DOCENTE, "General");
            docentes.add(gestionDocentes.buscarPorCodigo(codigo));
        }
        for (int i = 0; i < docentes.size(); i++) {
            Docente d = docentes.get(i);
            Asignatura a1 = todasAsig.get(i % todasAsig.size());
            Asignatura a2 = todasAsig.get((i + 1) % todasAsig.size());
            if (!d.getAsignaturas().contains(a1)) gestionDocentes.agregarAsignatura(d, a1);
            if (!d.getAsignaturas().contains(a2)) gestionDocentes.agregarAsignatura(d, a2);
        }
        System.out.println("  ✓ Docentes verificados/creados (" + docentes.size() + ").");

        // 7. Grupos
        List<Grupo> grupos = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String nombre = "Grupo " + i + "A";
            Grado g = (i % 2 == 0) ? grado1 : grado2;
            if (gestionGrupos.buscarPorNombre(nombre) == null) {
                gestionGrupos.crearGrupo(nombre, g);
            }
            grupos.add(gestionGrupos.buscarPorNombre(nombre));
        }
        for (int i = 0; i < grupos.size(); i++) {
            if (grupos.get(i).getTutor() == null) {
                gestionGrupos.asignarTutor(grupos.get(i), docentes.get(i));
            }
        }
        System.out.println("  ✓ Grupos verificados/creados (" + grupos.size() + ").");

        // 8. Padres
        for (int i = 1; i <= 10; i++) {
            String dni = String.format("200000%02d", i);
            crearUsuarioSiNoExiste("PAD-" + i, hash, dni, "Padre" + i, "Familia", 40, Usuario.Rol.PADRE, null);
        }
        System.out.println("  ✓ Padres verificados/creados.");

        // 9. Estudiantes + vinculación a padres
        List<Estudiante> estudiantes = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            String codigo = "EST-" + i;
            String dni = String.format("300000%02d", i);
            crearUsuarioSiNoExiste(codigo, hash, dni, "Estudiante" + i, "Apellido", 12 + (i % 6), Usuario.Rol.ESTUDIANTE, null);
            estudiantes.add(gestionEstudiantes.buscarPorCodigo(codigo));
        }
        for (int i = 1; i <= 10; i++) {
            Padre p = gestionPadres.buscarPorCodigo("PAD-" + i);
            if (p != null && p.getEstudiantes().isEmpty()) {
                for (int j = 0; j < 5; j++) {
                    int estIdx = (i - 1) * 5 + j;
                    if (estIdx < estudiantes.size()) {
                        gestionPadres.vincularEstudiante(p, estudiantes.get(estIdx));
                    }
                }
            }
        }
        System.out.println("  ✓ Estudiantes verificados/creados (" + estudiantes.size() + ").");

        // 10. Matrículas
        int nuevas = 0;
        for (int i = 0; i < estudiantes.size(); i++) {
            Estudiante est = estudiantes.get(i);
            if (gestionMatriculas.buscarMatricula(est.getCodigo(), periodo.getNombre()) == null) {
                Grupo grp = grupos.get(i % grupos.size());
                List<Asignatura> asigs = List.of(todasAsig.get(0), todasAsig.get(1), todasAsig.get(2));
                gestionMatriculas.matricularEstudiante(est, periodo, grp, asigs, "2026-02-15", EstadoMatricula.ACTIVA);
                nuevas++;
            }
        }
        System.out.println("  ✓ Matrículas verificadas/creadas (nuevas: " + nuevas + ").");

        System.out.println("✅ Seeder finalizado.");
        System.out.println("   Contraseña para todos los usuarios generados: " + defaultPass);
    }

    private void crearUsuarioSiNoExiste(String codigo, String hash, String dni,
                                         String nombre, String apellido, int edad,
                                         Usuario.Rol rol, String especialidad) {
        if (gestionUsuarios.buscarPorCodigo(codigo) == null) {
            gestionUsuarios.agregarUsuario(new CrearUsuarioCommand(
                    codigo, hash, dni, nombre, apellido, edad, rol, especialidad));
        }
    }
}
