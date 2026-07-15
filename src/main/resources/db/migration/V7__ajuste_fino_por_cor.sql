ALTER TABLE lancamentos
    ADD COLUMN ajuste_fino_verm DOUBLE PRECISION,
    ADD COLUMN ajuste_fino_preto DOUBLE PRECISION,
    ADD COLUMN ajuste_fino_hepr DOUBLE PRECISION;

UPDATE lancamentos SET ajuste_fino_verm = ajuste_fino,
                        ajuste_fino_preto = ajuste_fino,
                        ajuste_fino_hepr = ajuste_fino
WHERE ajuste_fino IS NOT NULL;

ALTER TABLE lancamentos DROP COLUMN ajuste_fino;
