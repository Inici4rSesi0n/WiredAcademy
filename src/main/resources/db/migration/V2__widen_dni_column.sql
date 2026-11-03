-- ==============================================================================
-- V2__widen_dni_column.sql
-- Proposito: ampliar la columna usuarios.dni de VARCHAR(8) a VARCHAR(20)
-- para soportar documentos de identidad internacionales (Chile 9 con K,
-- Espana 9 con letra, Mexico 18 alfanumerico, etc.).
--
-- Cierra el bug historico "value too long for type character varying(8)"
-- y prepara el modelo para ADR-007 (DNI agnostico de pais).
-- ==============================================================================
ALTER TABLE usuarios ALTER COLUMN dni TYPE VARCHAR(20);
