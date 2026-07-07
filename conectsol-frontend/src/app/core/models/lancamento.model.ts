export interface Inversor {
  kw: number;
  quantidade: number;
}

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

  retornou: boolean | null;
  tipoSistema: string | null;
  telhado: string | null;
  placas: number | null;
  strings: number | null;
  inversores: Inversor[];

  caboSolarVermEnviado: number | null;
  caboSolarPretoEnviado: number | null;
  caboHeprEnviado: number | null;
  caboSolarVermDevolvido: number | null;
  caboSolarPretoDevolvido: number | null;
  caboHeprDevolvido: number | null;
  caboSolarVermUsado: number | null;
  caboSolarPretoUsado: number | null;
  caboHeprUsado: number | null;

  qtdMateriaisEnviados: number | null;
  qtdMateriaisDivergentes: number | null;
  aproveitamento: number | null;
  fotoSobrasGrupo: boolean | null;
  ajusteFino: number | null;
}

export interface LancamentoRequest {
  equipeId: number;
  dataLancamento: string;
  cliente: string;
  sistemas: number;
  observacoes: string;
  criadoPor: string;

  retornou?: boolean;
  tipoSistema?: string;
  telhado?: string;
  placas?: number;
  inversores?: Inversor[];

  caboSolarVermDevolvido?: number;
  caboSolarPretoDevolvido?: number;
  caboHeprDevolvido?: number;

  qtdMateriaisEnviados?: number;
  qtdMateriaisDivergentes?: number;
  fotoSobrasGrupo?: boolean;
  ajusteFino?: number;
}
