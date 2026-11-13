# Documentación Conceptual de Módulos (WiredAcademy)

## 1. Matriz de Control de Acceso (10 Roles)

| Módulo / Sección | Admin | Director | Subdirector | Coordinador | Secretario | Tesorero | Psicólogo | Docente | Estudiante | Padre* |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **AUTENTICACIÓN** | | | | | | | | | | |
| Login / Selección Hijo | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **PORTAL DE INGRESO (Back-Office)** | | | | | | | | | | |
| Mis Cursos (LMS) | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ | Indirecto |
| Mi Horario | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ✅ | ✅ | Indirecto |
| Gestor de Publicaciones | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ |
| Gestión Académica | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Gestión de Usuarios | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Matrícula (Back-Office) | ✅ | ❌ | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Reportes | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ |
| Configuración (Sistema) | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Configuración (Perfil) | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ |
| **PORTALES EXTERNOS (Front-Office)** | | | | | | | | | | |
| Portal Trámites | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ | Indirecto |
| Portal Pagos | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | Indirecto |
| Portal Matrícula | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | Indirecto |

*\* Padre: Una vez logueado, selecciona a un hijo y asume sus permisos.*
