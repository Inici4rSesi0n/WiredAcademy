/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.sistema;
import aplicacion.puerto.ProveedorDatosIniciales;
import dominio.modelo.*;
import dominio.modelo.configuracion.ConfiguracionSistema;
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
    private final RepositorioConfiguracionSistema repoConfig;
    public BootstrapUseCase(HashProvider hashProvider, LoggerPort logger,
                            RepositorioAdministradores repoAdmin, RepositorioTurnos repoTurnos,
                            RepositorioGrados repoGrados, RepositorioPeriodos repoPeriodos,
                            RepositorioRecreos repoRecreos, RepositorioConfiguracionSistema repoConfig) {
        this.hashProvider = hashProvider;
        this.logger = logger;
        this.repoAdmin = repoAdmin;
        this.repoTurnos = repoTurnos;
        this.repoGrados = repoGrados;
        this.repoPeriodos = repoPeriodos;
        this.repoRecreos = repoRecreos;
        this.repoConfig = repoConfig;
    }
    @Transactional
    public void inicializarSistema(ProveedorDatosIniciales proveedor) {
        if (proveedor == null) throw new IllegalArgumentException("El proveedor no puede ser nulo");
        repoConfig.guardar(new ConfiguracionSistema(null, proveedor.getTipoInstitucion()));
        String hash = hashProvider.generarHash(proveedor.getContrasena());
        Administrador admin = new Administrador(proveedor.getCodigo(), hash, proveedor.getDni(),
                proveedor.getNombre(), proveedor.getApellido(), 30);
        repoAdmin.agregar(admin);
        logger.audit("op=CREATE entity=ADMINISTRADOR id=" + admin.getCodigo() + " | Initial setup");
        inicializarCatalogos();
    }
    @Transactional
    public void inicializarCatalogos() {
        if (repoTurnos.listarTodos().isEmpty()) {
            repoTurnos.agregar(new Turno("Mañana"));
            repoTurnos.agregar(new Turno("Tarde"));
            logger.info("Catálogo de Turnos inicializado.");
        }
        if (repoGrados.listarTodos().isEmpty()) {
            String[] nombres = {"1er Grado", "2do Grado", "3er Grado", "4to Grado", "5to Grado", "6to Grado"};
            for (String nombre : nombres) {
                repoGrados.agregar(new Grado(nombre, "Primaria"));
            }
            logger.info("Catálogo de Grados inicializado.");
        }
        if (repoPeriodos.listarTodos().isEmpty()) {
            repoPeriodos.agregar(new PeriodoAcademico("2026-I", "2026-01-01", "2026-12-31", "Activo"));
            logger.info("Catálogo de Periodos inicializado.");
        }
        if (repoRecreos.listarTodos().isEmpty()) {
            PeriodoAcademico periodo = repoPeriodos.buscarPorNombre("2026-I").orElse(null);
            if (periodo != null) {
                FranjaHoraria franja = new FranjaHoraria("Lunes", "10:15", "10:45");
                repoRecreos.agregar(new Recreo(franja, "Recreo", List.of(periodo)));
                logger.info("Catálogo de Recreos inicializado.");
            }
        }
    }
}
