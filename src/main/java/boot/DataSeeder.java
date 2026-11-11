/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package boot;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAulas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionClases;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionRecreos;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 *
 * @author inici4rsesi0n
 */
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
    private final GestionClases gestionClases;
    private final GestionRecreos gestionRecreos;
    private final HashProvider hashProvider;
    private final RepositorioPeriodos repoPeriodos;
    public DataSeeder(GestionUsuarios gestionUsuarios, GestionDocentes gestionDocentes,
                      GestionEstudiantes gestionEstudiantes, GestionPadres gestionPadres,
                      GestionGrados gestionGrados, GestionAsignaturas gestionAsignaturas,
                      GestionAulas gestionAulas, GestionGrupos gestionGrupos,
                      GestionPeriodos gestionPeriodos, GestionMatriculas gestionMatriculas,
                      GestionClases gestionClases, GestionRecreos gestionRecreos,
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
        this.gestionClases = gestionClases;
        this.gestionRecreos = gestionRecreos;
        this.hashProvider = hashProvider;
        this.repoPeriodos = repoPeriodos;
    }
    @Transactional
    public void run() {
        System.out.println("🚀 Iniciando Seeder (idempotente)...");
        String defaultPass = "WiredAcademy2026!";
        String hash = hashProvider.generarHash(defaultPass.toCharArray());
        PeriodoAcademico periodo = seedPeriodo();
        seedGrados();
        List<Asignatura> asignaturas = seedAsignaturas();
        List<Aula> aulas = seedAulas();
        crearUsuariosAdmin(hash);
        List<Docente> docentes = seedDocentes(hash, asignaturas);
        List<Grupo> grupos = seedGrupos(docentes);
        seedPadres(hash);
        List<Estudiante> estudiantes = seedEstudiantes(hash);
        vincularPadresEstudiantes(estudiantes);
        seedMatriculas(estudiantes, grupos, asignaturas, periodo);
        seedRecreos(periodo);
        seedClases(grupos, asignaturas, aulas, docentes, periodo);
        System.out.println("✅ Seeder finalizado.");
        System.out.println("   Contraseña para todos los usuarios generados: " + defaultPass);
    }
    private PeriodoAcademico seedPeriodo() {
        if (repoPeriodos.buscarPorNombre("2026-I").isEmpty()) {
            gestionPeriodos.crearPeriodo("2026-I", "2026-01-01", "2026-07-15", "Activo");
            System.out.println("  ✓ Periodo 2026-I creado.");
        }
        return repoPeriodos.buscarPorNombre("2026-I").orElseThrow();
    }
    private void seedGrados() {
        String[] nombres = {"1er Grado", "2do Grado", "3er Grado", "4to Grado", "5to Grado", "6to Grado"};
        for (String n : nombres) {
            if (gestionGrados.buscarPorNombreYNivel(n, "Primaria") == null) {
                gestionGrados.crearGrado(n, "Primaria");
            }
        }
        System.out.println("  ✓ Grados verificados/creados.");
    }
    private List<Asignatura> seedAsignaturas() {
        String[] nombres = {"Matemática", "Lenguaje", "Ciencias", "Historia", "Inglés", "Arte", "Música", "Ed. Física", "Física", "Química"};
        for (String n : nombres) {
            if (gestionAsignaturas.buscarAsignatura(n) == null) {
                gestionAsignaturas.crearAsignatura(n);
            }
        }
        List<Asignatura> todas = gestionAsignaturas.listarTodos();
        System.out.println("  ✓ Asignaturas verificadas/creadas (" + todas.size() + ").");
        return todas;
    }
    private List<Aula> seedAulas() {
        for (int i = 1; i <= 10; i++) {
            String nombre = "Aula " + (100 + i);
            if (gestionAulas.buscarAula(nombre) == null) {
                gestionAulas.crearAula(nombre, 30 + (i * 2), "Pabellón " + ((i % 3) + 1), "Teoría", ModalidadAula.PRESENCIAL);
            }
        }
        List<Aula> todas = gestionAulas.listarTodos();
        System.out.println("  ✓ Aulas verificadas/creadas (" + todas.size() + ").");
        return todas;
    }
    private void crearUsuariosAdmin(String hash) {
        crearUsuarioSiNoExiste("DIR-01", hash, "80000001", "Carlos", "Director", 45, Usuario.Rol.DIRECTOR, null);
        crearUsuarioSiNoExiste("CORD-01", hash, "80000002", "Ana", "Coordinadora", 40, Usuario.Rol.COORDINADOR, null);
        crearUsuarioSiNoExiste("SEC-01", hash, "80000003", "Luis", "Secretario 1", 30, Usuario.Rol.SECRETARIO, null);
        crearUsuarioSiNoExiste("SEC-02", hash, "80000004", "Maria", "Secretaria 2", 28, Usuario.Rol.SECRETARIO, null);
        System.out.println("  ✓ Admin/Director/Coordinador/Secretarios verificados/creados.");
    }
    private List<Docente> seedDocentes(String hash, List<Asignatura> asignaturas) {
        List<Docente> docentes = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            String codigo = "DOC-" + i;
            String dni = String.format("100000%02d", i);
            crearUsuarioSiNoExiste(codigo, hash, dni, "Docente" + i, "Apellido", 35 + i, Usuario.Rol.DOCENTE, "General");
            docentes.add(gestionDocentes.buscarPorCodigo(codigo));
        }
        for (int i = 0; i < docentes.size(); i++) {
            Docente d = docentes.get(i);
            Asignatura a1 = asignaturas.get(i % asignaturas.size());
            Asignatura a2 = asignaturas.get((i + 1) % asignaturas.size());
            if (!d.getAsignaturas().contains(a1)) gestionDocentes.agregarAsignatura(d, a1);
            if (!d.getAsignaturas().contains(a2)) gestionDocentes.agregarAsignatura(d, a2);
        }
        System.out.println("  ✓ Docentes verificados/creados (" + docentes.size() + ").");
        return docentes;
    }
    private List<Grupo> seedGrupos(List<Docente> docentes) {
        Grado grado1 = gestionGrados.buscarPorNombreYNivel("1er Grado", "Primaria");
        Grado grado2 = gestionGrados.buscarPorNombreYNivel("2do Grado", "Primaria");
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
        return grupos;
    }
    private void seedPadres(String hash) {
        for (int i = 1; i <= 10; i++) {
            String dni = String.format("200000%02d", i);
            crearUsuarioSiNoExiste("PAD-" + i, hash, dni, "Padre" + i, "Familia", 40, Usuario.Rol.PADRE, null);
        }
        System.out.println("  ✓ Padres verificados/creados.");
    }
    private List<Estudiante> seedEstudiantes(String hash) {
        List<Estudiante> estudiantes = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            String codigo = "EST-" + i;
            String dni = String.format("300000%02d", i);
            crearUsuarioSiNoExiste(codigo, hash, dni, "Estudiante" + i, "Apellido", 12 + (i % 6), Usuario.Rol.ESTUDIANTE, null);
            estudiantes.add(gestionEstudiantes.buscarPorCodigo(codigo));
        }
        System.out.println("  ✓ Estudiantes verificados/creados (" + estudiantes.size() + ").");
        return estudiantes;
    }
    private void vincularPadresEstudiantes(List<Estudiante> estudiantes) {
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
    }
    private void seedMatriculas(List<Estudiante> estudiantes, List<Grupo> grupos, List<Asignatura> asignaturas, PeriodoAcademico periodo) {
        int nuevas = 0;
        for (int i = 0; i < estudiantes.size(); i++) {
            Estudiante est = estudiantes.get(i);
            if (gestionMatriculas.buscarMatricula(est.getCodigo(), periodo.getNombre()) == null) {
                Grupo grp = grupos.get(i % grupos.size());
                List<Asignatura> asigs = List.of(asignaturas.get(0), asignaturas.get(1), asignaturas.get(2));
                gestionMatriculas.matricularEstudiante(est, periodo, grp, asigs, "2026-02-15", EstadoMatricula.ACTIVA);
                nuevas++;
            }
        }
        System.out.println("  ✓ Matrículas verificadas/creadas (nuevas: " + nuevas + ").");
    }
    private void seedRecreos(PeriodoAcademico periodo) {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};
        int nuevos = 0;
        for (String dia : dias) {
            FranjaHoraria franja = new FranjaHoraria(dia, "10:15", "10:45");
            boolean existe = gestionRecreos.listarTodos().stream()
                    .anyMatch(r -> r.getFranja().equals(franja) && r.aplicaEn(periodo));
            if (!existe) {
                gestionRecreos.crearRecreo(franja, "Recreo " + dia, List.of(periodo));
                nuevos++;
            }
        }
        System.out.println("  ✓ Recreos verificados/creados (nuevos: " + nuevos + ", total: " + gestionRecreos.listarTodos().size() + ").");
    }
    private void seedClases(List<Grupo> grupos, List<Asignatura> asignaturas, List<Aula> aulas, List<Docente> docentes, PeriodoAcademico periodo) {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};
        String[][] franjas = {{"08:00", "10:00"}, {"10:45", "12:15"}, {"12:30", "14:00"}, {"14:15", "15:45"}};
        Set<String> docenteOcupado = new HashSet<>();
        Set<String> aulaOcupada = new HashSet<>();
        Set<String> grupoOcupado = new HashSet<>();
        int nuevas = 0;
        int saltadas = 0;
        for (int g = 0; g < grupos.size(); g++) {
            Grupo grupo = grupos.get(g);
            String nombreGrado = grupo.getGrado().getNombre();
            for (int d = 0; d < dias.length; d++) {
                for (int f = 0; f < franjas.length; f++) {
                    if ((g * 5 + d * 3 + f * 2) % 10 >= 8) continue;
                    String dia = dias[d];
                    String horaInicio = franjas[f][0];
                    String horaFin = franjas[f][1];
                    if (gestionClases.buscarClase(nombreGrado, grupo.getNombre(), dia, horaInicio, horaFin) != null) {
                        saltadas++;
                        continue;
                    }
                    Asignatura asignatura = asignaturas.get((g * 2 + d + f) % asignaturas.size());
                    Docente docente = buscarDocenteLibre(docentes, asignatura, dia, horaInicio, docenteOcupado, (g * 2 + d + f));
                    if (docente == null) continue;
                    Aula aula = buscarAulaLibre(aulas, dia, horaInicio, aulaOcupada, (g * 3 + d + f));
                    if (aula == null) continue;
                    String keyGrupo = grupo.getNombre() + "|" + dia + "|" + horaInicio;
                    if (grupoOcupado.contains(keyGrupo)) continue;
                    gestionClases.crearClase(dia, horaInicio, horaFin, asignatura, docente, grupo, aula, periodo);
                    docenteOcupado.add(docente.getCodigo() + "|" + dia + "|" + horaInicio);
                    aulaOcupada.add(aula.getNombre() + "|" + dia + "|" + horaInicio);
                    grupoOcupado.add(keyGrupo);
                    nuevas++;
                }
            }
        }
        System.out.println("  ✓ Clases verificadas/creadas (nuevas: " + nuevas + ", saltadas: " + saltadas + ", total: " + gestionClases.listarClases().size() + ").");
    }
    private Docente buscarDocenteLibre(List<Docente> docentes, Asignatura asignatura, String dia, String horaInicio, Set<String> ocupados, int offset) {
        int inicio = offset % docentes.size();
        for (int i = 0; i < docentes.size(); i++) {
            Docente doc = docentes.get((inicio + i) % docentes.size());
            boolean tieneAsig = doc.getAsignaturas().stream().anyMatch(a -> a.equals(asignatura));
            String key = doc.getCodigo() + "|" + dia + "|" + horaInicio;
            if (tieneAsig && !ocupados.contains(key)) return doc;
        }
        return null;
    }
    private Aula buscarAulaLibre(List<Aula> aulas, String dia, String horaInicio, Set<String> ocupadas, int offset) {
        int inicio = offset % aulas.size();
        for (int i = 0; i < aulas.size(); i++) {
            Aula au = aulas.get((inicio + i) % aulas.size());
            String key = au.getNombre() + "|" + dia + "|" + horaInicio;
            if (!ocupadas.contains(key)) return au;
        }
        return null;
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
