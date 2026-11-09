# LOGGING.md — Convención de Logging de WiredAcademy

> Documento maestro de la estrategia de logging del proyecto.
> Define qué se registra, cómo se registra y por qué.

**Versión:** 1.1
**Estado:** Activo
**Última revisión:** 2026-10-05
**Mantenedor:** inici4rsesi0n

---

## 1. Principio Rector

**Logging dirigido, no masivo.** Se registran operaciones críticas de negocio; el resto se añade reactivamente cuando surge un bug y se retira al corregirlo.

| Modo | Zona | Estrategia |
|---|---|---|
| **Proactivo** | Usuarios y gestión académica | Logging permanente de auditoría |
| **Reactivo** | Resto del sistema | Logs temporales para diagnóstico, se eliminan al corregir |

> Un log bien puesto vale más que cien logs ruidosos.

---

## 2. Jerarquía de Loggers

```
WiredAcademy                    ← raíz
├── WiredAcademy.AUDIT          ← auditoría de negocio
├── WiredAcademy.EVENTBUS       ← flujo de eventos
└── WiredAcademy.UI             ← navegación (solo dev)
```

Cada categoría hereda del padre. La separación permite filtrar por archivo.

---

## 3. Niveles por Categoría

| Categoría | Nivel dev | Nivel prod | Uso |
|---|---|---|---|
| `AUDIT` | INFO | INFO | Eventos de negocio |
| `EVENTBUS` | DEBUG | WARN | Emisión y recepción de eventos |
| `UI` | TRACE | OFF | Navegación y refrescos |
| Raíz | INFO | WARN | Sistema general |

---

## 4. Formato Estándar

```
[<CATEGORÍA>] op=<OPERACION> entity=<ENTIDAD> id=<ID> | <detalles>
```

### Operaciones válidas

| Operación | Uso |
|---|---|
| `CREATE` | Creación de entidad |
| `UPDATE` | Modificación de entidad |
| `DELETE` | Eliminación de entidad |
| `LINK` | Vinculación entre entidades |
| `UNLINK` | Desvinculación entre entidades |
| `LOGIN` | Inicio de sesión exitoso |
| `FAILED` | Operación fallida (nivel WARN) |
| `PERSIST` | Error de persistencia (nivel ERROR) |

### Ejemplos

```
[AUDIT] op=CREATE entity=DOCENTE id=U34421561 | especialidad=Mat
[AUDIT] op=UPDATE entity=DOCENTE id=U34421561 | before=[Mat,Fis] after=[Mat]
[AUDIT] op=DELETE entity=GRUPO id=1A | estudiantes=15 tutor=U34421561
[AUDIT] op=LINK entity=DOCENTE id=U34421561 -> GRUPO id=1A | role=TUTOR
[AUDIT] op=LOGIN entity=USER id=ADMIN001 | role=ADMINISTRADOR
[EVENTBUS] event=USUARIOS listeners=3
[UI] action=NAVIGATE from=catalogos to=asignaciones
```

---

## 5. Alcance Proactivo

Las siguientes operaciones **siempre** emiten log `AUDIT`. Todas residen en la capa de aplicación.

### Usuarios
- `GestionUsuarios.agregarUsuario` → `CREATE`
- `GestionUsuarios.actualizarUsuario` → `UPDATE`
- `GestionUsuarios.eliminarUsuario` → `DELETE`

### Docentes
- `GestionDocentes.agregar` → `CREATE`
- `GestionDocentes.actualizar` → `UPDATE`
- `GestionDocentes.eliminar` → `DELETE`
- `GestionDocentes.agregarAsignatura` → `LINK`
- `GestionDocentes.removerAsignatura` → `UNLINK`
- `GestionDocentes.asignarTutoria` → `LINK`

### Grupos
- `GestionGrupos.crearGrupo` → `CREATE`
- `GestionGrupos.actualizarGrupo` → `UPDATE`
- `GestionGrupos.eliminarGrupo` → `DELETE`
- `GestionGrupos.asignarTutor` → `LINK`
- `GestionGrupos.removerTutor` → `UNLINK`

