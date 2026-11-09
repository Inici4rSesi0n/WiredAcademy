# SECURITY.md — Política de seguridad

> Cómo reportar vulnerabilidades y qué versiones reciben soporte.

---

## 1. Versiones soportadas

| Versión | Soporte |
|---|---|
| `master` (development) | ✅ Activo |
| Releases `0.x` | ⚠️ Sin garantía — fase alpha |

---

## 2. Reportar una vulnerabilidad

**NO abras un issue público** para reportar una vulnerabilidad. Esto expone a los usuarios antes de que haya fix.

### Cómo reportar

Envía un email a **`inici4rsesi0n@gmail.com`** con:

- **Descripción** de la vulnerabilidad.
- **Pasos para reproducir.**
- **Impacto potencial** (¿qué puede hacer un atacante?).
- **Versión afectada.**
- **Mitigación sugerida** (si la tienes).

### Qué esperar

| Etapa | Plazo |
|---|---|
| Acuse de recibo | ~72 h |
| Evaluación inicial | ~7 días |
| Fix y disclosure coordinado | Depende de la severidad |

Si la vulnerabilidad es **crítica**, se priorizará el fix y se publicará un **security advisory** en GitHub.

---

## 3. Scope

### En scope

- **SQL injection** (aunque JPA protege contra la mayoría).
- **Exposición de credenciales en logs** (`password`, `hash`, `pwd`).
- **Exposición de PII en logs** (DNIs reales, nombres, etc.).
- **Ejecución remota de código** (RCE).
- **Path traversal / LFI.**
- **Vulnerabilidades en dependencias directas** (Spring Boot, PostgreSQL driver, JavaFX, JBCrypt).

### Fuera de scope

- Vulnerabilidades en **dependencias transitivas** sin exploit conocido.
- Issues de **"hardening"** sin exploit concreto.
- **Ingeniería social.**
- **Ataques físicos.**
- **Configuración insegura del entorno del usuario** (ej. `WIREDACADEMY_DB_PASSWORD=admin`).
- Issues en **versiones no soportadas**.

---

## 4. Buenas prácticas de seguridad del proyecto

El proyecto aplica:

- **BCrypt con factor 12** para hashing de contraseñas.
- **Limpieza de `char[]`** tras verificar contraseñas (`UtilLimpieza.limpiarContrasena`).
- **PostgreSQL `scram-sha-256`** en todas las conexiones.
- **Prepared statements** vía Spring Data JPA (protección contra SQL injection).
- **No loggeo de PII** (regla de [`docs/LOGGING.md`](docs/LOGGING.md)).
- **Bot post-ejecución** detecta exposición de credenciales en logs (`scripts/analyze-logs.sh`, sección 4).

---

## 5. Auditorías de seguridad

El proyecto **no tiene auditorías externas**. Ver [`README.md`](README.md) para el estado actual.

---

## 6. Ver también

- **`README.md`** — Visión general y estado del proyecto.
- **`CONTRIBUTING.md`** — Guía de contribución.
- **`docs/LOGGING.md`** — Convención de logging (regla de no-PII).
- **`docs/ARCHITECTURE.md`** — Principios arquitectónicos.

---

**WiredAcademy — Ecosistema Educativo Digital**
