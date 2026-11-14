/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package boot;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.*;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.*;
import aplicacion.casosdeuso.portaldeingreso.gestiondetramites.GestionMatriculas;
import aplicacion.casosdeuso.portaldeingreso.publicaciones.GestionPublicaciones;
import aplicacion.dto.CrearUsuarioCommand;
import dominio.modelo.*;
import dominio.modelo.configuracion.TipoInstitucion;
import dominio.puerto.externo.HashProvider;
import dominio.puerto.repositorio.RepositorioConfiguracionSistema;
import dominio.puerto.repositorio.RepositorioPeriodos;
import dominio.puerto.repositorio.RepositorioEvaluaciones;
import dominio.puerto.repositorio.RepositorioCalificaciones;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
    private final GestionPublicaciones gestionPublicaciones;
    private final HashProvider hashProvider;
    private final RepositorioPeriodos repoPeriodos;
    private final RepositorioConfiguracionSistema repoConfig;
    private final RepositorioEvaluaciones repoEvaluaciones;
    private final RepositorioCalificaciones repoCalificaciones;
    public DataSeeder(GestionUsuarios gestionUsuarios, GestionDocentes gestionDocentes,
                      GestionEstudiantes gestionEstudiantes, GestionPadres gestionPadres,
                      GestionGrados gestionGrados, GestionAsignaturas gestionAsignaturas,
                      GestionAulas gestionAulas, GestionGrupos gestionGrupos,
                      GestionPeriodos gestionPeriodos, GestionMatriculas gestionMatriculas,
                      GestionClases gestionClases, GestionRecreos gestionRecreos,
                      GestionPublicaciones gestionPublicaciones,
                      HashProvider hashProvider, RepositorioPeriodos repoPeriodos,
                      RepositorioConfiguracionSistema repoConfig, RepositorioEvaluaciones repoEvaluaciones,
                      RepositorioCalificaciones repoCalificaciones) {
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
        this.gestionPublicaciones = gestionPublicaciones;
        this.hashProvider = hashProvider;
        this.repoPeriodos = repoPeriodos;
        this.repoConfig = repoConfig;
        this.repoEvaluaciones = repoEvaluaciones;
        this.repoCalificaciones = repoCalificaciones;
    }
    @Transactional
    public void run() {
        System.out.println("🚀 Iniciando Seeder (idempotente)...");
        TipoInstitucion tipo = repoConfig.obtener()
                .map(c -> c.getTipoInstitucion())
                .orElse(TipoInstitucion.COLEGIO);
        System.out.println("  ⚙ Tipo de institución detectado: " + tipo);
        String defaultPass = "WiredAcademy2026!";
        String hash = hashProvider.generarHash(defaultPass.toCharArray());
        PeriodoAcademico periodo = seedPeriodo();
        List<Asignatura> asignaturas = seedAsignaturas();
        List<Aula> aulas = seedAulas();
        crearUsuariosAdmin(hash);
        List<Docente> docentes = seedDocentes(hash, asignaturas);
        List<Grupo> grupos = seedGrupos(tipo, docentes);
        seedPadres(hash);
        List<Estudiante> estudiantes = seedEstudiantes(hash);
        vincularPadresEstudiantes(estudiantes);
        seedMatriculas(tipo, estudiantes, grupos, asignaturas, periodo);
        if (tipo != TipoInstitucion.UNIVERSIDAD) {
            seedRecreos(periodo);
        } else {
            System.out.println("  ✓ Recreos omitidos (Universidad).");
        }
        seedClases(tipo, grupos, asignaturas, aulas, docentes, periodo);
        seedEvaluacionesYCalificaciones(estudiantes, asignaturas);
        seedPublicacionesCMS(asignaturas, docentes, periodo);
        System.out.println("✅ Seeder finalizado.");
        System.out.println("   Contraseña para todos los usuarios generados: " + defaultPass);
    }
    private PeriodoAcademico seedPeriodo() {
        return repoPeriodos.buscarPorNombre("2026-I").orElseThrow();
    }
    private List<Asignatura> seedAsignaturas() {
        return gestionAsignaturas.listarTodos();
    }
    private List<Aula> seedAulas() {
        return gestionAulas.listarTodos();
    }
    private void crearUsuariosAdmin(String hash) {
        crearUsuarioSiNoExiste("DIR-01", hash, "80000001", "Carlos", "Director", 45, Usuario.Rol.DIRECTOR, null);
        crearUsuarioSiNoExiste("SUBDIR-01", hash, "80000005", "Luis", "Subdirector", 42, Usuario.Rol.SUBDIRECTOR, null);
        crearUsuarioSiNoExiste("CORD-01", hash, "80000002", "Ana", "Coordinadora", 40, Usuario.Rol.COORDINADOR, null);
        crearUsuarioSiNoExiste("SEC-01", hash, "80000003", "Luis", "Secretario 1", 30, Usuario.Rol.SECRETARIO, null);
        crearUsuarioSiNoExiste("SEC-02", hash, "80000004", "Maria", "Secretaria 2", 28, Usuario.Rol.SECRETARIO, null);
        crearUsuarioSiNoExiste("TES-01", hash, "80000006", "Pedro", "Tesorero", 38, Usuario.Rol.TESORERO, null);
        crearUsuarioSiNoExiste("PSI-01", hash, "80000007", "Lucia", "Psicologa", 35, Usuario.Rol.PSICOLOGO, null);
        System.out.println("  ✓ Admin/Director/Subdirector/Coordinador/Secretarios/Tesorero/Psicologo verificados/creados.");
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
    private List<Grupo> seedGrupos(TipoInstitucion tipo, List<Docente> docentes) {
        List<Grupo> grupos = new ArrayList<>();
        List<Grado> grados = gestionGrados.listarTodos();
        Modalidad[] modalidades = {Modalidad.PRESENCIAL, Modalidad.VIRTUAL, Modalidad.REMOTO, Modalidad.PRESENCIAL, Modalidad.VIRTUAL};
        for (int i = 0; i < 5; i++) {
            String nombre = (tipo == TipoInstitucion.COLEGIO) ? "Grupo " + (i+1) + "A" : "Sección " + (char)('A' + i);
            Grado g = grados.get(i % grados.size());
            if (gestionGrupos.buscarPorNombre(nombre) == null) gestionGrupos.crearGrupo(nombre, g, modalidades[i]);
            grupos.add(gestionGrupos.buscarPorNombre(nombre));
        }
        for (int i = 0; i < grupos.size(); i++) {
            if (grupos.get(i).getTutor() == null) gestionGrupos.asignarTutor(grupos.get(i), docentes.get(i));
        }
        System.out.println("  ✓ Grupos/Secciones verificados/creados (" + grupos.size() + ").");
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
                    if (estIdx < estudiantes.size()) gestionPadres.vincularEstudiante(p, estudiantes.get(estIdx));
                }
            }
        }
    }
    private void seedMatriculas(TipoInstitucion tipo, List<Estudiante> estudiantes, List<Grupo> grupos, List<Asignatura> asignaturas, PeriodoAcademico periodo) {
        int nuevas = 0;
        for (int i = 0; i < estudiantes.size(); i++) {
            Estudiante est = estudiantes.get(i);
            if (gestionMatriculas.buscarMatricula(est.getCodigo(), periodo.getNombre()) == null) {
                if (tipo == TipoInstitucion.COLEGIO) {
                    Grupo grp = grupos.get(i % grupos.size());
                    List<Asignatura> asigs = new ArrayList<>(asignaturas);
                    gestionMatriculas.matricularEstudiante(est, periodo, grp, asigs, "2026-02-15", EstadoMatricula.ACTIVA);
                } else {
                    List<InscripcionAsignatura> inscripciones = new ArrayList<>();
                    for (Asignatura asig : asignaturas) {
                        Grupo grp = grupos.get(i % grupos.size());
                        inscripciones.add(new InscripcionAsignatura(asig, grp));
                    }
                    gestionMatriculas.matricularPorAsignaturas(est, periodo, inscripciones, "2026-02-15", EstadoMatricula.ACTIVA);
                }
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
            boolean existe = gestionRecreos.listarTodos().stream().anyMatch(r -> r.getFranja().equals(franja) && r.aplicaEn(periodo));
            if (!existe) {
                gestionRecreos.crearRecreo(franja, "Recreo " + dia, List.of(periodo));
                nuevos++;
            }
        }
        System.out.println("  ✓ Recreos verificados/creados (nuevos: " + nuevos + ", total: " + gestionRecreos.listarTodos().size() + ").");
    }
    private void seedClases(TipoInstitucion tipo, List<Grupo> grupos, List<Asignatura> asignaturas, List<Aula> aulas, List<Docente> docentes, PeriodoAcademico periodo) {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};
        String[][] franjas = {{"08:00", "10:00"}, {"10:45", "12:15"}, {"12:30", "14:00"}, {"14:15", "15:45"}};
        if (tipo == TipoInstitucion.UNIVERSIDAD) {
            franjas = new String[][]{{"07:00", "09:00"}, {"09:00", "11:00"}, {"11:00", "13:00"}, {"14:00", "16:00"}};
        }
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
                    try {
                        gestionClases.crearClase(dia, horaInicio, horaFin, asignatura, docente, grupo, aula, periodo);
                        docenteOcupado.add(docente.getCodigo() + "|" + dia + "|" + horaInicio);
                        aulaOcupada.add(aula.getNombre() + "|" + dia + "|" + horaInicio);
                        grupoOcupado.add(keyGrupo);
                        nuevas++;
                    } catch (IllegalArgumentException e) {
                        saltadas++;
                    }
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
    private void seedEvaluacionesYCalificaciones(List<Estudiante> estudiantes, List<Asignatura> asignaturas) {
        if (repoEvaluaciones.listarTodos().isEmpty()) {
            System.out.println("  ✓ Iniciando creación de Evaluaciones y Calificaciones de prueba...");
            Random random = new Random();
            for (Asignatura asignatura : asignaturas) {
                Evaluacion pc1 = new Evaluacion(null, "Práctica Calificada 1 (PC1)", 0.2, asignatura);
                Evaluacion pc2 = new Evaluacion(null, "Práctica Calificada 2 (PC2)", 0.2, asignatura);
                Evaluacion exfn = new Evaluacion(null, "Examen Final (EXFN)", 0.6, asignatura);
                repoEvaluaciones.agregar(pc1);
                repoEvaluaciones.agregar(pc2);
                repoEvaluaciones.agregar(exfn);
                List<Evaluacion> evs = repoEvaluaciones.buscarPorAsignatura(asignatura.getNombre());
                Evaluacion pc1Saved = evs.stream().filter(e -> e.getNombre().contains("PC1")).findFirst().orElse(null);
                Evaluacion pc2Saved = evs.stream().filter(e -> e.getNombre().contains("PC2")).findFirst().orElse(null);
                for (int i = 0; i < Math.min(10, estudiantes.size()); i++) {
                    Estudiante est = estudiantes.get(i);
                    int n1 = 10 + random.nextInt(11);
                    int n2 = 10 + random.nextInt(11);
                    if (pc1Saved != null) repoCalificaciones.agregar(new Calificacion(null, pc1Saved, est, (double) n1));
                    if (pc2Saved != null) repoCalificaciones.agregar(new Calificacion(null, pc2Saved, est, (double) n2));
                }
            }
            System.out.println("  ✓ Evaluaciones y Calificaciones creadas exitosamente.");
        }
    }
    private void seedPublicacionesCMS(List<Asignatura> asignaturas, List<Docente> docentes, PeriodoAcademico periodo) {
        if (!gestionPublicaciones.listarTodos().isEmpty()) {
            System.out.println("  ✓ Publicaciones CMS ya existen. Omitiendo.");
            return;
        }
        System.out.println("  ✓ Iniciando creación de Publicaciones CMS (Materiales, Tareas, Foros, Anuncios)...");
        Random random = new Random();
        int publicaciones = 0;
        LocalDateTime fechaLimite = LocalDateTime.now().plusDays(7);
        for (int i = 0; i < asignaturas.size(); i++) {
            Asignatura asig = asignaturas.get(i);
            Docente autor = docentes.get(i % docentes.size());
            // Materiales (2 por asignatura)
            gestionPublicaciones.crearPublicacion(
                    "Material Semana 1 - " + asig.getNombre(),
                    "Lecturas y recursos de la primera semana",
                    TipoPublicacion.MATERIAL, 1, null, "/docs/semana1.pdf", asig, autor);
            gestionPublicaciones.crearPublicacion(
                    "Material Semana 2 - " + asig.getNombre(),
                    "Lecturas y recursos de la segunda semana",
                    TipoPublicacion.MATERIAL, 2, null, "/docs/semana2.pdf", asig, autor);
            // Tareas (2 por asignatura)
            gestionPublicaciones.crearPublicacion(
                    "Tarea 1: Análisis - " + asig.getNombre(),
                    "Realizar un análisis de los conceptos vistos en clase.",
                    TipoPublicacion.TAREA, 1, fechaLimite, "/docs/tarea1.pdf", asig, autor);
            gestionPublicaciones.crearPublicacion(
                    "Tarea 2: Ensayo - " + asig.getNombre(),
                    "Escribir un ensayo de 1000 palabras.",
                    TipoPublicacion.TAREA, 2, fechaLimite.plusDays(7), "/docs/tarea2.pdf", asig, autor);
            // Evaluación (1 por asignatura)
            gestionPublicaciones.crearPublicacion(
                    "Práctica Calificada 1 - " + asig.getNombre(),
                    "Evaluación de los temas de la semana 1 y 2.",
                    TipoPublicacion.EVALUACION, null, fechaLimite.plusDays(14), "/docs/pc1.pdf", asig, autor);
            // Foro (1 por asignatura)
            gestionPublicaciones.crearPublicacion(
                    "Foro de Consultas - " + asig.getNombre(),
                    "Espacio para preguntas sobre el curso.",
                    TipoPublicacion.FORO, null, null, null, asig, autor);
            publicaciones += 6;
        }
        // Anuncios globales (2)
        Docente director = docentes.get(0);
        gestionPublicaciones.crearPublicacion(
                "Bienvenidos al Periodo 2026-I",
                "Les damos la bienvenida al nuevo periodo académico. Revisen sus horarios y aulas.",
                TipoPublicacion.ANUNCIO, null, null, null, null, director);
        gestionPublicaciones.crearPublicacion(
                "Suspensión de clases por feriado",
                "El día 15 de noviembre no habrá clases por feriado nacional.",
                TipoPublicacion.ANUNCIO, null, null, null, null, director);
        publicaciones += 2;
        System.out.println("  ✓ Publicaciones CMS creadas (" + publicaciones + ").");
    }
    private void crearUsuarioSiNoExiste(String codigo, String hash, String dni, String nombre, String apellido, int edad, Usuario.Rol rol, String especialidad) {
        if (gestionUsuarios.buscarPorCodigo(codigo) == null) {
            gestionUsuarios.agregarUsuario(new CrearUsuarioCommand(codigo, hash, dni, nombre, apellido, edad, rol, especialidad));
        }
    }
}
