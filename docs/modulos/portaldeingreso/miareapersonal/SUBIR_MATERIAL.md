# Módulo: Subir Material (Gestor de Recursos)

---

## 1. Visión General

Es el panel de control (Backend) para la gestión de recursos didácticos, comunicados institucionales y documentos de evaluación. Aquí se crean, editan y eliminan los archivos, pero **no es el lugar principal donde se consumen**.

---

## 2. Control de Acceso (RBAC)

- **Roles con visibilidad:** `DOCENTE`, `DIRECTOR`, `SECRETARIO`, `COORDINADOR`, `ADMINISTRADOR`.
- **Enum asociado:** `SeccionMenu.SUBIR_MATERIAL`.

---

## 3. Clasificación y Destino del Material

El destino del material depende de su tipo:

- **Material de Curso** (Sílabo, Práctica, Lectura, Instrumento de Evaluación): Se renderiza dentro de la vista **MIS_CURSOS** de los estudiantes y docentes asignados a esa materia.
- **Material Global** (Comunicado Institucional, Documento Administrativo): Se renderiza en el feed de **Actividad Reciente** del **DASHBOARD** (visible para todos los usuarios logueados).

---

## 4. Requisitos Funcionales (RF)

- **RF-SM-01 (Subida):** Formulario con Título, Descripción, Tipo de Material, Archivo y vinculación (Asignatura o Global).
- **RF-SM-02 (Repositorio - Tabla):** Visualización tabular con `CONSTRAINED_RESIZE_POLICY` mostrando: Fecha, Título, Tipo, Asignatura (si aplica) y Autor.
- **RF-SM-03 (CRUD):** Permitir modificar metadatos, reemplazar archivos y eliminar publicaciones.
- **RF-SM-04 (Filtros):** Filtrar la tabla por Asignatura o por Tipo.

---

## 5. Flujo de Trabajo (Workflow)

1. El usuario (Docente/Admin/Secretario) ingresa a "Subir Material".
2. Visualiza la tabla de sus publicaciones.
3. Al crear un material, selecciona el Tipo. Si es "Sílabo", el sistema le pedirá obligatoriamente asociarlo a una Asignatura. Si es "Comunicado", no pedirá asignatura (será global).
