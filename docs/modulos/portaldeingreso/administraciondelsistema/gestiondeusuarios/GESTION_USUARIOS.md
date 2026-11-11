# Módulo: Gestión de Usuarios

---

## 1. Visión General

Administra el ciclo de vida de todos los actores. Utiliza herencia (SINGLE_TABLE) para 7 roles.

---

## 2. Control de Acceso (RBAC)

- **Roles con visibilidad:** `ADMINISTRADOR`, `DIRECTOR`.
- **Enum asociado:** `SeccionMenu.GESTION_USUARIOS`.

---

## 3. Requisitos Funcionales (RF)

- **RF-GU-01:** CRUD de usuarios (7 roles).
- **RF-GU-02:** Validación de DNI internacional (1-20 caracteres alfanuméricos).
- **RF-GU-03:** Encriptación BCrypt (factor 12).
- **RF-GU-04:** Bloqueo de edición del código de usuario una vez creado.

---

## 4. Entidades y Eventos

- **Entidades:** `Usuario`, `Administrador`, `Director`, `CoordinadorAcademico`, `Docente`, `Estudiante`, `Padre`, `Secretario`.
- **Eventos:** `USUARIOS` y evento específico del rol (`DOCENTES`, `ESTUDIANTES`, etc.).
