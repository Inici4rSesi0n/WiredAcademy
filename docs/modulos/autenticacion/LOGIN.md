# Flujo: Login (Autenticación)

---

## 1. Visión General

Proceso estándar de inicio de sesión para todos los usuarios (excepto Padres, que tienen un paso extra).

---

## 2. Flujo de Trabajo

1. El usuario ingresa su código y contraseña en `login.fxml`.
2. El caso de uso `AutenticarUsuario` verifica las credenciales usando BCrypt.
3. Si es exitoso:
   - Si el rol es `PADRE`: Redirige a la vista de Selección de Hijo.
   - Si es cualquier otro rol: Redirige directamente al Dashboard principal.
4. Si falla: Muestra mensaje de error y limpia los campos.
