export interface Equipe {
  id: number;
  montador: string;
  eletricista: string;
  ativa: boolean;
}

export interface EquipeRequest {
  montador: string;
  eletricista: string;
}
