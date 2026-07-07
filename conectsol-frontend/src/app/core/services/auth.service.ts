import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse, Perfil, Usuario } from '../models/usuario.model';

const CHAVE_TOKEN = 'conectsol_token';
const CHAVE_USUARIO = 'conectsol_usuario';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;

  constructor(private readonly http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, request).pipe(
      tap((response) => {
        localStorage.setItem(CHAVE_TOKEN, response.token);
        localStorage.setItem(
          CHAVE_USUARIO,
          JSON.stringify({ nome: response.nome, email: response.email, perfil: response.perfil })
        );
      })
    );
  }

  logout(): void {
    localStorage.removeItem(CHAVE_TOKEN);
    localStorage.removeItem(CHAVE_USUARIO);
  }

  getToken(): string | null {
    return localStorage.getItem(CHAVE_TOKEN);
  }

  getUsuario(): Usuario | null {
    const usuario = localStorage.getItem(CHAVE_USUARIO);
    return usuario ? (JSON.parse(usuario) as Usuario) : null;
  }

  getPerfil(): Perfil | null {
    return this.getUsuario()?.perfil ?? null;
  }

  isAdmin(): boolean {
    return this.getPerfil() === 'ADMIN';
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
