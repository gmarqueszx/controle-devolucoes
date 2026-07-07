CREATE TABLE alertas (
    id BIGSERIAL PRIMARY KEY,
    equipe_id BIGINT NOT NULL REFERENCES equipes(id),
    lancamento_id BIGINT REFERENCES lancamentos(id),
    data_alerta DATE NOT NULL,
    descricao TEXT,
    nivel VARCHAR(10) CHECK (nivel IN ('ALTO','MEDIO','LEVE')),
    status_original VARCHAR(50),
    status VARCHAR(20) DEFAULT 'ABERTO' CHECK (status IN ('ABERTO','RESOLVIDO','JUSTIFICADO')),
    criado_em TIMESTAMP DEFAULT now()
);
