# Módulo: Reportes y Business Intelligence

---

## 1. Visión General

Proporciona a la dirección y administración información estratégica para la toma de decisiones, con un fuerte enfoque en la **salud económica y operativa** de la institución.

---

## 2. Control de Acceso (RBAC)

- **Roles con visibilidad:** `ADMINISTRADOR`, `DIRECTOR`, `COORDINADOR`.
- **Enum asociado:** `SeccionMenu.REPORTES`.

---

## 3. Requisitos Funcionales (RF)

- **RF-RP-01 (Reportes Económicos):**
  - *Recaudación por Periodo:* Ingresos totales por matrículas y pensiones.
  - *Morosidad:* Listado de estudiantes con deudas vencidas y monto total adeudado.
  - *Impacto de Becas:* Cuánto dinero deja de ingresar por descuentos otorgados.

- **RF-RP-02 (Reportes Académicos):**
  - *Tasa de Deserción:* Estudiantes que han retirado matrícula o curso.
  - *Rendimiento por Curso:* Promedios globales por asignatura.

- **RF-RP-03 (Reportes Operativos):**
  - *Ocupación de Aulas:* Porcentaje de uso de infraestructura por franja horaria.

- **RF-RP-04:** Exportación de cualquier reporte a PDF y Excel (CSV).

---

## 4. Entidades Involucradas

- Cruce de datos de `Pago`, `Matricula`, `Clase`, `Usuario`.
