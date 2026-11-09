# Architecture Decision Records (ADR)

> Registro de decisiones arquitectónicas de WiredAcademy.

**Versión:** 1.0  
**Estado:** Activo  
**Última revisión:** 2026-11-08  
**Mantenedor:** inici4rsesi0n

---

## Nota importante sobre ADRs históricos

Los ADRs **ADR-001 a ADR-005** están documentados históricamente dentro de **[LOGGING.md §11-12](../LOGGING.md#11-decisiones-arquitectónicas-aceptadas)** por razones de evolución del proyecto:

- **§11** — "Decisiones Arquitectónicas Aceptadas": ADR-001, ADR-002, ADR-005.
- **§12** — "Deuda Diferida a Post-BBDD": D2 (contraseña del keystore), D9 (persistencia de sesión).

**Los nuevos ADRs (ADR-006+)** se documentan aquí como archivos individuales.

**Motivo de la migración parcial:** los ADRs históricos ya están consolidados en `LOGGING.md` y son estables (ninguna decisión ha cambiado desde su registro). Extraerlos ahora sería churn sin valor. La convención a partir de ADR-006 es: **un archivo por ADR** en este directorio.

---

## ADRs en este directorio

| ADR | Título | Fecha | Estado |
|---|---|---|---|
| [ADR-007](ADR-007.md) | DNI agnóstico de país | 2026-11-08 | Aceptada |

---

## Formato de un ADR

Cada ADR sigue el formato estándar de Michael Nygard:

```markdown
# ADR-NNN — Título breve

**Fecha:** YYYY-MM-DD  
**Estado:** Propuesta | Aceptada | Rechazada | Obsoleta | Reemplazada por ADR-XXX

## Contexto

¿Cuál es la situación que motiva esta decisión? ¿Qué fuerzas están en juego
(técnicas, de negocio, de equipo, de plazos)?

## Decisión

¿Qué decidimos? Redacción afirmativa ("Haremos X", "Usaremos Y").

## Consecuencias

¿Qué se vuelve más fácil y qué se vuelve más difícil a partir de esta decisión?

- **Positivas:** beneficios esperados.
- **Negativas:** costes aceptados conscientemente.
- **Neutras:** trade-offs sin juicio de valor.

## Alternativas consideradas

¿Qué otras opciones evaluamos? ¿Por qué las rechazamos? Esto es crucial para
que un futuro lector entienda el contexto de la decisión y no la revierta
sin conocer los motivos originales.

## Reevaluar si

Bajo qué condiciones la decisión podría necesitar revisión. Umbrales concretos
("si el proyecto supera N usuarios", "si aparece el caso de uso X", etc.).

---

*WiredAcademy — Ecosistema Educativo Digital*
```

---

## Cómo añadir un ADR

1. **Verificar que no exista ya.** Buscar en este directorio y en `LOGGING.md` §11-12.
2. **Numeración secuencial:** el siguiente número es el actual + 1 (ADR-008 si el último es ADR-007).
3. **Crear** `ADR-NNN.md` con el formato de arriba.
4. **Actualizar esta tabla** con la nueva entrada.
5. **Enlazar desde `ARCHITECTURE.md` §5** si la decisión es relevante para la visión general.
6. **Enlazar desde `DATABASE.md`, `EVENTBUS.md`, `TESTING.md` o `LOGGING.md`** según el área afectada.

---

## Estados posibles

| Estado | Significado |
|---|---|
| **Propuesta** | En discusión, no vinculante aún |
| **Aceptada** | Vinculante, aplicada en código |
| **Rechazada** | Evaluada pero descartada (documentada para no repetir el análisis) |
| **Obsoleta** | Ya no aplica (contexto cambió) |
| **Reemplazada por ADR-XXX** | Sustituida por una decisión posterior |

> **Nunca borrar un ADR.** Si una decisión se revierte, se documenta el cambio en un nuevo ADR y el antiguo pasa a "Reemplazada por ADR-XXX". El histórico es el valor principal del registro.

---

## Ver también

- **`LOGGING.md`** — ADRs históricos §11-12 + convención de logging.
- **`ARCHITECTURE.md`** — Visión arquitectónica general.
- **`DATABASE.md`** — Esquema y migraciones.
- **`EVENTBUS.md`** — Contrato B1.

---

**WiredAcademy — Ecosistema Educativo Digital**
