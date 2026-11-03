-- Relação opcional para preservar cursos anteriores, sem criar novas entidades.
BEGIN;
ALTER TABLE curso ADD COLUMN IF NOT EXISTS area_estudo_id bigint REFERENCES area_estudo(id);
COMMIT;
