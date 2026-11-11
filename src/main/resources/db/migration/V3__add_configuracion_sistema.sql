-- V3__add_configuracion_sistema.sql
-- Propósito: Crear la tabla de configuración global y sembrar el valor inicial.
CREATE TABLE configuracion_sistema (
    id BIGSERIAL PRIMARY KEY,
    tipo_institucion VARCHAR(20) NOT NULL CHECK (tipo_institucion IN ('COLEGIO', 'INSTITUTO', 'UNIVERSIDAD'))
);
INSERT INTO configuracion_sistema (tipo_institucion) VALUES ('COLEGIO');
