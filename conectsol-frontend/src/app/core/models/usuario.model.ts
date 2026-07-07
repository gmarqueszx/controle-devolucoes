export type Perfil = 'ADMIN' | 'VIEWER';

export interface Usuario {
  nome: string;
  email: string;
  perfil: Perfil;
}

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface LoginResponse {
  token: string;
  nome: string;
  email: string;
  perfil: Perfil;
}
