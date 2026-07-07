CREATE TABLE lancamentos (
    id BIGSERIAL PRIMARY KEY,
    equipe_id BIGINT NOT NULL REFERENCES equipes(id),
    data_lancamento DATE NOT NULL,
    cliente VARCHAR(200),
    sistemas INTEGER DEFAULT 1,
    observacoes TEXT,
    criado_em TIMESTAMP DEFAULT now(),
    criado_por VARCHAR(100)
);
