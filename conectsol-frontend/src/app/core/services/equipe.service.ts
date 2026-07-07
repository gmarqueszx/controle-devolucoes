import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Equipe, EquipeRequest } from '../models/equipe.model';

@Injectable({ providedIn: 'root' })
export class EquipeService {
  private readonly apiUrl = `${environment.apiUrl}/equipes`;

  constructor(private readonly http: HttpClient) {}

  listar(): Observable<Equipe[]> {
    return this.http.get<Equipe[]>(this.apiUrl);
  }

  buscarPorId(id: number): Observable<Equipe> {
    return this.http.get<Equipe>(`${this.apiUrl}/${id}`);
  }

  criar(request: EquipeRequest): Observable<Equipe> {
    return this.http.post<Equipe>(this.apiUrl, request);
  }

  atualizar(id: number, request: EquipeRequest): Observable<Equipe> {
    return this.http.put<Equipe>(`${this.apiUrl}/${id}`, request);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
