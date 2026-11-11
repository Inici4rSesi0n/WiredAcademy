# Módulo: Configuración (Vistas Adaptativas)

---

## 1. Visión General

Este módulo es polimórfico. Su interfaz de usuario cambia dinámicamente dependiendo del rol del usuario que accede.

---

## 2. Control de Acceso y Vistas Adaptativas

- **Roles con visibilidad:** Todos los roles.
- **Lógica de UI (RBAC Adaptativo):**
  - **Ruta A (Sistema):** Si `rol == ADMINISTRADOR`, el controlador inyecta `configuracion_sistema.fxml`. (Parámetros globales, TIPO_INSTITUCION, respaldos).
  - **Ruta B (Perfil):** Si `rol != ADMINISTRADOR`, el controlador inyecta `perfil_usuario.fxml`. (Cambiar contraseña, datos de contacto, foto).
- **Restricción:** Un Administrador **no** ve la vista de Perfil desde este menú (puede gestionar su cuenta desde "Gestión de Usuarios"). Todos los demás roles operativos y académicos usan la vista de Perfil.

---

## 3. Requisitos Funcionales (RF)

- **RF-CF-01:** El sistema debe evaluar el `Usuario.Rol` antes de renderizar la vista.
- **RF-CF-02:** Las opciones de configuración del sistema (Tipo de Institución) solo son visibles y editables por el Administrador.
