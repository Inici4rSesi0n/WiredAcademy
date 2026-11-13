-- V6__lms_structure.sql
-- 1. Mover la modalidad de Aulas a Grupos
ALTER TABLE aulas DROP COLUMN modalidad;
ALTER TABLE grupos ADD COLUMN modalidad VARCHAR(20) NOT NULL DEFAULT 'PRESENCIAL';

-- 2. Crear la tabla de Publicaciones (CMS)
CREATE TABLE publicaciones (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('MATERIAL', 'TAREA', 'EVALUACION', 'FORO', 'ANUNCIO')),
    numero_semana INT,
    fecha_limite TIMESTAMP,
    url_archivo VARCHAR(255),
    asignatura_id BIGINT REFERENCES asignaturas(id),
    autor_id BIGINT NOT NULL REFERENCES usuarios(id),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_publicaciones_asignatura ON publicaciones(asignatura_id);
