# (Front-Office - Autogestión del Estudiante)

# Módulo: Pagos

---

## 1. Visión General

Gestiona la economía del estudiante: visualización de deudas pendientes (matrículas, pensiones, seguros), historial de pagos realizados y cálculo de deuda total.

---

## 2. Control de Acceso (RBAC)

- **Roles con visibilidad:** `ESTUDIANTE`, `SECRETARIO`, `ADMINISTRADOR`, `DIRECTOR`.
- **Acceso de Padre:** Indirecto (puede pagar las deudas de su hijo seleccionado).
- **Restricción:** Docentes y Coordinadores no acceden a la información financiera de los alumnos.
- **Enum asociado:** `SeccionMenu.PAGOS` (Nuevo en el menú).

---

## 3. Requisitos Funcionales (RF)

- **RF-PG-01 (Estudiante):** Visualizar deudas pendientes agrupadas por concepto (ej. "Cuota 5", "Matrícula") con fecha de vencimiento.
- **RF-PG-02:** Visualizar historial de pagos realizados con estado ("Pagado").
- **RF-PG-03 (Secretario/Admin):** Registrar pagos manuales (efectivo, transferencia) y emitir recibos.
- **RF-PG-04:** Calcular la "Deuda Total" sumando los conceptos pendientes.
- **RF-PG-05 (Pasarela de Pago):** (A futuro) Botón para pagar en línea mediante tarjeta.

---

## 4. Flujo de Trabajo (Workflow)

1. El estudiante ingresa a "Pagos".
2. Visualiza el desglose de deudas y su historial de pagos.
3. Si es el Secretario, puede buscar a un estudiante por código y registrar un pago.

---

## 5. Entidades Involucradas (A Desarrollar)

- `ConceptoPago` (ej. Pensión, Matrícula), `Deuda`, `Pago` (transacción), `EstadoPago`.
