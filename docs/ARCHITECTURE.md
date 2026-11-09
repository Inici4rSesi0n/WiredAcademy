# ARCHITECTURE.md — Arquitectura de WiredAcademy

> Documento de referencia arquitectónica. Define las capas, principios y patrones del proyecto.

**Versión:** 1.0  
**Estado:** Activo  
**Última revisión:** 2026-11-08  
**Mantenedor:** inici4rsesi0n

---

## 1. Principios rectores

El proyecto sigue **4 escuelas** combinadas. No son excluyentes — cada una aporta una capa de garantía:

| Escuela | Aplicación concreta |
|---|---|
| **Hexagonal (Ports & Adapters)** | Dominio puro, puertos explícitos, adaptadores aislados |
| **DDD (Domain-Driven Design)** | Bounded contexts, entidades ricas, value objects |
| **Clean Architecture** | Dependencias apuntan al centro (dominio) |
| **SOLID** | SRP por clase, OCP vía puertos, DIP en todas las capas |

**Regla de oro:** el dominio **no importa** Spring, JPA, JavaFX, SLF4J, ni BCrypt. Cero dependencias de framework en `dominio/`. Si un día cambias de Spring a Quarkus, de JPA a JDBC puro, de JavaFX a Swing, el dominio no se toca.

---

## 2. Capas

```
┌─────────────────────────────────────────────────────────────┐
│ PRESENTACIÓN (JavaFX)                                       │
│ Controllers, DTOs de presentación, diálogos, Page Objects   │
└─────────────────────────┬───────────────────────────────────┘
                          │ (depende de)
┌─────────────────────────▼───────────────────────────────────┐
│ APLICACIÓN (Casos de uso)                                   │
│ GestionUsuarios, GestionGrupos, GestionMatriculas, ...      │
│ DTOs de entrada (CrearUsuarioCommand), servicios            │
└─────────────────────────┬───────────────────────────────────┘
                          │ (depende de)
┌─────────────────────────▼───────────────────────────────────┐
│ DOMINIO (Puro)                                              │
│ Entidades: Usuario, Grupo, Clase, Matricula, ...            │
│ Puertos: Repositorio*, LoggerPort, HashProvider,            │
│          EventPublisher, EventBusListener                   │
└─────────────────────────▲───────────────────────────────────┘
                          │ (implementa)
┌─────────────────────────┴───────────────────────────────────┐
│ INFRAESTRUCTURA (Adaptadores)                               │
│ JPA + PostgreSQL, SLF4J, BCrypt, EventBus, SpringFxmlLoader │
└─────────────────────────────────────────────────────────────┘
```

**Dirección de dependencias:** Presentación → Aplicación → Dominio ← Infraestructura. El dominio es el centro, nada depende de él salvo los adaptadores que lo implementan.

---

## 3. Estructura de paquetes

