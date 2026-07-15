CREATE TABLE equipe_membros (
    id BIGSERIAL PRIMARY KEY,
    equipe_id BIGINT NOT NULL REFERENCES equipes(id),
    nome VARCHAR(100) NOT NULL,
    funcao VARCHAR(20) NOT NULL
);

INSERT INTO equipe_membros (equipe_id, nome, funcao)
SELECT id, montador, 'MONTADOR' FROM equipes WHERE montador IS NOT NULL AND montador <> '';

INSERT INTO equipe_membros (equipe_id, nome, funcao)
SELECT id, eletricista, 'ELETRICISTA' FROM equipes WHERE eletricista IS NOT NULL AND eletricista <> '';

ALTER TABLE equipes DROP COLUMN montador, DROP COLUMN eletricista;
