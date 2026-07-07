export interface RelatorioEquipe {
  montador: string;
  eletricista: string;
  alto: number;
  medio: number;
  leve: number;
  totalAlertas: number;
  sistemas: number;
  indiceAlertasPorSistema: number;
}

export interface TendenciaMensal {
  mes: string; // "Jan/26"
  alto: number;
  medio: number;
  leve: number;
  total: number;
}

export interface DashboardStats {
  alertasHoje: number;
  alertasSemana: number;
  alertasMes: number;
  equipesAtivas: number;
  sistemasMes: number;
}