### Coordinadores
- `GestionCoordinadores.agregar` → `CREATE`
- `GestionCoordinadores.actualizar` → `UPDATE`
- `GestionCoordinadores.eliminar` → `DELETE`

### Padres
- `GestionPadres.agregar` → `CREATE`
- `GestionPadres.actualizar` → `UPDATE`
- `GestionPadres.eliminar` → `DELETE`
- `GestionPadres.vincularEstudiante` → `LINK`
- `GestionPadres.desvincularEstudiante` → `UNLINK`

### Estudiantes
- `GestionEstudiantes.agregar` → `CREATE`
- `GestionEstudiantes.actualizar` → `UPDATE`
- `GestionEstudiantes.eliminar` → `DELETE`

### Catálogos académicos
- `GestionAsignaturas.*` → `CREATE` / `UPDATE` / `DELETE`
- `GestionAulas.*` → `CREATE` / `UPDATE` / `DELETE`
- `GestionGrados.*` → `CREATE` / `UPDATE` / `DELETE`
- `GestionPeriodos.*` → `CREATE` / `UPDATE` / `DELETE`
- `GestionTurnos.*` → `CREATE` / `UPDATE` / `DELETE`
- `GestionRecreos.*` → `CREATE` / `UPDATE` / `DELETE` / `LINK` / `UNLINK`

### Clases y Matrículas
- `GestionClases.crearClase` → `CREATE`
- `GestionClases.actualizarClase` → `UPDATE`
- `GestionClases.eliminarClase` → `DELETE`
- `GestionMatriculas.matricularEstudiante` → `CREATE`
- `GestionMatriculas.actualizar` → `UPDATE`
- `GestionMatriculas.actualizarEstado` → `UPDATE`
- `GestionMatriculas.eliminarMatricula` → `DELETE`

### Administrativos
- `GestionAdministradores.*` → `CREATE` / `UPDATE` / `DELETE`
- `GestionDirectores.*` → `CREATE` / `UPDATE` / `DELETE`
- `GestionSecretarios.*` → `CREATE` / `UPDATE` / `DELETE`

### Autenticación
- `FxLoginController.handleLogin` (éxito) → `LOGIN`

---

## 6. Alcance Reactivo

Las siguientes zonas **no** tienen logging por defecto. Se instrumentan temporalmente ante un bug específico y se limpian al resolver.

- `AutenticarUsuario` (ya tiene WARN por fallo)
- `BootstrapUseCase`
- `GestionHorario` (solo lectura)
- `GestionPermisos`
- Controladores JavaFX (excepto `FxLoginController`)

**Única excepción permanente:** `SistemaEventBus` emite `EVENTBUS` en cada notificación para trazabilidad.

---

## 7. Uso en Código

### Casos de uso

```java
public void actualizarUsuario(Usuario usuario) {
    if (usuario == null) throw new IllegalArgumentException("El usuario no puede ser nulo");
    String antes = usuario.toString();

    switch (usuario.getRol()) {
        case DOCENTE -> repoDoc.actualizar((Docente) usuario, (Docente) usuario);
        // ...
    }

    logger.audit(String.format(
        "op=UPDATE entity=%s id=%s | before=%s after=%s",
        usuario.getRol(), usuario.getCodigo(), antes, usuario));
}
```

### EventBus

```java
public static void notificar(TipoEvento evento) {
    List<Runnable> copia = new ArrayList<>(suscriptores.getOrDefault(evento, List.of()));
    if (copia.isEmpty()) return;
    logger.eventBus(String.format("event=%s listeners=%d", evento, copia.size()));
    Platform.runLater(() -> { /* ... */ });
}
```

### Controladores

No emiten `AUDIT`. Solo notifican al `SistemaEventBus`, que registra automáticamente el flujo.

---

## 8. Reglas de Oro

