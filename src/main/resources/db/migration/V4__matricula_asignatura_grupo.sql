-- V4__matricula_asignatura_grupo.sql
-- Propósito: Permitir que un estudiante esté en diferentes grupos por asignatura.
-- 1. Hacemos que matriculas.grupo_id sea nullable.
-- 2. Convertimos matricula_asignaturas en una entidad con ID propio y le añadimos grupo_id.

ALTER TABLE matriculas ALTER COLUMN grupo_id DROP NOT NULL;

ALTER TABLE matricula_asignaturas DROP CONSTRAINT matricula_asignaturas_pkey;
ALTER TABLE matricula_asignaturas ADD COLUMN id BIGSERIAL PRIMARY KEY;
ALTER TABLE matricula_asignaturas ADD COLUMN grupo_id BIGINT REFERENCES grupos(id);

-- Sincronizamos los registros históricos (colegios) para que apunten al grupo de su matrícula.
UPDATE matricula_asignaturas ma
SET grupo_id = m.grupo_id
FROM matriculas m
WHERE ma.matricula_id = m.id;

-- Ahora que están sincronizados, forzamos la restricción.
ALTER TABLE matricula_asignaturas ALTER COLUMN grupo_id SET NOT NULL;
