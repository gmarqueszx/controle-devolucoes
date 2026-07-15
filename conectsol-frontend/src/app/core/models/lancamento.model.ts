export interface Inversor {
  kw: number;
  quantidade: number;
}

export interface MediaCaboUso {
  tipoSistema: string;
  solo: boolean;
  amostras: number;
  mediaSolarVermPorString: number | null;
  mediaSolarPretoPorString: number | null;
  mediaHeprTotal: number | null;
}

export interface Lancamento {
  id: number;
  equipeId: number;
  montador: string;
  eletricista: string;
  ajudante: string;
  dataLancamento: string; // yyyy-MM-dd
  cliente: string;
  sistemas: number;
  observacoes: string;
  criadoPor: string;

  retornou: boolean | null;
  tipoSistema: string | null;
  solo: boolean | null;
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
  ajusteFinoVerm: number | null;
  ajusteFinoPreto: number | null;
  ajusteFinoHepr: number | null;
  localizacaoSobra: string | null;
  diasParado: number | null;
}

export interface LancamentoRequest {
  montador: string;
  eletricista?: string;
  ajudante?: string;
  dataLancamento: string;
  cliente: string;
  sistemas: number;
  observacoes: string;
  criadoPor: string;

  retornou?: boolean;
  tipoSistema?: string;
  solo?: boolean;
  placas?: number;
  inversores?: Inversor[];

  caboSolarVermDevolvido?: number;
  caboSolarPretoDevolvido?: number;
  caboHeprDevolvido?: number;

  qtdMateriaisEnviados?: number;
  qtdMateriaisDivergentes?: number;
  fotoSobrasGrupo?: boolean;
  ajusteFinoVerm?: number;
  ajusteFinoPreto?: number;
  ajusteFinoHepr?: number;
  localizacaoSobra?: string;
}
