# Flujo: Login
1. El usuario ingresa código y contraseña.
2. `AutenticarUsuario` valida con BCrypt.
3. Si es Padre, va a `seleccion_hijo.fxml`. Otros roles van al Dashboard.
4. Auditoría: Se registra `op=LOGIN entity=USER` y se setea el MDC.
