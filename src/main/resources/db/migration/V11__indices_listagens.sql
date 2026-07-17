-- Colunas usadas em WHERE/ORDER BY pelos finders dos repositories, sem índice até aqui (FK não ganha índice
-- automático no Postgres). Composto (retornou, data_lancamento) cobre tanto o filtro de sobras pendentes
-- (retornou = false, ordenado por data) quanto o de recalcularAlertas (retornou = true).
CREATE INDEX idx_lancamentos_data_lancamento ON lancamentos (data_lancamento);
CREATE INDEX idx_lancamentos_equipe_id ON lancamentos (equipe_id);
CREATE INDEX idx_lancamentos_retornou_data ON lancamentos (retornou, data_lancamento);

CREATE INDEX idx_alertas_data_alerta ON alertas (data_alerta);
CREATE INDEX idx_alertas_equipe_id ON alertas (equipe_id);
CREATE INDEX idx_alertas_lancamento_id ON alertas (lancamento_id);
CREATE INDEX idx_alertas_nivel ON alertas (nivel);

-- Usado pelo @BatchSize de Equipe.membros (WHERE equipe_id IN (...)) e pela leitura de membros por equipe.
CREATE INDEX idx_equipe_membros_equipe_id ON equipe_membros (equipe_id);

CREATE INDEX idx_equipes_ativa ON equipes (ativa);
