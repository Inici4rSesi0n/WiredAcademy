/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.aplicacion.casosdeuso.portaldeingreso.sistema;
import aplicacion.casosdeuso.portaldeingreso.sistema.BootstrapUseCase;
import dominio.puerto.repositorio.RepositorioGrados;
import dominio.puerto.repositorio.RepositorioPeriodos;
import dominio.puerto.repositorio.RepositorioTurnos;
import integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class BootstrapIT extends BaseIntegrationTest {
    @Autowired private BootstrapUseCase bootstrapUseCase;
    @Autowired private RepositorioTurnos repoTurnos;
    @Autowired private RepositorioGrados repoGrados;
    @Autowired private RepositorioPeriodos repoPeriodos;
    @Test
    public void testInicializarCatalogosCreaDatosBase() {
        bootstrapUseCase.inicializarCatalogos();
        assertFalse(repoTurnos.listarTodos().isEmpty());
        assertFalse(repoGrados.listarTodos().isEmpty());
        assertFalse(repoPeriodos.listarTodos().isEmpty());
    }
}
