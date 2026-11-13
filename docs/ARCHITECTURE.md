# ARCHITECTURE.md — Arquitectura de WiredAcademy

> Documento de referencia arquitectónica. Define las capas, principios y patrones del proyecto.

**Versión:** 1.2  
**Estado:** Activo  
**Mantenedor:** inici4rsesi0n

---

## 1. Principios rectores

El proyecto sigue **4 escuelas** combinadas: Hexagonal, DDD, Clean Architecture y SOLID.

**Regla de oro:** el dominio **no importa** Spring, JPA, JavaFX, SLF4J, ni BCrypt. Cero dependencias de framework en `dominio/`.

---

## 2. Capas

```
┌─────────────────────────────────────────────────────────────┐
│ PRESENTACIÓN (JavaFX)                                       │
│ Controllers, DTOs de presentación, Vistas FXML, CSS         │
└─────────────────────────┬───────────────────────────────────┘
                          │ (depende de)
┌─────────────────────────▼───────────────────────────────────┐
│ APLICACIÓN (Casos de uso)                                   │
│ GestionUsuarios, GestionMatriculas, GestionCalificaciones...│
│ DTOs de entrada (CrearUsuarioCommand), servicios            │
└─────────────────────────┬───────────────────────────────────┘
                          │ (depende de)
┌─────────────────────────▼───────────────────────────────────┐
│ DOMINIO (Puro)                                              │
│ Entidades: Usuario (10 roles), Grupo, Clase, Tarea...       │
│ Puertos: Repositorio*, LoggerPort, HashProvider, EventBus...│
└─────────────────────────▲───────────────────────────────────┘
                          │ (implementa)
┌─────────────────────────┴───────────────────────────────────┐
│ INFRAESTRUCTURA (Adaptadores)                               │
│ JPA + PostgreSQL, SLF4J, BCrypt, EventBus, SpringFxmlLoader │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. Estructura de paquetes (Actualizada)

```
src/main/java/
├── dominio/
│   ├── modelo/
│   │   ├── Usuario.java                 ← abstract, 10 roles (Dir, Subdir, Coord, Sec, Tes, Psi, Doc, Est, Pad, Admin)
│   │   ├── Subdirector.java             ← nuevo rol
│   │   ├── Tesorero.java                ← nuevo rol
│   │   ├── Psicologo.java               ← nuevo rol
│   │   ├── ...
│   │   ├── configuracion/               ← ConfiguracionSistema, TipoInstitucion
│   │   ├── Aula.java                    ← Sin modalidad (solo espacio físico)
│   │   ├── Grupo.java                   ← Contiene la Modalidad (Presencial/Virtual/Remoto)
│   │   ├── Evaluacion.java              ← Pesos (PC1, Examen)
│   │   ├── Calificacion.java            ← Notas (0-20)
│   │   ├── Publicacion.java (futuro)    ← Contenido LMS, Tareas, Foros
│   │   └── TipoEvento.java              ← enum con ~22 eventos
│   └── puerto/
│       ├── externo/                     ← LoggerPort, HashProvider, EventBus
│       └── repositorio/                 ← +20 repositorios
├── aplicacion/
│   ├── casosdeuso/portaldeingreso/
│   │   ├── administraciondelsistema/    ← gestión de catálogos y usuarios
│   │   ├── miareapersonal/              ← GestionHorario, GestionCalificaciones
│   │   ├── publicaciones/               ← (futuro) Gestor de Contenido LMS
│   │   └── sistema/                     ← Bootstrap, Auth, Permisos
├── infraestructura/
│   ├── persistencia/
│   │   ├── adaptadores/
│   │   ├── entidades/
│   │   ├── jpa/
│   │   └── mappers/
│   └── seguridad/
├── presentacion/
│   ├── controlador/
│   │   ├── portaldeingreso/
│   │   │   ├── administraciondelsistema/
│   │   │   ├── miareapersonal/          ← MisCursos, CursoDetalle, Calificaciones
│   │   │   ├── publicaciones/           ← (futuro) SubirMaterialController
│   │   │   └── DashboardController.java
└── boot/
```

---

## 4. Patrones aplicados

### 4.1. Repository pattern

- Puerto genérico `Repositorio<T>`.
- Más de 20 repositorios especializados y sus adaptadores JPA.

### 4.2. Event Bus (Contrato B1)

- Los use cases son la única fuente de eventos.
- Notifican eventos como `USUARIOS`, `MATRICULAS`, `EVALUACIONES`, `CALIFICACIONES`.
- Los controllers solo se suscriben para actualizar la UI.

### 4.3. Patrón de Publicación Centralizada (Nuevo)

- El Docente crea contenido (Tareas, Evaluaciones, Foros) desde un **Gestor de Publicaciones (CMS)**.
- Ese contenido se inyecta dinámicamente en las pestañas del Aula Virtual del Estudiante (`MIS_CURSOS`).

---

## 5. Decisiones arquitectónicas

| Decisión | ADR | Estado |
|---|---|---|
| Recreos atados a múltiples periodos | ADR-001 | Aceptada |
| `equalsIgnoreCase` en entidades con nombre | ADR-002 | Aceptada |
| Migración de `.bin` a PostgreSQL + JPA | ADR-005 | Aceptada |
| DNI agnóstico de país | ADR-007 | Aceptada |
| **Modalidad movida de Aula a Grupo** | **ADR-008** | **Aceptada** |
| Sesiones persistidas en BD | (pendiente) | Deuda |

---

## 6. Anti-patterns evitados

- **Anemic domain model:** Entidades con lógica (validaciones en constructores).
- **Framework en dominio:** Cero imports de Spring/JPA/SLF4J en `dominio/`.
- **Controller publica eventos:** Prohibido (validado por contract tests).
- **String mágico:** Enums para roles, eventos, estados y modalidades.

---

## 7. Trade-offs conocidos

| Trade-off | Justificación |
|---|---|
| **Doble FK de tutoría** | Sincronización manual en `asignarTutor`. Deuda: unificar. |
| **Singletons estáticos** (`SesionUsuario`) | Simple para escritorio. Deuda: persistir en BD. |
| **Fechas/horas como String** | Compatibilidad con `DatePicker`. Deuda: migrar a `LocalDate`. |

---

## 8. Ver también

- **`TESTING.md`**
- **`EVENTBUS.md`**
- **`DATABASE.md`**
- **`LOGGING.md`**
- **`ADR/`**

---

**WiredAcademy — Ecosistema Educativo Digital**