```
src/main/java/
├── dominio/
│   ├── modelo/                          ← Entidades ricas + enums
│   │   ├── Usuario.java                 ← abstract, jerarquía de 7 roles
│   │   ├── Administrador.java           ← subclase
│   │   ├── Director.java                ← subclase
│   │   ├── Docente.java                 ← subclase con especialidad + asignaturas
│   │   ├── Estudiante.java
│   │   ├── Secretario.java
│   │   ├── CoordinadorAcademico.java    ← gradosSupervisados
│   │   ├── Padre.java                   ← estudiantes vinculados
│   │   ├── Asignatura.java
│   │   ├── Aula.java                    ← modalidad PRESENCIAL/VIRTUAL/REMOTO
│   │   ├── Clase.java                   ← extends Evento
│   │   ├── Evento.java                  ← abstract, FranjaHoraria
│   │   ├── FranjaHoraria.java           ← value object
│   │   ├── Grado.java
│   │   ├── Grupo.java                   ← tutor bidireccional
│   │   ├── Matricula.java               ← periodo + grupo + asignaturas
│   │   ├── PeriodoAcademico.java
│   │   ├── Recreo.java                  ← extends Evento, M:N con periodo
│   │   ├── Turno.java
│   │   ├── EstadoMatricula.java         ← enum ACTIVA/RETIRADA/CULMINADA
│   │   ├── ModalidadAula.java           ← enum
│   │   └── TipoEvento.java              ← enum de 17 eventos de dominio
│   └── puerto/
│       ├── externo/
│       │   ├── LoggerPort.java
│       │   ├── HashProvider.java
│       │   ├── EventPublisher.java
│       │   └── EventBusListener.java
│       └── repositorio/
│           ├── Repositorio.java         ← genérico T
│           └── Repositorio*             ← 17 repos especializados
├── aplicacion/
│   ├── casosdeuso/
│   │   └── portaldeingreso/
│   │       ├── administraciondelsistema/
│   │       │   ├── academico/           ← 8 use cases (Asignaturas, Aulas, Clases, Grados, Grupos, Periodos, Recreos, Turnos)
│   │       │   └── usuarios/            ← 8 use cases (Administradores, Coordinadores, Directores, Docentes, Estudiantes, Padres, Secretarios, Usuarios)
│   │       ├── gestiondetramites/       ← GestionMatriculas
│   │       ├── miareapersonal/          ← GestionHorario (solo lectura)
│   │       └── sistema/                 ← AutenticarUsuario, BootstrapUseCase, GestionPermisos
│   ├── dto/
│   │   └── CrearUsuarioCommand.java
│   ├── puerto/
│   │   └── ProveedorDatosIniciales.java
│   └── servicio/
│       └── BuscadorUsuario.java
├── infraestructura/
│   ├── configuracion/
│   │   └── SpringFxmlLoader.java
│   ├── eventos/
│   │   └── EventBus.java                ← Spring bean, WeakReference
│   ├── log/
│   │   └── Slf4jLoggerAdapter.java
│   ├── persistencia/
│   │   ├── adaptadores/                 ← 17 adaptadores JPA
│   │   ├── entidades/                   ← 16 entidades @Entity
│   │   ├── jpa/                         ← 15 JpaRepository interfaces
│   │   └── mappers/                     ← 16 mappers (MapStruct + manuales)
│   └── seguridad/
│       ├── BCryptHashProvider.java
│       └── UtilLimpieza.java
├── presentacion/
│   ├── animaciones/                     ← AnimacionesMain
│   ├── controlador/
│   │   ├── BootstrapController.java
│   │   ├── LoginController.java
│   │   ├── MainController.java
│   │   └── portaldeingreso/
│   │       ├── DashboardController.java
│   │       ├── administraciondelsistema/
│   │       │   ├── gestionacademica/    ← 10 controllers
│   │       │   └── gestiondeusuarios/   ← 1 controller
│   │       └── gestiondetramites/
│   │           └── matricula/           ← 1 controller
│   ├── dialogos/                        ← Dialogos (factory de modals)
│   ├── dto/                             ← AsignacionItem, CoordinacionItem, TutorItem
│   ├── estadousuario/                   ← SesionUsuario (singleton estático)
│   └── utilidades/                      ← ValidadorUI
└── boot/
    ├── BootstrapRunner.java
    └── WiredAcademyApp.java
```

---

## 4. Patrones aplicados

### 4.1. Repository pattern

**Puerto genérico:**

```java
public interface Repositorio<T> {
    void agregar(T entidad);
    List<T> listarTodos();
    void actualizar(T original, T actualizada);
    void eliminar(T entidad);
}
```

**17 repositorios especializados** (uno por entidad de dominio con búsquedas específicas):

- `RepositorioUsuarios`, `RepositorioGrupos`, `RepositorioClases`, etc.
- Añaden métodos como `buscarPorCodigo`, `buscarPorDni`, `buscarPorGrupoYGrado`.

**17 adaptadores JPA** (uno por repositorio):

- `AdaptadorRepositorio*` en `infraestructura/persistencia/adaptadores/`.
- Traducen Entity ↔ Domain con MapStruct o mappers manuales.
- Cada `actualizar`/`eliminar` tiene `@Transactional` propio.

**Ventajas:**

- El dominio no conoce JPA.
- Cambiar de PostgreSQL a otra BD requiere solo tocar adaptadores + mappers, no el dominio ni los use cases.

### 4.2. Event Bus

**Puertos:**

```java
public interface EventPublisher {
    void notificar(TipoEvento evento);
    void notificar(TipoEvento... eventos);
}

public interface EventBusListener {
    void onEvent(TipoEvento evento);
}
```

**Implementación:** `EventBus` (Spring `@Component`):

- `Map<TipoEvento, List<WeakReference<EventBusListener>>>`.
- `Platform.runLater(...)` para despachar en el FX thread.
- `WeakReference` evita memory leaks cuando controllers se GC'd.
- `removeIf(ref -> ref.get() == null)` limpia refs muertas.

**Contrato B1:** los use cases son la única fuente de eventos.

- **Enforcement:** 0 llamadas `eventBus.notificar(...)` en `src/main/java/presentacion/`.
- **Blindaje:** 51 contract tests en `integration/eventos/`.
- **Ver:** `EVENTBUS.md`.

### 4.3. Hexagonal adapters

- 17 adaptadores de persistencia, 1 adaptador de logging (`Slf4jLoggerAdapter`), 1 adaptador de hashing (`BCryptHashProvider`).
- Todos inyectables vía Spring, todos reemplazables sin tocar el dominio.

