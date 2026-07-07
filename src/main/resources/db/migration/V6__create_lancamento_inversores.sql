CREATE TABLE lancamento_inversores (
    id BIGSERIAL PRIMARY KEY,
    lancamento_id BIGINT NOT NULL REFERENCES lancamentos(id) ON DELETE CASCADE,
    kw DOUBLE PRECISION NOT NULL,
    quantidade INTEGER NOT NULL
);
