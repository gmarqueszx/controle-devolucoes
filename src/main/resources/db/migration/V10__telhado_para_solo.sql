ALTER TABLE lancamentos ADD COLUMN solo BOOLEAN NOT NULL DEFAULT false;
UPDATE lancamentos SET solo = true WHERE upper(trim(telhado)) = 'SOLO';
ALTER TABLE lancamentos DROP COLUMN telhado;
