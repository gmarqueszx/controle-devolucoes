export interface RelatorioEquipe {
  montador: string;
  eletricista: string;
  ajudante: string;
  alto: number;
  medio: number;
  leve: number;
  totalAlertas: number;
  pontos: number;
  sistemas: number;
  indiceAlertasPorSistema: number;
}

export interface RelatorioColaborador {
  nome: string;
  alto: number;
  medio: number;
  leve: number;
  totalAlertas: number;
  pontos: number;
}

export interface TendenciaMensal {
  mes: string; // "Jan/26"
  alto: number;
  medio: number;
  leve: number;
  total: number;
}

export interface DashboardStats {
  sistemasPeriodo: number;
  percentualAproveitamento100: number | null;
  sobrasPendentes: number;
  alertasAltoPeriodo: number;
}
