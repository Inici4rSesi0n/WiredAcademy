# Módulo: Mi Horario

---

## 1. Visión General

Proporciona una vista personalizada del horario de clases. Para los actores operativos (Estudiante y Docente), muestra sus compromisos personales. Para los roles administrativos, funciona como un monitor de ocupación de aulas.

---

## 2. Control de Acceso y Vistas Adaptativas (RBAC)

- **Roles con visibilidad:** `ESTUDIANTE`, `DOCENTE`, `ADMINISTRADOR`, `DIRECTOR`, `COORDINADOR`.
- **Acceso de Padre:** Indirecto (ve el horario del estudiante seleccionado).
- **Enum asociado:** `SeccionMenu.MI_HORARIO`.
- **Lógica de UI según Rol:**
  - **Estudiante (o Padre):** Se le muestra automáticamente la pestaña **"Por Grupo"**, precargando su grupo y periodo activo.
  - **Docente:** Se le muestra automáticamente la pestaña **"Por Docente"**, precargando su código y el periodo activo.
  - **Admin / Director / Coordinador:** Se les muestra la pestaña **"Por Aula"**, permitiéndoles seleccionar un aula para visualizar su tasa de ocupación.

---

## 3. Requisitos Funcionales (RF)

- **RF-MH-01:** El sistema debe evaluar el rol del usuario al inicializar la vista para configurar la pestaña por defecto.
- **RF-MH-02:** Utilizar el componente visual (`GridPane`) del Visor de Administración, respetando el ADN visual (colores por asignatura, franjas de recreo).
- **RF-MH-03:** Mostrar la línea de tiempo actual (indicador "Ahora") para ubicar al usuario en el tiempo.
- **RF-MH-04:** Permitir exportar el horario a PDF/PNG.

---

## 4. Flujo de Trabajo (Workflow)

1. El usuario ingresa a "Mi Horario".
2. El controlador detecta el `Usuario.Rol`.
3. Si es Estudiante/Docente: invoca `GestionHorario.obtenerHorarioCompleto()` u `obtenerHorarioDocente()`.
4. Si es Administrativo: habilita el ComboBox de Aulas y espera la selección del usuario.
5. Se renderiza el Grid con los datos correspondientes.

---

## 5. Entidades Involucradas

- `Clase`, `Evento`, `FranjaHoraria`, `PeriodoAcademico`, `Aula`, `Grupo`.
