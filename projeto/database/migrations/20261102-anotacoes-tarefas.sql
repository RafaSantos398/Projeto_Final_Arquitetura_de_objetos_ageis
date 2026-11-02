-- Aplicar antes de publicar a versão com anotações por tarefa.
-- Somente adiciona campos opcionais, preservando o checklist existente.
BEGIN;
ALTER TABLE tarefa_plano ADD COLUMN IF NOT EXISTS anotacoes varchar(10000);
ALTER TABLE tarefa_plano ADD COLUMN IF NOT EXISTS anotacoes_atualizadas_em timestamp(6) with time zone;
COMMIT;
