ALTER TABLE lancamentos ADD COLUMN cidade_sobra VARCHAR(100);

CREATE INDEX idx_lancamentos_cidade_sobra ON lancamentos (cidade_sobra);
