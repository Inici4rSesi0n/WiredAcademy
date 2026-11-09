# WiredAcademy

> **Ecosistema Educativo Digital** 

Aplicación de escritorio (JavaFX) para la gestión integral de una institución educativa: administración de usuarios, catálogos académicos, horarios, matrículas, y trazabilidad completa vía logging estructurado + EventBus.

Diseñada para **academias, colegios y universidades** — con especial atención a instituciones que no tienen presupuesto para costear infraestructura digital propietaria (zonas rurales, provincias, instituciones públicas con recursos limitados).

**Stack:** Java 21 · JavaFX 23 · Spring Boot 3.2 · PostgreSQL 16 · Maven · Flyway · MapStruct · JUnit 5 · TestFX · Podman.

[![CI](https://github.com/inici4rsesi0n/ProyectoJava/actions/workflows/ci.yml/badge.svg)](https://github.com/inici4rsesi0n/ProyectoJava/actions/workflows/ci.yml)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
![Java](https://img.shields.io/badge/Java-21-orange.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791.svg)

---

## 🌍 Misión

Cada año, miles de instituciones educativas en zonas con recursos limitados siguen operando con planillas de cálculo, cuadernos físicos o software propietario que no pueden costear. El resultado: procesos manuales, errores humanos, pérdida de datos históricos y dependencia de terceros que no siempre comprenden la realidad local.

**WiredAcademy nace para romper esa brecha.** Es un ecosistema educativo digital completo, libre y autohospedable, que cualquier institución puede adoptar sin pagar licencias, sin depender de internet permanente, y sin ceder la propiedad de sus datos.

**Principios de la misión:**

- 🆓 **Gratuito para siempre** — sin costes de licencia, sin suscripciones, sin planes premium.
- 🏠 **Autohospedable** — la institución controla sus datos en sus propios servidores.
- 🌐 **Adaptable** — desde una academia de 30 alumnos hasta una universidad con miles.
- 🧩 **Extensible** — software libre permite que cualquier institución o desarrollador lo adapte a su realidad local.
- 👥 **Comunitario** — las mejoras vuelven al proyecto, beneficiando a la próxima institución.

Si eres una institución sin recursos, este software es para ti. Si eres un desarrollador con tiempo y ganas, eres bienvenido a contribuir.

---

## 🎯 Casos de uso

| Tipo de institución | Escala | Adaptación típica |
|---|---|---|
| **Academia** | 30-300 alumnos | Módulos de matrícula + horarios + gestión de usuarios |
| **Colegio** | 300-2000 alumnos | Ecosistema completo: catálogos + matrícula + trazabilidad |
| **Universidad** | 2000+ alumnos | Módulos académicos extendidos + auditoría detallada |

El sistema está diseñado con **arquitectura hexagonal**, lo que permite adaptar o reemplazar módulos sin tocar el núcleo. Cada institución puede activar solo los módulos que necesita.

---

## 🚀 Quickstart

### Requisitos

- **Java 21+** (JDK, no JRE)
- **Maven 3.8+**
- **Podman** (o Docker)
- Linux (probado en Linux Mint XFCE)

### 1. Clonar y configurar

```bash
git clone https://github.com/inici4rsesi0n/ProyectoJava.git
cd ProyectoJava
cp .env.example .env
# Editar .env con tus credenciales
```

### 2. Levantar PostgreSQL

```bash
podman run -d --name wiredacademy-postgres \
  -e POSTGRES_USER=wiredacademy_app \
  -e POSTGRES_PASSWORD=<tu_password> \
  -e POSTGRES_DB=wiredacademy \
  -p 5433:5432 \
  -v wiredacademy-pgdata:/var/lib/postgresql/data \
  postgres:16
```

### 3. Compilar y ejecutar

```bash
mvn clean compile
mvn javafx:run
```

La app abrirá el diálogo de **Configuración Inicial** (Bootstrap). Crea el primer administrador.

### 4. Ejecutar tests

```bash
./scripts/test.sh fast     # ~2 min (unit + integration)
./scripts/test.sh ui       # ~5 min (TestFX)
./scripts/test.sh full     # ~10 min (todo)
```

**Bot post-ejecución** (análisis de logs):

```bash
./scripts/run-bot.sh fast
```

---

## 🏗️ Arquitectura

Arquitectura hexagonal con 4 capas:

```
Presentación (JavaFX) → Aplicación (Use cases) → Dominio (Puro) ← Infraestructura (Adapters)
```

**Principios aplicados:**

- **Hexagonal (Ports & Adapters)** — Dominio puro, puertos explícitos, adaptadores aislados.
- **DDD** — Bounded contexts, entidades ricas, value objects.
- **Clean Architecture** — Dependencias apuntan al centro.
- **SOLID** — SRP por clase, OCP vía puertos, DIP en todas las capas.

**Regla de oro:** el dominio **no importa** Spring, JPA, JavaFX, SLF4J, ni BCrypt. Cero dependencias de framework en `dominio/`.

**Contrato B1:** los use cases son la única fuente de eventos de dominio. Los controllers solo consumen.

**Ventaja para la misión:** esta arquitectura permite que una institución reemplace el adaptador de PostgreSQL por SQLite, o el de logging por otro, sin tocar el núcleo del negocio. Cada despliegue puede adaptarse a los recursos disponibles.

Ver **`docs/ARCHITECTURE.md`** para el detalle completo.

---

## 🧪 Testing

| Categoría | Archivos | Ejecuta |
|---|---|---|
| Unitarios de dominio | ~20 | Instancias puras o mocks |
| Unitarios de aplicación | ~22 | Mockito |
| Integración (IT) | 20 | BD real (`wiredacademy_test`) |
| Contract tests (EventBus) | 12 archivos / 51 tests | End-to-end |
| UI tests (TestFX) | 15 | TestFX + Monocle headless |

**Total:** ~495 tests sin UI + 15 UI = **~510 tests** en suite completa.

**CI/CD:**

- `push` a cualquier rama → `fast-tests` (sin UI)
- `pull_request` a `master`/`main` → `full-tests` (con UI)
- `push` a `master`/`main` → `full-tests` (con UI)

**Bot post-ejecución** (`scripts/run-bot.sh`) analiza logs tras los tests buscando regresiones, listeners GC'd y bugs históricos.

Ver **`docs/TESTING.md`** para el detalle completo.

---

## 📚 Documentación

| Documento | Propósito |
|---|---|
| `docs/ARCHITECTURE.md` | Arquitectura hexagonal, DDD, SOLID, patrones |
| `docs/TESTING.md` | Estrategia de testing, comandos, CI |
| `docs/DATABASE.md` | Esquema, migraciones Flyway, convenciones |
| `docs/EVENTBUS.md` | Contrato B1, flujo de eventos, contract tests |
| `docs/LOGGING.md` | Convención de logging + ADRs históricos |
| `docs/ADR/` | Architecture Decision Records nuevos (ADR-006+) |
| `CHANGELOG.md` | Historial de versiones |
| `CONTRIBUTING.md` | Cómo contribuir |
| `SECURITY.md` | Reportar vulnerabilidades |

---

## 🎯 Estado del proyecto

**Fase:** alpha (`v0.1.0`). No apto para producción todavía. API y esquema pueden cambiar sin aviso.

**Roadmap de estabilización:**

- `v0.1.0` — Consolidación interna (actual)
- `v0.2.0` — Beta pública con primeras instituciones piloto
- `v1.0.0` — Primera versión estable con API congelada

**Deuda técnica conocida:** documentada en `docs/DATABASE.md` §8 y `docs/ARCHITECTURE.md` §7.

---

## 📝 Licencia

Copyright (C) 2026 inici4rsesi0n

Este proyecto está licenciado bajo **GNU General Public License v3.0 o posterior** (GPL-3.0-or-later). Ver `LICENSE`.

La licencia no es casual: es la única coherente con la misión.

**GPLv3 protege las cuatro libertades esenciales del software:**

- ✅ **Libertad 0** — Usar el software para cualquier propósito (incluido el comercial).
- ✅ **Libertad 1** — Estudiar el código fuente y adaptarlo a tus necesidades.
- ✅ **Libertad 2** — Redistribuir copias a quien lo necesite.
- ✅ **Libertad 3** — Mejorar el software y publicar tus mejoras para el beneficio de la comunidad.

**Por qué GPLv3 y no MIT/Apache:**

Las licencias permisivas (MIT, Apache 2.0) permiten que un tercero tome el código, lo mejore, y lo cierre. Eso crearía una versión privada con features que la comunidad no puede usar, y las instituciones sin recursos volverían a quedar fuera.

GPLv3 exige que las mejoras vuelvan al proyecto. Si una institución o un desarrollador mejora WiredAcademy, el beneficio se comparte con la próxima institución. Esto es lo que hace sostenible un proyecto con misión social.

> **Software libre, no gratis.** La GPLv3 protege tu derecho a adaptar y compartir. Ver `LICENSE` para el texto completo.

---

## 🤝 Contribuir

Ver **`CONTRIBUTING.md`**.

**Formas de contribuir (no solo código):**

- 🐛 **Reportar bugs** — abre un issue describiendo el problema.
- 💡 **Proponer mejoras** — sugiere features o refactors arquitectónicos.
- 📖 **Mejorar documentación** — aclaraciones, traducciones, ejemplos.
- 🧪 **Escribir tests** — aumentar cobertura, especialmente casos borde.
- 🌐 **Traducir** — el proyecto puede adaptarse a otros idiomas (i18n pendiente).
- 🏫 **Probar en instituciones reales** — feedback de uso real es invaluable.

Para reportar vulnerabilidades de seguridad, ver **`SECURITY.md`**.

---

## 🙏 Agradecimientos

Este proyecto se apoya en estas excelentes herramientas de software libre:

- **JavaFX** — UI toolkit (GPL+CE)
- **Spring Boot** — Framework de aplicación (Apache 2.0)
- **PostgreSQL** — Base de datos (PostgreSQL License)
- **Flyway** — Migraciones (Apache 2.0)
- **MapStruct** — Mappers (Apache 2.0)
- **Hibernate** — ORM (LGPL 2.1)
- **JUnit 5** — Testing (EPL 2.0)
- **TestFX** — UI testing (EUPL)
- **Podman** — Contenedores (Apache 2.0)

A la comunidad de software libre, que hizo posible que este proyecto exista sin pedir permiso ni pagar licencias.

---

**WiredAcademy — Ecosistema Educativo Digital**
