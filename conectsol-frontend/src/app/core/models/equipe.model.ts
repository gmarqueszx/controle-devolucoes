export type FuncaoEquipe = 'MONTADOR' | 'ELETRICISTA' | 'AJUDANTE';

export interface EquipeMembro {
  nome: string;
  funcao: FuncaoEquipe;
}

export interface Equipe {
  id: number;
  montador: string;
  eletricista: string;
  ajudante: string;
  membros: EquipeMembro[];
  ativa: boolean;
}

export interface EquipeRequest {
  membros: EquipeMembro[];
}
