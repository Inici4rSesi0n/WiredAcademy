# Módulo: Dashboard (Pantalla Principal)

---

## 1. Visión General

Es la primera pantalla que ve el usuario tras iniciar sesión. Centraliza los indicadores clave (KPIs) y funciona como **consumidor de comunicados globales** de la institución.

---

## 2. Control de Acceso (RBAC)

- **Roles con visibilidad:** Todos (excepto el flujo de selección de hijo previo, que es exclusivo del Padre).

---

## 3. Requisitos Funcionales (RF)

- **RF-DB-01 (KPIs):** Mostrar tarjetas con estadísticas (Estudiantes, Docentes, Grupos, Asignaturas) para roles administrativos.
- **RF-DB-02 (Actividad Reciente / Comunicados):** Mostrar un *feed* o lista con los últimos **Comunicados Institucionales** y **Documentos Administrativos** publicados en el módulo de "Subir Material" con alcance Global.
- **RF-DB-03 (Navegación):** El logo "WiredAcademy" actúa como botón de retroceso a esta pantalla principal.
- **RF-DB-04 (Sidebar):** El menú lateral filtra sus opciones según el `GestionPermisos` del rol.

---

## 4. Flujo de Trabajo (Workflow)

1. El usuario inicia sesión y llega al Dashboard.
2. El sistema carga los KPIs y consulta los materiales globales más recientes.
3. El usuario puede hacer clic en un comunicado para leerlo completo o descargar el archivo adjunto.
