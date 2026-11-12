/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.sistema;
import aplicacion.puerto.ProveedorDatosIniciales;
import dominio.modelo.*;
import dominio.modelo.configuracion.ConfiguracionSistema;
import dominio.modelo.configuracion.TipoInstitucion;
import dominio.puerto.externo.HashProvider;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class BootstrapUseCase {
    private final HashProvider hashProvider;
    private final LoggerPort logger;
    private final RepositorioAdministradores repoAdmin;
    private final RepositorioTurnos repoTurnos;
    private final RepositorioGrados repoGrados;
    private final RepositorioPeriodos repoPeriodos;
    private final RepositorioRecreos repoRecreos;
    private final RepositorioAsignaturas repoAsignaturas;
    private final RepositorioAulas repoAulas;
    private final RepositorioConfiguracionSistema repoConfig;
    public BootstrapUseCase(HashProvider hashProvider, LoggerPort logger,
                            RepositorioAdministradores repoAdmin, RepositorioTurnos repoTurnos,
                            RepositorioGrados repoGrados, RepositorioPeriodos repoPeriodos,
                            RepositorioRecreos repoRecreos, RepositorioAsignaturas repoAsignaturas,
                            RepositorioAulas repoAulas, RepositorioConfiguracionSistema repoConfig) {
        this.hashProvider = hashProvider;
        this.logger = logger;
        this.repoAdmin = repoAdmin;
        this.repoTurnos = repoTurnos;
        this.repoGrados = repoGrados;
        this.repoPeriodos = repoPeriodos;
        this.repoRecreos = repoRecreos;
        this.repoAsignaturas = repoAsignaturas;
        this.repoAulas = repoAulas;
        this.repoConfig = repoConfig;
    }
    @Transactional
    public void inicializarSistema(ProveedorDatosIniciales proveedor) {
        if (proveedor == null) throw new IllegalArgumentException("El proveedor no puede ser nulo");
        TipoInstitucion tipo = proveedor.getTipoInstitucion();
        repoConfig.guardar(new ConfiguracionSistema(null, tipo));
        String hash = hashProvider.generarHash(proveedor.getContrasena());
        Administrador admin = new Administrador(proveedor.getCodigo(), hash, proveedor.getDni(),
                proveedor.getNombre(), proveedor.getApellido(), 30);
        repoAdmin.agregar(admin);
        logger.audit("op=CREATE entity=ADMINISTRADOR id=" + admin.getCodigo() + " | Initial setup | tipo=" + tipo);
        inicializarCatalogos(tipo);
    }
    @Transactional
    public void inicializarCatalogos() {
        TipoInstitucion tipo = repoConfig.obtener()
                .map(ConfiguracionSistema::getTipoInstitucion)
                .orElse(TipoInstitucion.COLEGIO);
        inicializarCatalogos(tipo);
    }
    private void inicializarCatalogos(TipoInstitucion tipo) {
        if (repoTurnos.listarTodos().isEmpty()) {
            repoTurnos.agregar(new Turno("Mañana"));
            repoTurnos.agregar(new Turno("Tarde"));
            if (tipo != TipoInstitucion.COLEGIO) repoTurnos.agregar(new Turno("Noche"));
            logger.info("Catálogo de Turnos inicializado.");
        }
        if (repoGrados.listarTodos().isEmpty()) {
            if (tipo == TipoInstitucion.COLEGIO) {
                String[] prim = {"1er Grado", "2do Grado", "3er Grado", "4to Grado", "5to Grado", "6to Grado"};
                for (String n : prim) repoGrados.agregar(new Grado(n, "Primaria"));
                String[] sec = {"1er Año", "2do Año", "3er Año", "4to Año", "5to Año"};
                for (String n : sec) repoGrados.agregar(new Grado(n, "Secundaria"));
            } else if (tipo == TipoInstitucion.INSTITUTO) {
                String[] mods = {"Módulo I", "Módulo II", "Módulo III", "Módulo IV"};
                for (String n : mods) repoGrados.agregar(new Grado(n, "Técnico"));
            } else {
                String[] cics = {"Ciclo I", "Ciclo II", "Ciclo III", "Ciclo IV", "Ciclo V", "Ciclo VI", "Ciclo VII", "Ciclo VIII", "Ciclo IX", "Ciclo X"};
                for (String n : cics) repoGrados.agregar(new Grado(n, "Pregrado"));
            }
            logger.info("Catálogo de Grados/Ciclos inicializado para " + tipo);
        }
        if (repoAsignaturas.listarTodos().isEmpty()) {
            if (tipo == TipoInstitucion.COLEGIO) {
                String[] asigs = {"Matemática", "Comunicación", "Ciencia y Tecnología", "Personal Social", "Inglés", "Arte", "Educación Física"};
                for (String n : asigs) repoAsignaturas.agregar(new Asignatura(n));
            } else if (tipo == TipoInstitucion.INSTITUTO) {
                String[] asigs = {"Fundamentos de Programación", "Redes", "Base de Datos", "Ofimática", "Matemática Aplicada"};
                for (String n : asigs) repoAsignaturas.agregar(new Asignatura(n));
            } else {
                String[] asigs = {"Cálculo I", "Álgebra Lineal", "Introducción a la Programación", "Física I", "Metodología de la Investigación"};
                for (String n : asigs) repoAsignaturas.agregar(new Asignatura(n));
            }
            logger.info("Catálogo de Asignaturas inicializado.");
        }
        if (repoAulas.listarTodos().isEmpty()) {
            if (tipo == TipoInstitucion.COLEGIO) {
                repoAulas.agregar(new Aula("Aula 1A", 30, "Piso 1", "Teoría", ModalidadAula.PRESENCIAL));
                repoAulas.agregar(new Aula("Aula 1B", 30, "Piso 1", "Teoría", ModalidadAula.PRESENCIAL));
                repoAulas.agregar(new Aula("Laboratorio", 25, "Piso 2", "Laboratorio", ModalidadAula.PRESENCIAL));
            } else {
                repoAulas.agregar(new Aula("Aula 101", 40, "Pabellón A", "Teoría", ModalidadAula.PRESENCIAL));
                repoAulas.agregar(new Aula("Aula 102", 40, "Pabellón A", "Teoría", ModalidadAula.PRESENCIAL));
                repoAulas.agregar(new Aula("Laboratorio de Cómputo", 35, "Pabellón B", "Laboratorio", ModalidadAula.PRESENCIAL));
                repoAulas.agregar(new Aula("Auditorio", 100, "Pabellón Central", "Auditorio", ModalidadAula.PRESENCIAL));
            }
            logger.info("Catálogo de Aulas inicializado.");
        }
        if (repoPeriodos.listarTodos().isEmpty()) {
            repoPeriodos.agregar(new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", "Activo"));
            logger.info("Catálogo de Periodos inicializado.");
        }
        if (tipo != TipoInstitucion.UNIVERSIDAD && repoRecreos.listarTodos().isEmpty()) {
            PeriodoAcademico periodo = repoPeriodos.buscarPorNombre("2026-I").orElse(null);
            if (periodo != null) {
                FranjaHoraria franja = new FranjaHoraria("Lunes", "10:15", "10:45");
                repoRecreos.agregar(new Recreo(franja, "Recreo", List.of(periodo)));
                logger.info("Catálogo de Recreos inicializado.");
            }
        }
    }
}
