export interface Alerta {
  id: number;
  equipeId: number;
  montador: string;
  eletricista: string;
  cliente: string | null;
  dataAlerta: string; // yyyy-MM-dd
  descricao: string;
  nivel: 'ALTO' | 'MEDIO' | 'LEVE';
  status: 'ABERTO' | 'RESOLVIDO' | 'JUSTIFICADO';
  statusOriginal: string;
  origem: string | null;
  confirmadoDesvioEm: string | null;
  confirmadoDesvioPor: string | null;
  justificativaConfirmacao: string | null;
}

export interface AlertaRequest {
  equipeId: number;
  lancamentoId?: number;
  dataAlerta: string;
  descricao: string;
  statusOriginal: string;
}

export interface ConfirmarDesvioRequest {
  justificativa: string;
  confirmadoPor?: string;
}
