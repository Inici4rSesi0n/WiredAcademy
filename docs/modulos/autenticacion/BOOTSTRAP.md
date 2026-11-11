# Flujo: Bootstrap (Configuración Inicial)

---

## 1. Visión General

Es el primer flujo que se ejecuta en el sistema cuando la base de datos está vacía. Obliga a la creación del primer usuario con rol `ADMINISTRADOR` para poder ingresar al sistema.

---

## 2. Flujo de Trabajo

1. La aplicación arranca y detecta que no hay administradores en la BD (`repoAdmin.listarTodos().isEmpty()`).
2. Se lanza la vista `configuracion_inicial.fxml`.
3. El usuario ingresa los datos del primer administrador (código, nombre, apellido, DNI y contraseña).
4. Al confirmar, se encripta la contraseña y se guarda el usuario.
5. Se inicializan catálogos base (Turnos, Grados, Periodos).
6. El sistema avanza al Login normal.
