CREATE TABLE asistencias (
    id BIGSERIAL PRIMARY KEY,
    grupo_id BIGINT NOT NULL REFERENCES grupos(id) ON DELETE CASCADE,
    asignatura_id BIGINT NOT NULL REFERENCES asignaturas(id) ON DELETE CASCADE,
    estudiante_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    fecha DATE NOT NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('PRESENTE', 'AUSENTE', 'TARDE', 'JUSTIFICADO')),
    CONSTRAINT uq_asistencia_grupo_asignatura_estudiante_fecha UNIQUE (grupo_id, asignatura_id, estudiante_id, fecha)
);
