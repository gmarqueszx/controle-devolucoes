CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) DEFAULT 'VIEWER' CHECK (perfil IN ('ADMIN','VIEWER')),
    ativo BOOLEAN DEFAULT true,
    criado_em TIMESTAMP DEFAULT now()
);
