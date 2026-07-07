import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Lancamento, LancamentoRequest } from '../models/lancamento.model';

@Injectable({ providedIn: 'root' })
export class LancamentoService {
  private readonly apiUrl = `${environment.apiUrl}/lancamentos`;

  constructor(private readonly http: HttpClient) {}

  listar(de: string, ate: string, equipeId?: number): Observable<Lancamento[]> {
    let params = new HttpParams().set('de', de).set('ate', ate);
    if (equipeId) {
      params = params.set('equipeId', equipeId);
    }
    return this.http.get<Lancamento[]>(this.apiUrl, { params });
  }

  buscarPorId(id: number): Observable<Lancamento> {
    return this.http.get<Lancamento>(`${this.apiUrl}/${id}`);
  }

  criar(request: LancamentoRequest): Observable<Lancamento> {
    return this.http.post<Lancamento>(this.apiUrl, request);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