### 4.4. Use case pattern

Cada caso de uso:

1. Es un `@Service` de Spring.
2. Recibe dependencias por constructor (repos + logger + publisher).
3. Expone métodos públicos con nombres de negocio (`crearGrupo`, `matricularEstudiante`).
4. Emite eventos vía `EventPublisher` tras mutaciones exitosas.
5. Registra `logger.audit(...)` con formato estructurado (ver `LOGGING.md`).

**Ejemplo** (`GestionGrupos.crearGrupo`):

```java
@Service
public class GestionGrupos {
    private final RepositorioGrupos repo;
    private final RepositorioDocentes repoDocentes;
    private final LoggerPort logger;
    private final EventPublisher publisher;

    public void crearGrupo(String nombre, Grado grado) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (grado == null)
            throw new IllegalArgumentException("El grado no puede ser nulo.");
        if (repo.buscarPorNombre(nombre).isPresent())
            throw new IllegalArgumentException("Ya existe un grupo con ese nombre.");

        repo.agregar(new Grupo(nombre, grado));
        logger.audit(String.format("op=CREATE entity=GRUPO id=%s | grado=%s",
                nombre, grado.getNombre()));
        publisher.notificar(TipoEvento.GRUPOS);
    }
}
```

**Características:**

- Validaciones en el use case (no en el controller).
- Logger estructurado (`op=CREATE entity=GRUPO`).
- Notificación de evento al final (contrato B1).
- Sin estado mutable (todos los campos son `final`).

---

## 5. Decisiones arquitectónicas

**Histórico completo de ADRs:**

- ADR-001 a ADR-005 en `LOGGING.md` §11-12.
- ADR-006+ en `ADR/`.

**Decisiones clave tomadas durante el desarrollo:**

| Decisión | ADR | Estado |
|---|---|---|
| Recreos atados a múltiples periodos | ADR-001 | Aceptada |
| `equalsIgnoreCase` en entidades con nombre | ADR-002 | Aceptada |
| Migración de `.bin` a PostgreSQL + JPA | ADR-005 | Aceptada |
| DNI agnóstico de país | ADR-007 | Aceptada |
| Sesiones persistidas en BD | (pendiente) | Deuda |

---

## 6. Anti-patterns evitados

| Anti-pattern | Cómo se evita |
|---|---|
| **Anemic domain model** | Entidades con lógica (validaciones en constructores, `Grupo.asignarTutor` bidireccional, `Recreo.aplicarA`) |
| **Framework en dominio** | Ningún import de Spring/JPA/SLF4J en `dominio/` |
| **God class** | Cada use case tiene una responsabilidad (SRP). El más grande (`GestionUsuarios`) tiene 9 dependencias porque orquesta 7 roles distintos |
| **Controller publica eventos** | Prohibido desde Fase 4 (validado por bot + contract tests) |
| **String mágico** | `TipoEvento` enum, `EstadoMatricula` enum, `Usuario.Rol` enum, `ModalidadAula` enum |
| **Raw type** | Todos los repos son genéricos con tipo concreto |
| **Acoplamiento temporal** | Los use cases notifican; los controllers reciben vía `EventBus` (no hay `controller.refresh()` explícito entre sí) |
| **Fat constructor** | Inyección por constructor con `final`. Si un use case tiene 9 dependencias, es señal de refactor futuro |

---

## 7. Trade-offs conocidos

| Trade-off | Justificación |
|---|---|
| **Doble FK de tutoría** (`grupos.tutor_id` + `usuarios.grupo_tutoria_id`) | Permite consultas rápidas desde ambos lados. Sincronización manual en `asignarTutor`. Deuda: unificar en V3 |
| **EventBus con `Platform.runLater`** | Despacha en FX thread, evita race conditions. Contrato: los listeners no deben bloquear |
| **Singletons estáticos** (`SesionUsuario`) | Simple para app de escritorio de un solo usuario. Deuda: migrar a bean Spring + persistencia (D9) |
| **Fechas/horas como String** | Compatibilidad con `DatePicker` JavaFX y validación explícita. Deuda: migrar a `LocalDate`/`LocalTime` con `AttributeConverter` |
| **Un único `LoggerPort`** | Simple. Jerarquía por capa requeriría 4 beans distintos (rompe autowiring). Descartado tras análisis |

---

## 8. Ver también

- **`TESTING.md`** — Estrategia de tests (495 sin UI + 15 UI)
- **`EVENTBUS.md`** — Contrato B1 completo
- **`DATABASE.md`** — Esquema y migraciones
- **`LOGGING.md`** — Convención de logging + ADRs históricos
- **`ADR/`** — ADRs nuevos (006+)

---

**WiredAcademy — Ecosistema Educativo Digital**
