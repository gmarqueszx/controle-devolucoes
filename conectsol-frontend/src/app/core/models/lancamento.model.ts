export interface Lancamento {
  id: number;
  equipeId: number;
  montador: string;
  eletricista: string;
  dataLancamento: string; // yyyy-MM-dd
  cliente: string;
  sistemas: number;
  observacoes: string;
  criadoPor: string;
}

export interface LancamentoRequest {
  equipeId: number;
  dataLancamento: string;
  cliente: string;
  sistemas: number;
  observacoes: string;
  criadoPor: string;
}
