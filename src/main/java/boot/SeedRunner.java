/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package boot;
import aplicacion.casosdeuso.portaldeingreso.sistema.BootstrapUseCase;
import dominio.puerto.externo.LoggerPort;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
public class SeedRunner {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(WiredAcademyApp.class);
        LoggerPort logger = ctx.getBean(LoggerPort.class);
        logger.audit("[SESSION] seed_mode_started");
        try {
            ctx.getBean(BootstrapUseCase.class).inicializarCatalogos();
            ctx.getBean(DataSeeder.class).run();
        } catch (Exception e) {
            logger.error("Error en seed mode: " + e.getMessage(), e);
            ctx.close();
            System.exit(1);
        }
        logger.audit("[SESSION] seed_mode_completed");
        ctx.close();
        System.exit(0);
    }
}
