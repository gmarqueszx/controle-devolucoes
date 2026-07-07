CREATE TABLE equipes (
    id BIGSERIAL PRIMARY KEY,
    montador VARCHAR(100) NOT NULL,
    eletricista VARCHAR(100),
    ativa BOOLEAN DEFAULT true,
    criado_em TIMESTAMP DEFAULT now()
);
