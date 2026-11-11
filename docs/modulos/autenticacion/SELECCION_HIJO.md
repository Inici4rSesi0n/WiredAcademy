# Flujo: Selección de Hijo (Exclusivo Padres)

---

## 1. Visión General

Debido a que un Padre puede tener múltiples hijos matriculados, el sistema requiere que seleccione con qué cuenta de estudiante desea interactuar.

---

## 2. Flujo de Trabajo

1. El Padre se loguea exitosamente.
2. El sistema obtiene la lista de `Estudiante` vinculados (`Padre.getEstudiantes()`).
3. Se muestra la vista `seleccion_hijo.fxml` con una tarjeta o lista por cada hijo.
4. El Padre hace clic en un hijo.
5. El sistema almacena temporalmente la selección y redirige al Dashboard, asumiendo los permisos del estudiante elegido para la navegación.
