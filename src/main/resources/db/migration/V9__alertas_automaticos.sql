ALTER TABLE lancamentos ADD COLUMN localizacao_sobra VARCHAR(200);

ALTER TABLE alertas
    ADD COLUMN origem VARCHAR(30),
    ADD COLUMN confirmado_desvio_em TIMESTAMP,
    ADD COLUMN confirmado_desvio_por VARCHAR(100),
    ADD COLUMN justificativa_confirmacao TEXT;