1. **No loggear en el dominio** — el dominio es puro.
2. **Loggear en la capa de aplicación** — casos de uso.
3. **No loggear lecturas** — solo mutaciones.
4. **No loggear UI** salvo navegación en `TRACE`.
5. **Nunca loggear PII** — ni contraseñas, ni hashes, ni datos sensibles.
6. **Eliminar logs reactivos** una vez corregido el bug.
7. **Un log por operación**, no varios.

---

## 9. Archivos de Salida

**Dev:** consola con todo visible.
**Prod:** archivos rotados diariamente.

| Archivo | Contenido | Retención |
|---|---|---|
| `wiredacademy.log` | Todo (raíz) | 30 días |
| `wiredacademy-audit.log` | Solo `AUDIT` | 90 días |
| `wiredacademy-error.log` | Solo `ERROR` | 30 días |
| `wiredacademy-eventbus.log` | Solo `EVENTBUS` | 15 días |

### Consultas útiles

```bash
# Ver todas las mutaciones de un usuario
grep "U34421561" logs/wiredacademy-audit.log

# Ver solo eliminaciones
grep "op=DELETE" logs/wiredacademy-audit.log

# Ver flujo de eventos en vivo
tail -f logs/wiredacademy-eventbus.log

# Ver errores recientes
tail -50 logs/wiredacademy-error.log
```

---

## 10. Anti-patrones

```java
// ❌ Ruido
logger.info("Entrando a método X");

// ❌ Concatenación ineficiente
logger.info("El usuario es " + usuario);

// ❌ Fuga de PII
logger.info("Password: " + password);

// ❌ TRACE disfrazado de DEBUG
logger.debug("Iteración " + i);
```

```java
// ✅ Estructurado y filtrable
logger.audit(String.format(
    "op=UPDATE entity=USER id=%s | before=%s after=%s",
    usuario.getCodigo(), antes, usuario));
```

---

## 11. Decisiones Arquitectónicas Aceptadas

Registro de decisiones de diseño conscientes que afectan al logging o al almacenamiento.

### ADR-001 — Recreos atados a PeriodoAcademico

**Decisión:** un `Recreo` puede asociarse a **múltiples** `PeriodoAcademico`.
**Razón:** un mismo recreo (misma franja + descripción) se repite entre semestres. Evitar duplicación.
**Alternativa rechazada:** recreos globales sin periodo (pierde flexibilidad).
**Reevaluar si:** el 100% de las instituciones usa el mismo horario siempre.

### ADR-002 — `equals` case-insensitive en entidades con nombre

**Decisión:** `Grupo`, `Aula`, `Asignatura`, `Grado`, `PeriodoAcademico`, `Turno` usan `equalsIgnoreCase` sobre el nombre.
**Razón:** evitar duplicados por diferencias de mayúsculas/minúsculas.
**Excepción:** `Clase` y `Recreo` comparan por composición completa de sus partes.

---

## 12. Deuda Diferida a Post-BBDD

Cambios identificados que **no se aplican ahora** porque la migración a base de datos los hará obsoletos o redundantes.

### D2 — Contraseña del keystore

**Estado actual (mitigado):**
- Se lee de la variable de entorno `WIRED_KEYSTORE_PASSWORD`.
- Fallback: `"S1st3maMatr1culas2026"` (solo desarrollo y compatibilidad).

**Solución definitiva (diferida):**
- Unificar la contraseña maestra (ingresada en el primer arranque) con la del keystore.
- Requiere diálogo de arranque que solicite la contraseña maestra cada vez.
- Requiere migración de keystores existentes.

**Razón de la dilación:** la BBDD traerá autenticación nativa y hará obsoleto el keystore.

### D9 — Persistencia real de la sesión

**Estado actual:** `SesionUsuario` mantiene el estado en memoria.
**Comportamiento:** marcar "Mantener sesión iniciada" no persiste entre reinicios.
**Solución definitiva:** persistir el token de sesión en disco con cifrado + expiración.
**Razón de la dilación:** la BBDD traerá tokens/sesiones nativos.

---

## 13. Evolución del Documento

