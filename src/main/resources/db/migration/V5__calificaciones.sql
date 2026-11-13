-- V5__calificaciones.sql
CREATE TABLE evaluaciones (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    peso DOUBLE PRECISION NOT NULL,
    asignatura_id BIGINT NOT NULL REFERENCES asignaturas(id)
);

CREATE TABLE calificaciones (
    id BIGSERIAL PRIMARY KEY,
    evaluacion_id BIGINT NOT NULL REFERENCES evaluaciones(id) ON DELETE CASCADE,
    estudiante_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    nota DOUBLE PRECISION,
    CONSTRAINT uq_calificacion_evaluacion_estudiante UNIQUE (evaluacion_id, estudiante_id)
);
