# CONTRIBUTING.md — Guía de contribución

> Cómo contribuir a WiredAcademy.

**Licencia del proyecto:** GPL-3.0-or-later (ver [`LICENSE`](LICENSE)).

Al enviar una contribución, aceptas que tu aporte se licencie bajo los mismos términos.

---

## 1. Antes de empezar

- Verifica que el issue que quieres resolver **no esté ya en el tracker**.
- Para cambios grandes (nuevas features, refactors arquitectónicos), **abre un issue de discusión primero**.
- Para bugs pequeños o typos, envía un PR directo.
- Para vulnerabilidades de seguridad, **NO abras un issue público**. Ver [`SECURITY.md`](SECURITY.md).

---

## 2. Setup del entorno

Ver [`README.md`](README.md) sección "Quickstart".

Resumen:

```bash
git clone https://github.com/Inici4rSesi0n/WiredAcademy.git
cd WiredAcademy
cp .env.example .env
# Configurar credenciales en .env
podman start wiredacademy-postgres
mvn clean compile
./scripts/test.sh fast     # verificar que todo funciona
```

---

## 3. Flujo de contribución

1. **Fork** del repositorio.
2. **Crear rama** desde `master`:

   ```bash
   git checkout -b feature/mi-feature
   # o fix/mi-bug
   # o refactor/mi-refactor
   # o docs/mi-documentacion
   ```

3. **Commits atómicos** (ver §5).
4. **Tests verdes** localmente:

   ```bash
   ./scripts/test.sh fast
   ./scripts/test.sh ui       # si tocaste controllers JavaFX
   ```

5. **Push** a tu fork.
6. **Pull Request** a `master` con descripción clara.

---

## 4. Estándar de código

### Java

- **Estilo:** compacto (sin líneas en blanco innecesarias). Ver archivos existentes como referencia.
- **JavaDoc:** mínimo obligatorio: `@author` en clases nuevas.
- **Imports:** evitar wildcards (`import dominio.modelo.*`) salvo en tests.
- **Nombres:** español consistente con el resto del proyecto (`GestionUsuarios`, no `UserManager`).
- **Validaciones:** en el constructor de entidades de dominio, en comandos (records), **nunca** en controllers.

### Tests obligatorios

| Cambio | Test requerido |
|---|---|
| Nueva entidad de dominio | `*Test.java` en `dominio/modelo/` |
| Nuevo use case | `*Test.java` en `aplicacion/casosdeuso/` + `*IT.java` en `integration/` |
| Use case que notifica evento | `*ContractIT.java` en `integration/eventos/` |
| Nuevo controller con vista | `*UITest.java` en `presentacion/` |
| Nueva migración Flyway | Test de integración que ejercita el cambio |

---

## 5. Commits

**Formato:** [Conventional Commits](https://www.conventionalcommits.org/).

```
<tipo>(<scope>): <descripción corta>

<cuerpo opcional explicando el por qué>

<footer opcional>
```

### Tipos válidos

| Tipo | Propósito |
|---|---|
| `feat` | Nueva funcionalidad |
| `fix` | Corrección de bug |
| `refactor` | Refactor sin cambio funcional |
| `test` | Tests añadidos/mejorados |
| `docs` | Documentación |
| `chore` | Tareas de mantenimiento |
| `build` | Cambios en build/deps |
| `ci` | Cambios en CI |

### Ejemplos

```
feat(aplicacion): use cases notifican eventos (Fase 3c: grupos, docentes)

fix(dni): validacion agnostica de pais + migracion V2 (Fase 9, Bug 2)

docs(readme): añadir seccion de licencia GPLv3

refactor(presentacion): eliminar eventBus.notificar de controllers de catalogos (Fase 4.1)
```

### Reglas

- **Un commit = un cambio lógico.** Si necesitas "y también", son 2 commits.
- **Verifica tests** antes del commit: `./scripts/test.sh fast` verde.
- **Firma tu commit** (opcional pero recomendado): `git commit -S`.

---

## 6. Pull Request

- **Título:** mismo formato que el commit (Conventional Commits).
- **Descripción:**

```markdown
## Qué cambia
Breve descripción.

## Por qué
Motivación (link a issue si existe).

## Cómo probarlo
Pasos para verificar el cambio.

## Checklist
- [ ] `./scripts/test.sh fast` verde
- [ ] `./scripts/test.sh ui` verde (si aplica)
- [ ] Tests nuevos añadidos
- [ ] Documentación actualizada (si aplica)
```

---

## 7. Reportar bugs

Abre un issue con:

- **Título:** resumen conciso.
- **Pasos para reproducir.**
- **Comportamiento esperado vs real.**
- **Sistema:** SO, versión de Java, versión de Maven, versión de PostgreSQL.
- **Logs relevantes:** `wiredacademy-error.log` (últimas 20 líneas).

> **Nunca incluyas credenciales, DNIs reales u otra PII.**

---

## 8. Reportar vulnerabilidades

**NO abras un issue público.** Ver [`SECURITY.md`](SECURITY.md).

---

## 9. Ver también

- **`README.md`** — Quickstart y visión general.
- **`SECURITY.md`** — Política de divulgación responsable.
- **`docs/ARCHITECTURE.md`** — Principios arquitectónicos.
- **`docs/TESTING.md`** — Estrategia de tests.
- **`docs/ADR/`** — Decisiones arquitectónicas.

---

**WiredAcademy — Ecosistema Educativo Digital**
