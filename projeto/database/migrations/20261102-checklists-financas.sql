-- Executar uma vez antes de publicar o backend com checklists independentes.
-- Migração aditiva: preserva tarefas, associações, prazos e conclusões existentes.
BEGIN;
ALTER TABLE plano_intercambio ADD COLUMN IF NOT EXISTS valor_guardado numeric(14,2);
ALTER TABLE plano_intercambio ADD COLUMN IF NOT EXISTS aporte_mensal numeric(14,2);
ALTER TABLE tarefa_plano ALTER COLUMN modelo_origem_id DROP NOT NULL;
ALTER TABLE tarefa_plano ADD COLUMN IF NOT EXISTS categoria varchar(32);
ALTER TABLE tarefa_plano ADD COLUMN IF NOT EXISTS tema varchar(32);
ALTER TABLE tarefa_plano ADD COLUMN IF NOT EXISTS chave_sugestao varchar(80);
CREATE UNIQUE INDEX IF NOT EXISTS uk_tarefa_plano_sugestao ON tarefa_plano (plano_id, chave_sugestao);
COMMIT;
