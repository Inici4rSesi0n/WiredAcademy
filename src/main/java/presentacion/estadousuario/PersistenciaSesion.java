/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.estadousuario;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public final class PersistenciaSesion {
    private static final Path ARCHIVO = Paths.get(System.getProperty("user.home"), ".wiredacademy-session");
    private PersistenciaSesion() {}
    public static boolean guardar(String codigoUsuario) {
        if (codigoUsuario == null || codigoUsuario.isBlank()) return false;
        try {
            Files.writeString(ARCHIVO, codigoUsuario, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.err.println("No se pudo guardar la sesión: " + e.getMessage());
            return false;
        }
    }
    public static Optional<String> leer() {
        try {
            if (!Files.exists(ARCHIVO)) return Optional.empty();
            String codigo = Files.readString(ARCHIVO, StandardCharsets.UTF_8).trim();
            return codigo.isEmpty() ? Optional.empty() : Optional.of(codigo);
        } catch (IOException e) {
            return Optional.empty();
        }
    }
    public static boolean borrar() {
        try {
            return Files.deleteIfExists(ARCHIVO);
        } catch (IOException e) {
            System.err.println("No se pudo borrar la sesión: " + e.getMessage());
            return false;
        }
    }
}
