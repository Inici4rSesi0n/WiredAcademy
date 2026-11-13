# Módulo: Gestor de Publicaciones (CMS)

## 1. Visión General
Backend centralizado donde Docentes y Administrativos crean el contenido que luego se muestra en el Aula Virtual o en el Dashboard.

## 2. Control de Acceso (RBAC)
- `DOCENTE`, `DIRECTOR`, `SUBDIRECTOR`, `SECRETARIO`, `COORDINADOR`, `ADMINISTRADOR`.

## 3. Modelo y Reglas Compartidas
- **Entidad:** `Publicacion` (tipo: MATERIAL, TAREA, EVALUACION, FORO, ANUNCIO).
- **Publicación Masiva:** Permite seleccionar múltiples Secciones a la vez. El sistema clona la publicación para cada una.

## 4. Perspectiva DOCENTE (Creación)
- Formulario para Material (Semana, Asignatura, Secciones).
- Formulario para Tareas (Fecha límite, formatos permitidos, intentos).
- Formulario para Evaluaciones (Pesos, criterios de nota).
- Formulario para Foros y Anuncios.
- Repositorio en Tabla para editar/eliminar lo publicado.

## 5. Perspectiva ADMINISTRATIVA
- Crean **Comunicados Globales** (aparecen en el Dashboard de todos).
