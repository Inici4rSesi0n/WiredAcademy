# Flujo: Bootstrap (Configuración Inicial)
## 1. Visión General
Primer flujo al arrancar con BD vacía. Configura el ecosistema base.
## 2. Flujo de Trabajo
1. Se detecta que no hay administradores.
2. Se lanza `configuracion_inicial.fxml`.
3. El usuario ingresa datos del Admin y selecciona el **TIPO_INSTITUCION** (Colegio, Instituto, Universidad).
4. El sistema guarda la configuración y siembra los catálogos correspondientes (Grados/Ciclos, Aulas, etc.).
5. Redirige al Login.
