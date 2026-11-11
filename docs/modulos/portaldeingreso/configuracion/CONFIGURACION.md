# Módulo: Configuración (Vistas Adaptativas)

---

## 1. Visión General

Este módulo es polimórfico. Su interfaz de usuario cambia dinámicamente dependiendo del rol del usuario que accede.

---

## 2. Control de Acceso y Vistas Adaptativas

- **Enum asociado:** `SeccionMenu.CONFIGURACION_SISTEMA`.
- **Roles con visibilidad:** Todos excepto `PADRE`.
- **Lógica de UI (RBAC Adaptativo):**
  - **Ruta A (Perfil):** Si `rol == ESTUDIANTE || rol == DOCENTE`, el controlador inyecta `perfil_usuario.fxml`. (Cambiar contraseña, datos de contacto).
  - **Ruta B (Sistema):** Si `rol == ADMINISTRADOR || rol == DIRECTOR || rol == COORDINADOR || rol == SECRETARIO`, el controlador inyecta `configuracion_sistema.fxml`. (Parámetros globales, respaldos).

---

## 3. Requisitos Funcionales (RF)

- **RF-CF-01:** El sistema debe evaluar el `Usuario.Rol` antes de renderizar la vista.
- **RF-CF-02:** Las opciones de configuración del sistema están protegidas a nivel de backend por seguridad.
