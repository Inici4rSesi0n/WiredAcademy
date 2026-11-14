# Flujo: Login

---

## 1. Visión General

Pantalla de autenticación. Valida credenciales con BCrypt y gestiona el ciclo de vida de la sesión (RAM + persistencia opcional en `~/.wiredacademy-session`).

---

## 2. Contrato del Checkbox "Mantener sesión"

| Caso | Sesión en RAM (`SesionUsuario`) | Archivo `~/.wiredacademy-session` | Siguiente arranque |
|---|---|---|---|
| **A. Sin marcar → navegar a Dashboard** | ✅ Activa (efímera del proceso JVM) | ❌ No se escribe. Si existía de antes, **se borra** | 🔑 Pide login |
| **B. Marcado → navegar a Dashboard** | ✅ Activa (efímera del proceso JVM) | ✅ Se escribe con el código del usuario | 🔁 Auto-login |
| **C. "Cerrar sesión" (explícito)** | ❌ Limpiada | ❌ Borrada | 🔑 Pide login |
| **D. "Volver al Menú Principal" sin persistencia** | ❌ **Logout automático** | ❌ Ya estaba vacío | 🔑 Pide login |
| **E. "Volver al Menú Principal" con persistencia** | ✅ Activa | ✅ Intacto | 🔁 Auto-login |

> **Nota A**: "Sin marcar" implica sesión **estrictamente efímera entre pantallas**. Al volver al Menú Principal (`Dashboard → Main`) sin archivo persistido, `MainController.navegarAVista("main", ...)` ejecuta logout automático con evento `[SESSION] logout_auto motivo=volver_a_main_sin_persistencia`. El panel de sesión no aparece en Main y el próximo portal exige login.
>
> **Nota B/E**: "Con marcar" mantiene la sesión tanto entre pantallas (Main ↔ Dashboard) como entre reinicios del proceso (auto-login).

---

## 3. Flujo Paso a Paso

1. Usuario ingresa código y contraseña.
2. `LoginController.handleLogin` registra `[SESSION] login_intento` (audit) con `codigo` y `rememberMe`.
3. `AutenticarUsuario` valida con BCrypt y setea MDC con el código si es válido.
4. **Rama A (sin marcar):** `PersistenciaSesion.borrar()` (best-effort).
   **Rama B (marcado):** `PersistenciaSesion.guardar(codigo)`.
5. Log `[SESSION] login_exitoso` con `persistencia=escrita|borrada|error_escritura`.
6. Si es Padre, va a `seleccion_hijo.fxml`. Otros roles van al Dashboard.
7. Auditoría base: `op=LOGIN entity=USER` + MDC.

---

## 4. Navegación Dashboard → Main

`MainController.navegarAVista("main", ...)` (invocado al pulsar "Volver al Menú Principal" o al cerrar el Dashboard):

1. ¿Existe `~/.wiredacademy-session`?
   - **Sí** (rama E): sesión sobrevive → panel de sesión se muestra en Main → próximo portal entra directo.
   - **No** (rama D): ejecutar logout automático:
     - `logger.audit("[SESSION] logout_auto motivo=volver_a_main_sin_persistencia codigo=X")`
     - `SesionUsuario.cerrarSesion()`
     - `logger.setUsuario(null)` (limpia MDC)
     - Main se construye sin sesión → panel oculto → próximo portal pide login.

---

## 5. Logout Explícito

`MainController.handleCerrarSesion` (botón "Cerrar sesión"):

1. Captura el código actual.
2. `SesionUsuario.cerrarSesion()` (limpia RAM).
3. `PersistenciaSesion.borrar()` (limpia archivo).
4. Log `[SESSION] logout codigo=X persistencia_borrada=bool`.
5. `logger.setUsuario(null)` (limpia MDC).

---

## 6. Auto-login al Arrancar

`WiredAcademyApp.intentarAutoLogin` (solo si existe `~/.wiredacademy-session`):

1. `PersistenciaSesion.leer()` → `Optional<String>`.
2. Si vacío: `[SESSION] autologin_fallido motivo=sin_archivo`.
3. Si presente: `[SESSION] autologin_intento codigo=X`, buscar usuario.
4. Si usuario no existe: borrar archivo, `[SESSION] autologin_fallido motivo=usuario_inexistente`.
5. Si usuario existe: `SesionUsuario.iniciarSesion`, setear MDC, `[SESSION] autologin_exitoso`.

---

## 7. Eventos `[SESSION]` (ver `LOGGING.md` §8)

| Evento | Cuándo |
|---|---|
| `login_intento` | Al pulsar "Iniciar Sesión" |
| `login_exitoso` | Credenciales válidas |
| `login_fallido` | Credenciales inválidas o campos vacíos |
| `login_cancelado` | Botón "Regresar" |
| `autologin_intento` | Archivo de sesión presente al arrancar |
| `autologin_exitoso` | Archivo válido + usuario existe |
| `autologin_fallido` | Archivo ausente o usuario inexistente |
| `logout` | Botón "Cerrar sesión" |
| `logout_auto` | Volver a Main sin persistencia |
| `seed_mode_started` / `seed_mode_completed` | Arranque con `WIRED_SEED=true` |

---

## 8. Bootstrap vs Seeder — Contraseñas

> **Importante**: la contraseña del primer administrador se define en el **Bootstrap** (`configuracion_inicial.fxml`), NO es la universal. La universal `WiredAcademy2026!` aplica únicamente a los usuarios generados por el **Seeder** (`WIRED_SEED=true`): `DIR-01`, `DOC-1..10`, `EST-1..50`, etc.
>
> - **BD vacía + sin `WIRED_SEED`** → Bootstrap pide crear admin con contraseña propia.
> - **BD con admin + `WIRED_SEED=true`** → Seeder crea usuarios de prueba con `WiredAcademy2026!`.

---

**WiredAcademy — Ecosistema Educativo Digital**
