-- Preserva cursos do catálogo e tarefas existentes. Aplicar antes do deploy.
BEGIN;
ALTER TABLE curso ALTER COLUMN instituicao_id DROP NOT NULL;
ALTER TABLE curso ALTER COLUMN tipo_intercambio DROP NOT NULL;
ALTER TABLE curso ADD COLUMN IF NOT EXISTS proprietario_id bigint REFERENCES users(id);
ALTER TABLE curso ADD COLUMN IF NOT EXISTS instituicao_informada varchar(100);
ALTER TABLE curso ADD COLUMN IF NOT EXISTS etiquetas varchar(120);
ALTER TABLE curso ADD COLUMN IF NOT EXISTS inicio_previsto date;
ALTER TABLE curso ADD COLUMN IF NOT EXISTS fim_previsto date;
ALTER TABLE curso ADD COLUMN IF NOT EXISTS link varchar(500);
ALTER TABLE tarefa_plano ADD COLUMN IF NOT EXISTS curso_apoio_id bigint REFERENCES curso(id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_tarefa_plano_curso_apoio ON tarefa_plano (plano_id, curso_apoio_id);
COMMIT;
