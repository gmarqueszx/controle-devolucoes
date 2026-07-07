export interface Alerta {
  id: number;
  equipeId: number;
  montador: string;
  eletricista: string;
  dataAlerta: string; // yyyy-MM-dd
  descricao: string;
  nivel: 'ALTO' | 'MEDIO' | 'LEVE';
  status: 'ABERTO' | 'RESOLVIDO' | 'JUSTIFICADO';
  statusOriginal: string;
}

export interface AlertaRequest {
  equipeId: number;
  lancamentoId?: number;
  dataAlerta: string;
  descricao: string;
  statusOriginal: string;
}