Este documento es **vivo**. Se actualiza cuando:
- Se añade una nueva zona crítica con logging proactivo.
- Se descubre un patrón útil de consulta de logs.
- Se retira o añade una categoría de logger.
- Se registra una decisión arquitectónica o deuda diferida.


## 14. Criterio de Creación en Casos de Uso

El sistema aplica **dos estilos** para la creación de entidades, según el contexto. **No es inconsistencia — es polimorfismo contextual.**

### `agregar(Entidad)` — Para jerarquías de tipos

**Cuándo usar:**
- La entidad tiene jerarquía (`Usuario` → 7 subclases).
- El controlador conoce el tipo concreto a instanciar.
- El caso de uso solo persiste, sin lógica de construcción.

**Aplicado en:**
- `GestionAdministradores.agregar(Administrador)`
- `GestionDirectores.agregar(Director)`
- `GestionCoordinadores.agregar(CoordinadorAcademico)`
- `GestionSecretarios.agregar(Secretario)`
- `GestionDocentes.agregar(Docente)`
- `GestionEstudiantes.agregar(Estudiante)`
- `GestionPadres.agregar(Padre)`

**Excepción:** `GestionUsuarios.agregarUsuario(CrearUsuarioCommand)` — usa el DTO para manejar los 7 roles con una firma unificada.

### `crearX(parámetros)` — Para entidades uniformes

**Cuándo usar:**
- La entidad tiene un constructor uniforme (sin jerarquía).
- El caso de uso aplica validaciones + construye la entidad.
- El controlador no debe conocer detalles de construcción.

**Aplicado en:**
- `GestionAsignaturas.crearAsignatura(nombre)`
- `GestionAulas.crearAula(nombre, capacidad, ...)`
- `GestionGrados.crearGrado(nombre, nivel)`
- `GestionGrupos.crearGrupo(nombre, grado)`
- `GestionPeriodos.crearPeriodo(nombre, fechas, estado)`
- `GestionTurnos.crearTurno(nombre)`
- `GestionRecreos.crearRecreo(franja, desc, periodos)`
- `GestionClases.crearClase(dia, horaInicio, ...)`

### Regla de decisión

| Pregunta | Si SÍ | Si NO |
|---|---|---|
| ¿La entidad tiene jerarquía de tipos? | `agregar(Entidad)` | `crearX(parámetros)` |
| ¿El controlador conoce el tipo concreto? | `agregar(Entidad)` | `crearX(parámetros)` |
| ¿Hay lógica de validación previa a construir? | `crearX(parámetros)` | `agregar(Entidad)` |

**Nuevos casos de uso:** evaluar según la regla. **No forzar unificación** — cada estilo tiene su razón.

---

*WiredAcademy — Ecosistema Educativo Digital*

### ADR-005 — Migración de persistencia de .bin a PostgreSQL (JPA)

**Decisión:** Reemplazar el sistema de archivos `.bin` cifrados con AES por una base de datos relacional PostgreSQL, usando JPA/Hibernate y MapStruct.

**Detalles de implementación:**
- **Infraestructura:** PostgreSQL 16 corriendo en Podman (puerto 5433), volumen persistente `wiredacademy-pgdata`.
- **Migraciones:** Flyway (`V1__init_schema.sql`) es la única fuente de verdad del esquema. Hibernate está en modo `validate`.
- **Arquitectura:** Se mantiene el Hexágono puro (Opción A2). Las entidades `@Entity` y mappers viven en `infraestructura.persistencia`.
- **Mapeo:** Uso de MapStruct para conversiones Dominio ↔ Entidad. Las relaciones M:N y M:1 se resuelven en los adaptadores para evitar `TransientObjectException`.
- **Seguridad:** Se elimina el `keystore.jceks` y el `ManejadorPersistencia`. Las credenciales se manejan mediante variables de entorno (`.env`) con autenticación `scram-sha-256`.

**Razón:** SQLite (o archivos locales) no soporta concurrencia real. PostgreSQL garantiza ACID, integridad referencial y prepara el terreno para RLS (Row-Level Security) en la LAN.
